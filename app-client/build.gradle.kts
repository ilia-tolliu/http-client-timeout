plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":lib-apache-http-client-impl"))
    implementation(project(":lib-jdk-http-client-impl"))
}

application {
    mainClass = "org.example.app.App"
}
