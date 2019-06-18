plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.3.31"))
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.21.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
