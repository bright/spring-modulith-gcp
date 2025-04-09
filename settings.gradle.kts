rootProject.name = "spring-modulith-gcp"

include("spring-modulith-events-gcp-datastore")
include("spring-modulith-starter-gcp-datastore")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("springBoot", "3.4.4")
            version("kotlin", "2.1.20")
            version("springCloudGcp", "6.1.1")
            version("springModulith", "1.4.0-SNAPSHOT")
            version("testcontainers", "1.19.7")

            library("spring-boot-starter", "org.springframework.boot", "spring-boot-starter").versionRef("springBoot")
            library("spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test").versionRef("springBoot")

            library("spring-modulith-events-api", "org.springframework.modulith", "spring-modulith-events-api").versionRef("springModulith")
            library("spring-modulith-events-core", "org.springframework.modulith", "spring-modulith-events-core").versionRef("springModulith")
            library("spring-modulith-events-jackson", "org.springframework.modulith", "spring-modulith-events-jackson").versionRef("springModulith")
            library("spring-modulith-starter-core", "org.springframework.modulith", "spring-modulith-starter-core").versionRef("springModulith")

            library("spring-cloud-gcp-starter-data-datastore", "com.google.cloud", "spring-cloud-gcp-starter-data-datastore").versionRef("springCloudGcp")

            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")

            // TestContainers
            library("testcontainers-core", "org.testcontainers", "testcontainers").versionRef("testcontainers")
            library("testcontainers-junit-jupiter", "org.testcontainers", "junit-jupiter").versionRef("testcontainers")
            library("testcontainers-gcloud", "org.testcontainers", "gcloud").versionRef("testcontainers")

            // BOM imports
            library("spring-cloud-gcp-dependencies", "com.google.cloud", "spring-cloud-gcp-dependencies").versionRef("springCloudGcp")
        }
    }
}
