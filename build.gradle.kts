import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")

    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")

    id("com.gradle.build-scan") version "2.3"

    id("gradle-versions")
    id("signing-config")
    id("sentry-io")
}

repositories {
    google()
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")

    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystoreLocation: String by extra
            val keystorePassword: String by extra
            val storeKeyAlias: String by extra
            val aliasKeyPassword: String by extra

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

        versionCode = 123
        versionName = "1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // ignore javax packages in sentry.io jar
    lintOptions {
        disable = setOf("InvalidPackage")
    }
}

dependencies {
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))

    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.emoji:emoji-bundled:1.0.0")
    implementation("androidx.browser:browser:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    implementation("com.google.android.material:material:1.0.0")

    implementation("com.google.firebase:firebase-core:16.0.9")
    implementation("com.google.firebase:firebase-perf:17.0.2")
    implementation("com.google.firebase:firebase-config:17.0.0")

    implementation("com.google.android.gms:play-services-maps:16.1.0")

    implementation("com.jakewharton:process-phoenix:2.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("com.squareup.moshi:moshi:1.8.0")

    implementation("com.google.dagger:dagger:2.23.1")
    kapt("com.google.dagger:dagger-compiler:2.23.1")

    implementation("io.reactivex.rxjava2:rxjava:2.2.9")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding-appcompat-v7:2.2.0")
    implementation("com.f2prateek.rx.preferences2:rx-preferences:2.0.0")

    implementation("com.parse:parse-android:1.17.3")

    implementation("com.github.bumptech.glide:glide:4.9.0")
    kapt("com.github.bumptech.glide:compiler:4.9.0")

    implementation("com.github.rogues-dev:hoard:0.2.0")
    implementation("com.andrewreitz.velcro:velcro-betterviewanimator:1.0.1")
    implementation("ca.barrenechea.header-decor:header-decor:0.2.8")
    implementation("xyz.danoz:recyclerviewfastscroller:0.1.3")
    implementation("pub.devrel:easypermissions:3.0.0")

    releaseImplementation("io.sentry:sentry-android:1.7.16")
    releaseImplementation("org.slf4j:slf4j-nop:1.7.25")

    debugImplementation("com.facebook.stetho:stetho:1.5.1")

    androidTestImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.23.1")

    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.amshove.kluent:kluent:1.49")

    testImplementation("org.threeten:threetenbp:1.4.0") { exclude(group = "com.jakewharton.threetenabp") }
}

kapt {
    useBuildCache = true

    arguments {
        arg("dagger.gradle.incremental", "true")
    }
}


tasks.withType<KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}

// The default "assemble" task only applies to normal variants. Add test variants as well.
tasks.named("assemble").configure {
    dependsOn(android.testVariants.map { it.testedVariant.assembleProvider })
}
