plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.6.10"
}
val ourGroupId = "io.github.ybolnet"
val ourArtifactId = "traceplugin"
val ourVersionArtifact = "0.0.16"
val ourPluginName = "TracePlugin"
val pluginId = ourGroupId
val pluginMainClass = "com.ybo.trackingplugin.TrackingPlugin"
val pluginDesc = "gradle plugin allowing to add automatic logs (or other process) at the start of each traced method"
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
    dependsOn( tasks.classes)
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
                // artifact javadocJar
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
                    properties.set(
                        mapOf(
                            "myProp" to "value",
                            "prop.with.dots" to "anotherValue",
                        ),
                    )
                    licenses {
                        license {
                            name.set("GNU LESSER GENERAL PUBLIC LICENSE 3.0")
                            url.set("http://www.gnu.org/copyleft/lesser.html")
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
            publishing.publications.forEach {
                println("TESTACOMP ${it.name}")
            }
            sign(publishing.publications[ourPluginName])
        }
    }
    repositories {
        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.properties["ossrhUsername"].toString()
                    password = project.properties["ossrhPassword"].toString()
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
