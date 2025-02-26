import java.io.FileInputStream
import java.io.FileWriter
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version "2.0.0"
    kotlin("plugin.compose") version "2.0.0"
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    signingConfigs {
        create("release") {
        }
    }

    compileSdk = 34
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    defaultConfig {
        applicationId = "de.westnordost.streetcomplete.expert"
        minSdk = 21
        targetSdk = 34
        versionCode = 5803
        versionName = "58.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        all {
            isShrinkResources = false
            // don't use proguard-android-optimize.txt, it is too aggressive, it is more trouble than it is worth
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFile("test-proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "IS_GOOGLE_PLAY", "false")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".debug"
            buildConfigField("boolean", "IS_GOOGLE_PLAY", "false")
        }
        create("releaseGooglePlay") {
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "IS_GOOGLE_PLAY", "true")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    bundle {
        language {
            enableSplit = false // because language is selectable in-app
        }
    }

    lint {
        disable += listOf(
            "MissingTranslation", // crowd-contributed translations are incomplete all the time
            "UseCompatLoadingForDrawables" // doesn't make sense for minSdk >= 21
        )
        abortOnError = false
    }
    namespace = "de.westnordost.streetcomplete"
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(keystorePropertiesFile))
    val releaseSigningConfig = android.signingConfigs.getByName("release")
    releaseSigningConfig.storeFile = file(props.getProperty("storeFile"))
    releaseSigningConfig.storePassword = props.getProperty("storePassword")
    releaseSigningConfig.keyAlias = props.getProperty("keyAlias")
    releaseSigningConfig.keyPassword = props.getProperty("keyPassword")
}

repositories {
    google()
    mavenCentral()
}

configurations {
    all {
        // it's already included in Android
        exclude(group = "net.sf.kxml", module = "kxml2")
        exclude(group = "xmlpull", module = "xmlpull")
    }
}

