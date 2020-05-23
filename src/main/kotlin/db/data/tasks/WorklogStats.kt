package db.data.tasks

data class WorklogStats(
    val worklogs: List<WorklogStatsItem>,
    val totalDaysLogged: Int,
    val totalHoursLogged: Int,
    val totalMinutesLogged: Int
)