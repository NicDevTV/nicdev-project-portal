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

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    val props =
        mapOf(
            "pluginName" to providers.gradleProperty("pluginName").get(),
            "pluginVersion" to providers.gradleProperty("pluginVersion").get(),
            "pluginMainClass" to providers.gradleProperty("pluginMainClass").get(),
            "pluginApiVersion" to providers.gradleProperty("pluginApiVersion").get(),
            "pluginAuthors" to providers.gradleProperty("pluginAuthors").get(),
            "pluginDescription" to providers.gradleProperty("pluginDescription").get(),
        )
    inputs.properties(props)
    filesMatching("plugin.yml") {
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
        exclude { it.moduleGroup != "org.bstats" }
    }

    relocate("org.bstats", "${project.group}.bstats")
}
