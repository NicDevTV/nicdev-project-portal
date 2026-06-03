import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.UUID

plugins {
    java
    id("com.diffplug.spotless") version "6.25.0"
    id("com.gradleup.shadow") version "9.3.1"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paperApiVersion").get()}")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    val experimentalBuild = project.findProperty("experimentalBuild")?.toString()?.toBoolean() ?: false
    val buildId = (System.getenv("GITHUB_SHA") ?: UUID.randomUUID().toString().replace("-", "")).take(7)
    val props =
        mapOf(
            "pluginName" to providers.gradleProperty("pluginName").get(),
            "pluginVersion" to providers.gradleProperty("pluginVersion").get(),
            "pluginMainClass" to providers.gradleProperty("pluginMainClass").get(),
            "pluginApiVersion" to providers.gradleProperty("pluginApiVersion").get(),
            "pluginAuthors" to providers.gradleProperty("pluginAuthors").get(),
            "pluginDescription" to providers.gradleProperty("pluginDescription").get(),
            "experimentalBuild" to experimentalBuild.toString(),
            "buildId" to buildId,
        )
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
    filesMatching("build-flags.properties") {
        expand(props)
    }
}

tasks.build {
    dependsOn("spotlessApply")
}

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX

    java {
        target("src/**/*.java")
        licenseHeader(
            """
            /*
             * Copyright (c) 2026 NicDevTV
             * Licensed under the MIT License.
             * https://opensource.org/licenses/MIT
             */
            
            """.trimIndent()
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    dependencies {
        exclude { it.moduleGroup != "org.bstats" && it.moduleGroup != "org.xerial" }
    }

    relocate("org.bstats", "${project.group}.bstats")
}

tasks.register<GradleBuild>("experimentalBuild") {
    group = "build"
    description = "Builds the plugin jar with experimental build flag enabled."
    tasks = listOf("spotlessApply", "clean", "shadowJar")
    startParameter.projectProperties = startParameter.projectProperties + mapOf("experimentalBuild" to "true")
}

tasks.named<ShadowJar>("shadowJar") {
    if (project.findProperty("experimentalBuild")?.toString()?.toBoolean() == true) {
        archiveVersion.set("")
        archiveClassifier.set("experimental")
    }
}
