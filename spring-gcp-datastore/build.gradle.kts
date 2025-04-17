dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.gcp.starter.data.datastore)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.gcloud)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Spring GCP Datastore",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "pl.brightinventions.spring.datastore"
        )
    }
}
