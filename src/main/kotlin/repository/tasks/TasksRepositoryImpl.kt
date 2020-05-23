package repository.tasks

import db.dao.*
import db.data.User
import db.data.tasks.*
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toTask
import utils.toUser
import utils.toWorklog
import utils.toWorklogStatsItem
import java.time.LocalDateTime

class TasksRepositoryImpl(database: Database) : BaseRepository(database), TasksRepository {

    override fun getTasksBySprint(sprintId: Int): List<Task> = query {
        val tasks = getTasksJoin().select { Tasks.sprintId eq sprintId }
        val taskIds = tasks.map { it[Tasks.id].toInt() }
        val executors = getExecutorsMap(taskIds)
        val worklogs = getWorklogsMap(taskIds)
        return@query tasks.map {
            it.toTask(
                executors[it[Tasks.id].toInt()],
                worklogs[it[Tasks.id].toInt()]
            )
        }
    }

    override fun getTasksByEpic(epicId: Int): List<Task> = query {
        val sprints = Sprints.select { Sprints.epicId eq epicId }.map { it[Sprints.id].toInt() }
        val tasks = getTasksJoin().select { Tasks.sprintId inList sprints }
        val ids = tasks.map { it[Tasks.id].toInt() }
        val executors = getExecutorsMap(ids)
        val worklogs = getWorklogsMap(ids)
        return@query tasks.map {
            it.toTask(
                executors[it[Tasks.id].toInt()],
                worklogs[it[Tasks.id].toInt()]
            )
        }
    }

    override fun getTaskByProject(projectId: Int): List<Task> = query {
        val epics = Epics.select { Epics.projectId eq projectId }.map { it[Epics.id].toInt() }
        val sprints = Sprints.select { Sprints.epicId inList epics }.map { it[Sprints.id].toInt() }
        val tasks = getTasksJoin().select { Tasks.sprintId inList sprints }
        val ids = tasks.map { it[Tasks.id].toInt() }
        val executors = getExecutorsMap(ids)
        val worklogs = getWorklogsMap(ids)
        return@query tasks.map {
            it.toTask(
                executors[it[Tasks.id].toInt()],
                worklogs[it[Tasks.id].toInt()]
            )
        }
    }

    override fun getTaskById(taskId: Int): Task? = query {
        val task = getTasksJoin().select { Tasks.id eq taskId }.singleOrNull() ?: return@query null
        val executors = getExecutorsMap(listOf(task[Tasks.id].toInt()))
        val worklogs = getWorklogsMap(listOf(task[Tasks.id].toInt()))
        return@query task.toTask(
            executors[task[Tasks.id].toInt()],
            worklogs[task[Tasks.id].toInt()]
        )
    }

    override fun createTask(userId: Int, body: TaskCreateBody): Task? = query {
        val inserted = Tasks.insert {
            it[name] = body.name
            it[description] = body.description
            it[sprintId] = body.sprintId
            it[createdAt] = LocalDateTime.now()
            it[creatorId] = userId
            it[stateId] = 1
        }.resultedValues?.firstOrNull()?.let {
            it[Tasks.id].toInt()
        } ?: return@query null
        body.executors?.forEach { executor ->
            TaskExecutors.insert {
                it[taskId] = inserted
                it[executorId] = executor
            }
        }
        return@query getTaskById(inserted)
    }

    override fun modifyTask(taskId: Int, updateBody: TaskUpdateBody): Task? = query {
        if (updateBody.name != null
            || updateBody.description != null
            || updateBody.stateId != null
            || updateBody.sprintId != null) {
            val updated = Tasks.update({ Tasks.id eq taskId }) {
                updateBody.name?.let { newValue -> it[name] = newValue }
                updateBody.description?.let { newValue -> it[description] = newValue }
                updateBody.stateId?.let { newValue -> it[stateId] = newValue }
                updateBody.sprintId?.let { newValue -> it[sprintId] = newValue }
            }
            if (updated < 1) {
                rollback()
                return@query null
            }
        }
        val addExecutors = updateBody.addExecutors ?: emptyList()
        val removeExecutors = updateBody.removeExecutors ?: emptyList()
        val add = addExecutors - removeExecutors
        val remove = removeExecutors - addExecutors
        add.forEach { executor ->
            TaskExecutors.insert {
                it[TaskExecutors.taskId] = taskId
                it[executorId] = executor
            }
        }
        if (remove.isNotEmpty()) {
            TaskExecutors.deleteWhere {
                (TaskExecutors.taskId eq taskId) and (TaskExecutors.executorId inList remove)
            }
        }
        return@query getTaskById(taskId)
    }

    override fun getUserRightsOnTask(user: User, taskId: Int): Int = query {
        val task = getTaskById(taskId) ?: return@query -1
        if (task.creator.id == user.id) {
            return@query 300
        }
        return@query if (task.executors.any { it.id == user.id }) 100 else -1
    }

    override fun deleteTask(taskId: Int): Boolean = query {
        return@query Tasks.deleteWhere { Tasks.id eq taskId } > 0
    }

