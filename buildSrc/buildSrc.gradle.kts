plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.4.0"))
    implementation("com.android.tools.build:gradle:4.0.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.28.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
