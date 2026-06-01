import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.smartfinanse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smartfinanse"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Wstrzykiwanie klucza do BuildConfig
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildFeatures {
        // Wymagane w nowszych wersjach AGP, aby włączyć generowanie klasy BuildConfig
        buildConfig = true 
    }
}