    override fun getWorklogById(worklogId: Int): Worklog? = query {
        return@query (WorkLogs leftJoin Users)
            .select { WorkLogs.id eq worklogId }.singleOrNull()?.toWorklog()
    }

    override fun isUserWorklog(userId: Int, worklogId: Int): Boolean = query {
        return@query getWorklogById(worklogId)?.reporter?.id == userId
    }

    override fun createWorklog(userId: Int, taskId: Int, createBody: WorklogCreateBody): Task? = query {
        WorkLogs.insert {
            it[WorkLogs.userId] = userId
            it[WorkLogs.taskId] = taskId
            it[comment] = createBody.comment
            it[startedAt] = createBody.workStartedAt
            it[finishedAt] = createBody.workFinishedAt
        }.resultedValues?.firstOrNull()?.let {
            it[WorkLogs.id].toInt()
        } ?: return@query null
        return@query getTaskById(taskId)
    }

    override fun modifyWorklog(worklogId: Int, updateBody: WorklogUpdateBody): Task? = query {
        val updated = WorkLogs.update({ WorkLogs.id eq worklogId }) {
            updateBody.comment?.let { newValue -> it[comment] = newValue }
            updateBody.workStartedAt?.let { newValue -> it[startedAt] = newValue }
            updateBody.workFinishedAt?.let { newValue -> it[finishedAt] = newValue }
        }
        if (updated < 1) {
            rollback()
            return@query null
        }
        val taskId = WorkLogs.select { WorkLogs.id eq worklogId }
            .singleOrNull()?.let { it[WorkLogs.taskId].toInt() } ?: return@query null
        return@query getTaskById(taskId)
    }

    override fun deleteWorklog(worklogId: Int): Boolean = query {
        return@query WorkLogs.deleteWhere { WorkLogs.id eq worklogId } > 0
    }

    override fun getUserWorklogStatsByTask(userId: Int, taskId: Int): List<WorklogStatsItem> = query {
        return@query (WorkLogs leftJoin Tasks leftJoin Sprints leftJoin Epics leftJoin Projects)
            .select { (WorkLogs.userId eq userId) and (WorkLogs.taskId eq taskId) }
            .map { it.toWorklogStatsItem() }
    }

    override fun getUserWorklogStatsBySprint(userId: Int, sprintId: Int): List<WorklogStatsItem> = query {
        val tasks = Tasks.select { Tasks.sprintId eq sprintId }.map { it[Tasks.id].toInt() }
        return@query (WorkLogs leftJoin Tasks leftJoin Sprints leftJoin Epics leftJoin Projects)
            .select { (WorkLogs.userId eq userId) and (WorkLogs.taskId inList tasks) }
            .map { it.toWorklogStatsItem() }
    }

    override fun getUserWorklogStatsByEpic(userId: Int, epicId: Int): List<WorklogStatsItem> = query {
        val sprints = Sprints.select { Sprints.epicId eq epicId }.map { it[Sprints.id].toInt() }
        val tasks = Tasks.select { Tasks.sprintId inList sprints }.map { it[Tasks.id].toInt() }
        return@query (WorkLogs leftJoin Tasks leftJoin Sprints leftJoin Epics leftJoin Projects)
            .select { (WorkLogs.userId eq userId) and (WorkLogs.taskId inList tasks) }
            .map { it.toWorklogStatsItem() }
    }

    override fun getUserWorklogStatsByProject(userId: Int, projectId: Int): List<WorklogStatsItem> = query {
        val epics = Epics.select { Epics.projectId eq projectId }.map { it[Epics.id].toInt() }
        val sprints = Sprints.select { Sprints.epicId inList epics }.map { it[Sprints.id].toInt() }
        val tasks = Tasks.select { Tasks.sprintId inList sprints }.map { it[Tasks.id].toInt() }
        return@query (WorkLogs leftJoin Tasks leftJoin Sprints leftJoin Epics leftJoin Projects)
            .select { (WorkLogs.userId eq userId) and (WorkLogs.taskId inList tasks) }
            .map { it.toWorklogStatsItem() }
    }

    override fun getUserWorklogStats(userId: Int): List<WorklogStatsItem> = query {
        return@query (WorkLogs leftJoin Tasks leftJoin Sprints leftJoin Epics leftJoin Projects)
            .select { WorkLogs.userId eq userId }
            .map { it.toWorklogStatsItem() }
    }

    private fun getTasksJoin() = Tasks leftJoin Sprints leftJoin Users leftJoin TaskStates

    private fun getExecutorsMap(taskIds: List<Int>): Map<Int, List<User>> {
        return (TaskExecutors leftJoin Users)
            .select { TaskExecutors.taskId inList taskIds }
            .groupBy { it[TaskExecutors.taskId].toInt() }
            .mapValues { entry -> entry.value.map { it.toUser() } }
    }

    private fun getWorklogsMap(taskIds: List<Int>): Map<Int, List<Worklog>> {
        return (WorkLogs leftJoin Users)
            .select { WorkLogs.taskId inList taskIds }
            .groupBy { it[WorkLogs.taskId].toInt() }
            .mapValues { entry -> entry.value.map { it.toWorklog() } }
    }
}