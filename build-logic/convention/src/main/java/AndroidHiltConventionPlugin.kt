import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }
            dependencies {
                "implementation"(catalogLibs("hilt-android"))
                "ksp"(catalogLibs("hilt-compiler"))
            }
        }
    }
}

fun Project.catalogLibs(alias: String) =
    extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
        .named("libs")
        .findLibrary(alias)
        .get()