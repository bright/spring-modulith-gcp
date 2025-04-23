dependencies {
    implementation(platform(libs.spring.cloud.gcp.dependencies))

    api(libs.spring.modulith.starter.core)
    api(libs.spring.modulith.events.api)
    api(project(":spring-modulith-events-gcp-datastore"))
    api(project(":spring-gcp-datastore"))

    runtimeOnly(libs.spring.modulith.events.core)
    runtimeOnly(libs.spring.modulith.events.jackson)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    testImplementation(testFixtures(project(":spring-modulith-events-gcp-datastore")))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.web) // just to have a default ObjectMapper configured
    testImplementation(libs.spring.modulith.events.core)
    testImplementation(libs.spring.cloud.gcp.starter.data.datastore)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.jackson.module.kotlin)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Spring Modulith Starter GCP Datastore",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "pl.brightinventions.spring.modulith.starter.datastore"
        )
    }
}
