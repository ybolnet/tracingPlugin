plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
}

group = "io.github.ybolnet"
version = "0.0.4"
description = "testdesc"

/*
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}*/

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("trackingPlugin") {
            id = "io.github.ybolnet"
            implementationClass = "com.ybo.trackingplugin.TrackingPlugin"
        }
    }
}
/*
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}*/
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("TracePlugin") {
                groupId = "io.github.ybolnet"
                artifactId = "traceplugin"
                version = "0.0.4"
                components.forEach {
                    println("TESTACOMP ${it.name}")
                }

                from(components["kotlin"])

                pom {
                    name.set("TracePlugin")
                    description.set("Gradle plugin for tracing")
                    url.set("https://github.com/ybolnet/tracingPlugin") // Set your project URL
                    developers {
                        developer {

                            name.set("YBOLNET")
                            email.set("yannick.bolner@gmaul.com")
                        }
                        // Add more developers if needed
                    }
                }
                signing {
                    publishing.publications.forEach {
                        println("TESTACOMP ${it.name}")
                    }
                    sign(publishing.publications["TracePlugin"])
                }
            }
        }

        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = "héhé"
                    password = "héhé"
                }
            }
        }
    }
}
