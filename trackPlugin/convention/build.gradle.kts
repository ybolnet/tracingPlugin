plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "com.ybo.trackingplugin"
version = "0.0.1"

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

dependencies{
    compileOnly("com.android.tools.build:gradle:7.4.2")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}

gradlePlugin {
    plugins {
        register("trackingPlugin") {
            id = "com.ybo.trackingplugin.plugin"
            implementationClass = "com.ybo.trackingplugin.TrackingPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
