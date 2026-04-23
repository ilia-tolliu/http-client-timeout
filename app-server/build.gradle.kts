plugins {
    id("buildlogic.java-common-conventions")
    id("io.quarkus") version "3.34.6"
}

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.34.6"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-arc")
}
