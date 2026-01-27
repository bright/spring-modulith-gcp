plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.cloud.gcp.dependencies))

    api(libs.spring.modulith.events.api)
    implementation(libs.spring.modulith.events.core)

    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.gcp.starter.data.datastore)
    implementation(libs.spring.retry)
    implementation(libs.spring.aspects)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.google.cloud.firestore.admin)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.modulith.events.jackson)

    testFixturesImplementation(libs.spring.boot.starter.test)
    testFixturesImplementation(libs.testcontainers.core)
    testFixturesImplementation(libs.testcontainers.gcloud)

    testImplementation(libs.testcontainers.junit.jupiter)

    testImplementation(libs.kotest.assertions.core)
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