dependencies {
    val mockitoVersion = "3.12.4"

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // tests
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation(kotlin("test"))

    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("org.mockito:mockito-android:$mockitoVersion")
    androidTestImplementation(kotlin("test"))

    // dependency injection
    implementation(platform("io.insert-koin:koin-bom:3.5.6"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-androidx-workmanager")

    // Android stuff
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material:material")
    // Jetpack Compose Previews
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // multiplatform webview (for login via OAuth)
    implementation("io.github.kevinnzou:compose-webview-multiplatform-android:1.9.12")

    // photos
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // settings
    implementation("com.russhwolf:multiplatform-settings:1.1.1")

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.4.0")

    // Date/time
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // scheduling background jobs
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // HTTP Client
    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-cio:2.3.11")
    testImplementation("io.ktor:ktor-client-mock:2.3.11")

    // finding in which country we are for country-specific logic
    implementation("de.westnordost:countryboundaries:2.1")
    // finding a name for a feature without a name tag
    implementation("de.westnordost:osmfeatures:6.1")
    // talking with the OSM API
    implementation("de.westnordost:osmapi-map:3.0")
    implementation("de.westnordost:osmapi-changesets:3.0")
    implementation("de.westnordost:osmapi-notes:3.0")
    implementation("de.westnordost:osmapi-traces:3.1")
    implementation("de.westnordost:osmapi-user:3.0")

    // widgets
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("me.grantland:autofittextview:0.2.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // box2d view
    implementation("org.jbox2d:jbox2d-library:2.2.1.1")

    // sharing presets/settings via QR Code
    implementation("com.google.zxing:core:3.5.3")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation("com.charleskorn.kaml:kaml:0.59.0")

    // map and location
    implementation("com.mapzen.tangram:tangram:0.17.1")

    // opening hours parser
    implementation("de.westnordost:osm-opening-hours:0.1.0")

    // image view that allows zoom and pan
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // faster sqlite library (additional capabilities like R*-tree or json1 not used)
    // writing 25% faster, reading 5% faster than Android 9 built-in sqlite (tested with 3.36.0)
    implementation("com.github.requery:sqlite-android:3.43.0")
    implementation("androidx.sqlite:sqlite:2.3.0")

    // fast json (de)serialization used for database read and write
    implementation("com.squareup.moshi:moshi:1.15.0")

    // sunset-sunrise parser for lit quests
    implementation("com.luckycatlabs:SunriseSunsetCalculator:1.2")

    // diff utils for comparing filters modified by quest settings with original
    implementation("io.github.java-diff-utils:java-diff-utils:4.12")

    // parser GPX
    implementation("com.github.ticofab:android-gpx-parser:2.3.1")
}

/** Localizations that should be pulled from POEditor */
val bcp47ExportLanguages = setOf(
    "ar", "ast", "be", "bg", "bs", "ca", "cs", "da", "de", "el",
    "en", "en-AU", "en-GB", "eo", "es", "eu", "fa", "fi", "fr", "gl", "he", "hr", "hu", "hy",
    "id", "it", "ja", "ko", "lt", "lv", "nb", "no", "nl", "nn", "pl", "pt", "pt-BR",
    "ro", "ru", "sk", "sl", "sr-cyrl", "sr-latn", "sv", "sw", "th", "tr", "uk",
    "zh", "zh-CN", "zh-HK", "zh-TW"
)

// see https://github.com/osmlab/name-suggestion-index/tags for latest version
val nsiVersion = "v6.0.20240702"
// see https://github.com/openstreetmap/id-tagging-schema/releases for latest version
val presetsVersion = "v6.7.3"

val poEditorProjectId = "97843"

tasks.register("updateAvailableLanguages") {
    group = "streetcomplete"
    doLast {
        val fileWriter = FileWriter("$projectDir/src/main/res/raw/languages.yml", false)
        fileWriter.write(bcp47ExportLanguages.joinToString("\n") { "- $it" })
        fileWriter.write("\n")
        fileWriter.close()
    }
}

tasks.register<GetTranslatorCreditsTask>("updateTranslatorCredits") {
    group = "streetcomplete"
    targetFile = "$projectDir/src/main/res/raw/credits_translators.yml"
    languageCodes = bcp47ExportLanguages
    cookie = properties["POEditorCookie"] as String
    phpsessid = properties["POEditorPHPSESSID"] as String
}

tasks.register<UpdatePresetsTask>("updatePresets") {
    group = "streetcomplete"
    version = presetsVersion
    languageCodes = bcp47ExportLanguages
    targetDir = "$projectDir/src/main/assets/osmfeatures/default"
}

tasks.register<UpdateNsiPresetsTask>("updateNsiPresets") {
    group = "streetcomplete"
    version = nsiVersion
    targetDir = "$projectDir/src/main/assets/osmfeatures/brands"
}

// tasks.register<DownloadBrandLogosTask>("downloadBrandLogos") {
//     group = "streetcomplete"
//     version = nsiVersion
//     targetDir = "$projectDir/src/main/assets/osmfeatures/brands"
// }

tasks.register<DownloadAndConvertPresetIconsTask>("downloadAndConvertPresetIcons") {
    group = "streetcomplete"
    version = presetsVersion
    targetDir = "$projectDir/src/main/res/drawable/"
    iconSize = 34
    transformName = { "ic_preset_" + it.replace('-', '_') }
    indexFile = "$projectDir/src/main/java/de/westnordost/streetcomplete/view/PresetIconIndex.kt"
}

tasks.register<UpdateAppTranslationsTask>("updateTranslations") {
    group = "streetcomplete"
    languageCodes = bcp47ExportLanguages
    apiToken = properties["POEditorAPIToken"] as String
    projectId = poEditorProjectId
    targetFiles = { "$projectDir/src/main/res/values-$it/strings.xml" }
}

tasks.register<UpdateAppTranslationCompletenessTask>("updateTranslationCompleteness") {
    group = "streetcomplete"
    languageCodes = bcp47ExportLanguages
    mustIncludeLanguagePercentage = 90
    apiToken = properties["POEditorAPIToken"] as String
    projectId = poEditorProjectId
    targetFiles = { "$projectDir/src/main/res/values-$it/translation_info.xml" }
}

tasks.register<UpdateMapStyleTask>("updateMapStyle") {
    group = "streetcomplete"
    targetDir = "$projectDir/src/main/assets/map_theme/jawg"
    mapStyleBranch = "jawg"
}

tasks.register<UpdateChangelogTask>("updateChangelog") {
    group = "streetcomplete"
    sourceFile = "$rootDir/CHANGELOG.md"
    targetFile = "$projectDir/src/main/res/raw/changelog.html"
}

tasks.register<GenerateMetadataByCountryTask>("generateMetadataByCountry") {
    group = "streetcomplete"
    sourceDir = "$rootDir/res/country_metadata"
    targetDir = "$projectDir/src/main/assets/country_metadata"
}

tasks.register("copyDefaultStringsToEnStrings") {
    doLast {
        File("$projectDir/src/main/res/values/strings.xml")
            .copyTo(File("$projectDir/src/main/res/values-en/strings.xml"), true)
    }
}

// this task is EE only, suggestions are used in the tag editor
tasks.register<GenerateTagSuggestions>("generateTagSuggestions") {
    group = "streetcomplete"
    version = presetsVersion
    targetDir = "$projectDir/src/main/assets/tag_editor"
}
