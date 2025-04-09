dependencies {
    api(libs.spring.modulith.starter.core)
    api(libs.spring.modulith.events.api)
    api(project(":spring-modulith-events-gcp-datastore"))

    runtimeOnly(libs.spring.modulith.events.core)
    runtimeOnly(libs.spring.modulith.events.jackson)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Spring Modulith Starter GCP Datastore",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "spring.modulith.starter.datastore"
        )
    }
}
