
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*

fun Project.propertiesFromFile(fileName: String): Properties {
    val loadedFile = file(fileName)
    return Properties().apply {
        if (loadedFile.isFile) {
            FileInputStream(loadedFile).use {
                load(it)
            }
        }
    }
}

fun File.propertiesFromFile(): Properties {
    val loadedFile = this
    return Properties().apply {
        if (loadedFile.isFile) {
            FileInputStream(loadedFile).use {
                load(it)
            }
        }
    }
}