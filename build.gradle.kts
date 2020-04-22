plugins {
    application
    kotlin("jvm") version "1.3.71"
}

group = "com.github.trueddd"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    val ktorVersion = "1.3.2"
    val kotlinVersion = "1.3.71"
    val logbackVersion = "1.2.3"

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

sourceSets {
    kotlin.sourceSets["main"].kotlin.srcDirs("src")
    kotlin.sourceSets["test"].kotlin.srcDirs("test")
    kotlin.sourceSets["main"].kotlin.srcDirs("resources")
}

tasks.register("stage") {
    dependsOn(tasks.named("clean"), tasks.named("build"))
    mustRunAfter(tasks.named("clean"))
}

tasks.register<Jar>("fatJar") {
    manifest {
        attributes(
                mapOf(
                        "Main-Class" to application.mainClassName,
                        "Class-Path" to configurations.compile
                )
        )
    }
    archiveBaseName.set("${project.name}-all")
    from(Callable { configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) } })
}
