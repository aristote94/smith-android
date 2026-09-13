import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

/**
 * Signature release.
 *
 * Ordre de résolution :
 *   1. `keystore.properties` à la racine du dépôt (poste de dev, jamais versionné) ;
 *   2. variables d'environnement SMITH_KEYSTORE_* (CI).
 *
 * Si aucune des deux sources n'est disponible, `assembleRelease` reste possible
 * mais produit un APK non signé : le build ne casse jamais faute de secret.
 */
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun secret(key: String, env: String): String? =
    keystoreProperties.getProperty(key) ?: System.getenv(env)

val releaseStoreFile = secret("storeFile", "SMITH_KEYSTORE_FILE")
val releaseStorePassword = secret("storePassword", "SMITH_KEYSTORE_PASSWORD")
val releaseKeyAlias = secret("keyAlias", "SMITH_KEY_ALIAS")
val releaseKeyPassword = secret("keyPassword", "SMITH_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    releaseStoreFile, releaseStorePassword, releaseKeyAlias, releaseKeyPassword,
).all { !it.isNullOrBlank() }

android {
    namespace = "fr.smith.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.smith.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += setOf("fr", "en")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                enableV1Signing = false
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        debug {
            // Debug et release cohabitent sur le même appareil : deux packs d'icônes
            // distincts, donc possibilité de comparer sans désinstaller.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = true
        // Contrôles purement consultatifs sur les versions : ils signalent qu'une
        // version plus récente existe ailleurs, jamais un défaut dans ce code. Avec
        // `warningsAsErrors`, ils font échouer le CI au rythme des publications de
        // Google plutôt qu'au rythme des commits — un build vert devient rouge sans
        // qu'une seule ligne ait changé.
        //
        // OldTargetApi : targetSdk 35 est délibéré. C'est le niveau exigé par Play et
        // le maximum qu'autorise AGP 8.7.3 en compileSdk. À réexaminer en même temps
        // que la montée vers AGP 8.9+, qui débloque compileSdk 36.
        disable += setOf("GradleDependency", "AndroidGradlePluginVersion", "OldTargetApi")
    }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "/META-INF/versions/9/previous-compilation-data.bin",
        )
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
