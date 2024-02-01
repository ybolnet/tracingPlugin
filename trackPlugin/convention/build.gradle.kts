plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
}

group = "io.github.ybolnet"
version = "0.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.4.2")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}

gradlePlugin {
    plugins {
        register("trackingPlugin") {
            id = "io.github.ybolnet"
            implementationClass = "com.ybo.trackingplugin.TrackingPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("TracePlugin") {
                groupId = "io.github.ybolnet"
                artifactId = "traceplugin"
                version = "0.0.2"
                from(components["java"])

                pom {
                    name.set("TracePlugin")
                    description.set("gradle plugin for tracing")

                    // ... Additional pom configuration
                }
            }
        }

        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = "CyrilleTelmer"
                    password = "Cracotte123&Jira"
                }
            }
        }

        signing {
            // useGpgCmd()
            // useGpgKeyChain()
            sign(publishing.publications)
            // useGpgKeyChain()
        }
    }
}
