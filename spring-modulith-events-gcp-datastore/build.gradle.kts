dependencies {
    api(libs.spring.modulith.events.api)
    implementation(libs.spring.modulith.events.core)

    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.gcp.starter.data.datastore)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.modulith.events.jackson)

    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.gcloud)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.spring.cloud.gcp.starter.data.firestore)
    testImplementation(libs.google.cloud.firestore.admin)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Spring Modulith Events GCP Datastore",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "pl.brightinventions.spring.modulith.events.datastore"
        )
    }
}
