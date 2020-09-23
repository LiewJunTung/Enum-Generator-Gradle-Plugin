plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}


group = "com.liewjuntung"
version = "0.1.1"

gradlePlugin {
    plugins {
        create("enum_generator") {
            id = "com.liewjuntung.enum_generator"
            implementationClass = "com.liewjuntung.enum_generator.EnumGeneratorPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/LiewJunTung/Enum-Generator-Gradle-Plugin/"
    vcsUrl = "https://github.com/LiewJunTung/Enum-Generator-Gradle-Plugin/"

    // tags and description can be set for the whole bundle here, but can also
    // be set / overridden in the config for specific plugins
    description = "A plugin to generate Kotlin and Swift enum classes for Flutter binding code."

    (plugins) {

        // first plugin
        "enum_generator" {
            // id is captured from java-gradle-plugin configuration
            displayName = "A plugin to generate Kotlin and Swift enum classes for Flutter binding code."
            tags = listOf("kotlin", "swift", "flutter")
        }
    }
}


repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.eclipse.jgit:org.eclipse.jgit:2.2.0.201212191850-r")
    implementation("com.squareup:kotlinpoet:1.6.0")
    implementation("io.outfoxx:swiftpoet:1.0.0")
    implementation("com.google.code.gson:gson:2.8.6")

    testImplementation("junit", "junit", "4.12")
    testImplementation(gradleTestKit())

}
