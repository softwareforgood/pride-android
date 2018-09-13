import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application") version "3.3.0-alpha10"
    id("kotlin-android") version "1.2.70"
    id("kotlin-kapt") version "1.2.70"
    id("kotlin-android-extensions") version "1.2.70"
    id("io.fabric") version "1.25.4"
    id("com.google.gms.google-services") version "4.0.1"
    id("com.gradle.build-scan") version "1.15.2"
}

apply(from = "$rootDir/gradle/signing.gradle.kts")

repositories {
    google()
    jcenter()
    maven { url = uri("https://maven.fabric.io/public") }
    maven { url = uri("https://jitpack.io") }
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.2")

    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystoreLocation: String by project
            val keystorePassword: String by project
            val storeKeyAlias: String by project
            val aliasKeyPassword: String by project

            storeFile = file(keystoreLocation)
            storePassword = keystorePassword
            keyAlias = storeKeyAlias
            keyPassword = aliasKeyPassword
        }
    }

    defaultConfig {
        applicationId = "com.softwareforgood.pridefestival"
        minSdkVersion(21)
        targetSdkVersion(28)

        versionCode = 1
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
            val alwaysUpdateBuildId by extra { false }
            val enableCrashlytics by extra { false }
        }
        getByName("release") {
            applicationIdSuffix = ".release"
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    sourceSets {
        getByName("main") { java.srcDirs(file("src/main/kotlin")) }
        getByName("debug") { java.srcDirs(file("src/debug/kotlin")) }
        getByName("release") { java.srcDirs(file("src/release/kotlin")) }

        // Put into testDebug so they only run once instead of for both
        // release and debug build types.
        getByName("testDebug") { java.srcDirs(file("src/test/kotlin")) }
        getByName("androidTest") { java.srcDirs(file("src/androidTest/kotlin")) }
    }

    packagingOptions {
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE.txt")
    }
}

dependencies {
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))

    implementation("androidx.appcompat:appcompat:1.0.0-rc02")
    implementation("androidx.recyclerview:recyclerview:1.0.0-rc02")
    implementation("androidx.emoji:emoji-bundled:1.0.0-rc02")

    implementation("androidx.browser:browser:1.0.0-rc02")

    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha2")

    implementation("com.google.android.material:material:1.0.0-rc02")

    implementation("com.google.firebase:firebase-core:16.0.3")
    implementation("com.google.firebase:firebase-perf:16.1.0")
    implementation("com.google.firebase:firebase-config:16.0.0")

    implementation("com.crashlytics.sdk.android:crashlytics:2.9.5")
    implementation("androidx.core:core-ktx:1.0.0-rc02")
    implementation("com.google.android.gms:play-services-maps:15.0.1")

    implementation("com.jakewharton:process-phoenix:2.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.1.0")
    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("com.squareup.moshi:moshi:1.6.0")

    implementation("com.google.dagger:dagger:2.16")
    kapt("com.google.dagger:dagger-compiler:2.16")

    implementation("io.reactivex.rxjava2:rxjava:2.2.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.rxbinding2:rxbinding-appcompat-v7:2.1.1")
    implementation("com.f2prateek.rx.preferences2:rx-preferences:2.0.0")

    implementation("com.parse:parse-android:1.15.8")

    implementation("com.github.bumptech.glide:glide:4.7.1")
    kapt("com.github.bumptech.glide:compiler:4.7.1")

    implementation("com.github.rogues-dev:hoard:0.2.0")
    implementation("com.andrewreitz.velcro:velcro-betterviewanimator:1.0.1")
    implementation("ca.barrenechea.header-decor:header-decor:0.2.8")
    implementation("xyz.danoz:recyclerviewfastscroller:0.1.3")
    implementation("pub.devrel:easypermissions:1.1.3")

    add("debugImplementation", "com.facebook.stetho:stetho:1.5.0")

    add("androidTestImplementation", kotlin("test", KotlinCompilerVersion.VERSION))
    add("androidTestImplementation", "androidx.test:runner:1.1.0-alpha4")
    add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.1.0-alpha4")
    add("kaptAndroidTest", "com.google.dagger:dagger-compiler:2.16")

    add("testImplementation", kotlin("test", KotlinCompilerVersion.VERSION))
    add("testImplementation", "com.nhaarman:mockito-kotlin:1.5.0")
    add("testImplementation", "org.amshove.kluent:kluent:1.14")

    add("testImplementation", "org.threeten:threetenbp:1.3.6") { exclude(group = "com.jakewharton.threetenabp") }
}

kapt.useBuildCache = true

// The default "assemble" task only applies to normal variants. Add test variants as well.
android.testVariants.all {
    tasks.getByName("assemble").dependsOn(assemble)
}

tasks.withType<Test> {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

tasks.withType<JavaCompile> {
    options.isFork = true
}

tasks["lint"].enabled = false

