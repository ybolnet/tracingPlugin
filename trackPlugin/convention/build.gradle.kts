plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.6.10"
}
val ourGroupId = "io.github.ybolnet"
val ourArtifactId = "traceplugin"
val ourVersionArtifact = "0.1.3"
val ourPluginName = "TracePlugin"
val pluginId = ourGroupId
val pluginMainClass = "com.ybo.trackingplugin.TrackingPlugin"
val pluginDesc =
    "gradle plugin allowing to add automatic logs (or other process) at the start of each traced method"
group = ourGroupId
version = ourVersionArtifact

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register(ourPluginName) {
            id = pluginId
            implementationClass = pluginMainClass
            displayName = ourPluginName
            description = pluginDesc
        }
    }
}

val dokkaOutputDir = "$buildDir/dokka"

tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}
val sourcesJar = tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.classes)
    archiveClassifier.set("sources")
    from(java.sourceSets["main"].allSource)
}

publishing {
    afterEvaluate {
        publications {
            create<MavenPublication>(ourPluginName) {
                groupId = ourGroupId
                artifactId = ourArtifactId
                version = ourVersionArtifact
                from(components["java"])
                artifact(javadocJar)
                artifact(sourcesJar)
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }

            withType<MavenPublication>() {

                pom {
                    name.set(ourPluginName)
                    description.set(pluginDesc)
                    url.set("https://github.com/ybolnet/tracingPlugin")
                    licenses {
                        license {
                            name.set("MIT licence")
                            url.set("https://opensource.org/license/mit/")
                        }
                    }

                    developers {
                        developer {
                            id.set("YBOLNET")
                            name.set("Cyrille Telmer")
                            email.set("cyrilletelmer@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/ybolnet/tracingPlugin.git")
                        developerConnection.set("scm:git:ssh://github.com/ybolnet/tracingPlugin.git")
                        url.set("https://github.com/ybolnet/tracingPlugin")
                    }
                }
            }
        }
        signing {
            val signingKeyId: String? = System.getenv("TRACING_SIGNING_KEY_ID")
            val signingKey: String? = System.getenv("TRACING_SIGNING_SECRET_KEY")
            val signingPassword: String? = System.getenv("TRACING_SIGNING_PASSWORD")
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
            sign(publishing.publications[ourPluginName])
        }
    }
    repositories {
        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("TRACING_MAVEN_PUBLISH_USERNAME")
                    password = System.getenv("TRACING_MAVEN_PUBLISH_PASSWORD")
                }
            }
            maven {
                name = "test"
                url = uri("$rootDir/test-repository")
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
