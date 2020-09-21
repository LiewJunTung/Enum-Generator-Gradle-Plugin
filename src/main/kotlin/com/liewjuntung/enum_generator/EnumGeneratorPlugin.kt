package com.liewjuntung.enum_generator

import com.google.gson.Gson
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.outfoxx.swiftpoet.FileSpec
import io.outfoxx.swiftpoet.Modifier
import io.outfoxx.swiftpoet.STRING
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class EnumGeneratorPlugin: Plugin<Project> {
    private val gson = Gson()

    override fun apply(project: Project) {
        project.run {
            val extension = extensions.create("enumGenExtension", EnumGeneratorPluginExtension::class.java)
            tasks.register("createFlutterJson") {
                it.doLast {
                    val jsonPath = extension.jsonPath ?: throw GradleException("jsonPath must not be null")
                    val swiftOutputPath = extension.swiftOutputPath ?: throw GradleException("swiftOutputPath must not be null")
                    val kotlinOutputPath = extension.kotlinOutputPath ?: throw GradleException("kotlinOutputPath must not be null")
                    val kotlinPackageName = extension.kotlinOutputPackageName ?: throw GradleException("kotlinPackageName must not be null")
                    val jsonFile = file(jsonPath)
                    if (jsonFile.exists()) {
                        val verifytServiceJson = jsonFile.readText()

                        val data = gson.fromJson(verifytServiceJson, ServiceDataClass::class.java)
                        val className = data.name
                        val methodList = data.methodList

                        val kotlinBuilder = TypeSpec.enumBuilder(className)
                            .primaryConstructor(
                                FunSpec.constructorBuilder()
                                .addParameter("funcName", String::class)
                                .build())
                            .addProperty(
                                PropertySpec.builder("funcName", String::class)
                                .initializer("funcName")
                                .build())

                        val swiftEnumBuilder = io.outfoxx.swiftpoet.TypeSpec
                            .enumBuilder(className)
                            .addModifiers(Modifier.PUBLIC)
                            .addSuperType(STRING)

                        methodList.forEach {methodName ->
                            kotlinBuilder.addEnumConstant(methodName.camelToSnakeCase().toUpperCase(Locale.ROOT), TypeSpec.anonymousClassBuilder()
                                .addSuperclassConstructorParameter("%S", methodName)
                                .build())
                            swiftEnumBuilder.addEnumCase(methodName.camelToSnakeCase().toUpperCase(Locale.ROOT), constant = methodName)
                        }
                        val swiftOutputDirectory = file(swiftOutputPath)
                        val kotlinOutputDirectory = file(kotlinOutputPath)
                        swiftOutputDirectory.mkdirs()
                        kotlinOutputDirectory.mkdirs()
                        FileSpec.builder(className)
                            .addType(swiftEnumBuilder.build())
                            .build()
                            .writeTo(swiftOutputDirectory)
                        com.squareup.kotlinpoet.FileSpec.builder(kotlinPackageName, className)
                            .addType(kotlinBuilder.build())
                            .build()
                            .writeTo(kotlinOutputDirectory)
                    } else {
                        throw GradleException("JSON file at $jsonPath not found!")
                    }
                }
            }
        }
    }



    private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
//    private val snakeRegex = "_[a-zA-Z]".toRegex()

    private fun String.camelToSnakeCase(): String {
        return camelRegex.replace(this) {
            "_${it.value}"
        }.toLowerCase(Locale.ROOT)
    }
}

open class EnumGeneratorPluginExtension {
    var jsonPath: String? = null
    var swiftOutputPath: String? = null
    var kotlinOutputPath: String? = null
    var kotlinOutputPackageName: String? = null
}
