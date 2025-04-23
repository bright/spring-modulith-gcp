import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.model.Active
import java.time.LocalDate

plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("plugin.spring") version libs.versions.kotlin.get() apply false
    id("java-library")
    alias(libs.plugins.jreleaser)
}

allprojects {
    group = "pl.brightinventions.spring.modulith"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    if (project.name != "examples") {
        java {
            withJavadocJar()
            withSourcesJar()
        }

        configure<PublishingExtension> {
            publications {
                register<MavenPublication>("maven") {
                    from(components["java"])
                    pom {
                        name.set(project.name)
                        description.set(project.description ?: project.name)
                        url.set("https://github.com/bright/spring-modulith-gcp")
                        licenses {
                            license {
                                name.set("MIT")
                                url.set("https://github.com/bright/spring-modulith-gcp/blob/main/LICENSE")
                            }
                        }
                        developers {
                            developer {
                                id.set("bright")
                                name.set("Bright Inventions")
                                email.set("info@brightinventions.pl")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/bright/spring-modulith-gcp.git")
                            developerConnection.set("scm:git:ssh://github.com:bright/spring-modulith-gcp.git")
                            url.set("https://github.com/bright/spring-modulith-gcp")
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
                }
            }
        }
    }
}

configure<org.jreleaser.gradle.plugin.JReleaserExtension> {
    gitRootSearch = true
    project {
        description = "Spring Modulith GCP integration libraries"
        authors = listOf("Bright Inventions")
        license = "MIT"
        links {
            homepage = "https://github.com/bright/spring-modulith-gcp"
            bugTracker = "https://github.com/bright/spring-modulith-gcp/issues"
            contact = "https://brightinventions.pl"
        }
        inceptionYear = "2025"
        vendor = "Bright Inventions"
        copyright = "Copyright (c) ${LocalDate.now().year} Bright Inventions Sp. z o.o."
    }

    release {
        github {
            commitAuthor {
                name = "Bright Inventions"
                email = "info@brightinventions.pl"
            }
        }
    }

    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                register("sonatype") { // https://jreleaser.org/guide/latest/examples/maven/maven-central.html#_portal_publisher_api
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    subprojects.filter { it.name != "examples" }.forEach { project ->
                        stagingRepository(project.layout.buildDirectory.dir("staging-deploy").get().asFile.path)
                    }

                }
            }
        }
    }
}
