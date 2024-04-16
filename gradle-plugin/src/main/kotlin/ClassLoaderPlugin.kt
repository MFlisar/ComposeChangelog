import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.math.ceil
import kotlin.math.floor
import com.michaelflisar.composechangelog.ChangelogVersionFormatter

class ClassLoaderPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        // no-op
    }
}

object ChangelogUtils {

    /**
     * uses the formatter to parse the versionInfo to a valid version code and logs the result
     * in the build window
     *
     * @return version code
     */
   fun buildVersionCode(versionInfo: String, formatter: ChangelogVersionFormatter): Int {

        val code = formatter.parseVersion(versionInfo)

        val info1 = "$versionInfo => $code"
        val t1 = (floor((28.0 - info1.length) / 2.0)).toInt().coerceAtLeast(0)
        val t2 = (ceil((28.0 - info1.length) / 2.0)).toInt().coerceAtLeast(0)
        val info2 = " ".repeat(t1) + info1 + " ".repeat(t2)

        println("")
        println("##############################")
        println("#     BUILD VERSION CODE     #")
        println("# -------------------------- #")
        println("#$info2#")
        println("##############################")
        println("")

        return code
    }
}