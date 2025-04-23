plugins {
    id("org.springframework.boot") version libs.versions.springBoot.get()
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(project(":spring-modulith-starter-gcp-datastore"))
    implementation(libs.spring.cloud.gcp.starter.data.datastore)
    implementation(platform(libs.spring.cloud.gcp.dependencies))

    implementation(libs.kotlin.stdlib)
}
