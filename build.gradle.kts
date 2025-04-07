plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

group = "com.kyant"
version = getGitCommitHash()

android {
    namespace = "com.kyant.taglib"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildToolsVersion.get()
    ndkVersion = libs.versions.android.ndkVersion.get()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
        ndk {
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.31.6"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    //noinspection WrongGradleMethod
    kotlin {
        jvmToolchain(21)
        explicitApi()
    }
    lint {
        checkReleaseBuilds = false
    }
    publishing {
        singleVariant("release") {}
    }
}

dependencies {
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
}

afterEvaluate {
    publishing {
        publications {
            //noinspection WrongGradleMethod
            register("mavenRelease", MavenPublication::class) {
                groupId = "com.kyant"
                artifactId = "taglib"
                version = getGitCommitHash()
                from(components["release"])
            }
        }
        repositories {
            maven {
                name = "GitHubPages"
                url = uri(layout.buildDirectory.dir("gh-pages-repo"))
            }
        }
    }
}

fun getGitCommitHash(): String {
    return System.getenv("GITHUB_SHA")?.take(7) ?: "local-SNAPSHOT"
}