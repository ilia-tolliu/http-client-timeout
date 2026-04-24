plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":lib-apache-http-client-impl"))
    implementation(project(":lib-jdk-http-client-impl"))
    implementation("ch.qos.logback:logback-classic:1.5.32")
}

application {
    mainClass = "example.client.app.ClientApp"
}
