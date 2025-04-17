import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("plugin.spring") version libs.versions.kotlin.get() apply false
    id("java-library")
    id("org.jreleaser") version "1.17.0" apply false
}

allprojects {
    group = "pl.brightinventions.spring.modulith"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jreleaser")
    apply(plugin = "signing")

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
                        description.set("Spring Modulith GCP integration for ${project.name}")
                        url.set("https://github.com/bright/spring-modulith-gcp")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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

    configure<SigningExtension> {
        sign(extensions.getByType<PublishingExtension>().publications)
        val secretKey = providers.environmentVariable("JRELEASER_GPG_SECRET_KEY")
            .map { it.trim() }
            .orNull
        val password = providers.environmentVariable("JRELEASER_GPG_PASSPHRASE")
            .map { it.trim() }
            .orNull
        useInMemoryPgpKeys(
            secretKey,
            password,
        )
    }

    configure<org.jreleaser.gradle.plugin.JReleaserExtension> {
        gitRootSearch = true
        project {
            description.set("Spring Modulith GCP integration")
            authors.set(listOf("Bright Inventions"))
            license.set("Apache-2.0")
            links {
                homepage.set("https://github.com/bright/spring-modulith-gcp")
            }
            inceptionYear.set("2024")
            vendor.set("Bright Inventions")
        }


        release {
            github {
                repoOwner.set("bright")
                name.set("spring-modulith-gcp")
            }
        }


        checksum {
            individual = true  // Generate checksums for each file
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
                        stagingRepository(layout.buildDirectory.dir("staging-deploy").get().asFile.path)
                    }
                }
            }
        }

    }
}
