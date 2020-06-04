plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.3.72"))
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.28.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
