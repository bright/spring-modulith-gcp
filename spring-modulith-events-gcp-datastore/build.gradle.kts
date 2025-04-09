dependencies {
    api(libs.spring.modulith.events.api)
    implementation(libs.spring.modulith.events.core)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.gcp.starter.data.datastore)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.spring.boot.starter.test)

    // TestContainers dependencies
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:gcloud:1.19.7")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Spring Modulith Events GCP Datastore",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "spring.modulith.events.datastore"
        )
    }
}
