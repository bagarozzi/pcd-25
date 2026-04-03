plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application
}

repositories { // Where to search for dependencies
    mavenCentral()
}

dependencies {

}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.pcd.assignment-01.Main")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        showStandardStreams = true
    }
}
