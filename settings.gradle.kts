rootProject.name = "Twin-Cities-Pride"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        maven { url = uri("https://maven.fabric.io/public") }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }

            if (requested.id.id == "io.fabric") {
                useModule("io.fabric.tools:gradle:${requested.version}")
            }
        }
    }
}
