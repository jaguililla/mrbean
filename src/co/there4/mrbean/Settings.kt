package co.there4.mrbean

import kotlin.*
import kotlin.jvm.*

import java.util.logging.Logger.getLogger

import java.util.ArrayList
import java.util.Scanner
import javax.swing.*

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls

class Settings : Configurable {
    internal var templates: MutableList<TemplateArea> = ArrayList()

    @Nls override fun getDisplayName() = DISPLAY_NAME

    override fun getHelpTopic() = null

    override fun createComponent(): JComponent {
        val pnlSettings = JPanel()
        pnlSettings.layout = BoxLayout(pnlSettings, BoxLayout.PAGE_AXIS)
        listOf("equals", "get", "hashCode", "set", "toString", "with").forEach { template ->
            val templateArea = TemplateArea(template)
            pnlSettings.add(templateArea)
            templates.add(templateArea)
        }
        return pnlSettings
    }

    override fun isModified(): Boolean = templates.map { modified(it) }.reduce { a, b -> a || b }

    private fun modified(ta: TemplateArea): Boolean = ta.txtaTemplate.text != getTemplate(ta.key)

    override fun apply() {
        templates.forEach { it.save() }
    }

    override fun reset() {
        templates.forEach (TemplateArea::reset)
    }

    override fun disposeUIResources() {}

    companion object {
        private val LOGGER = getLogger(Settings::class.java.name)

        internal val DISPLAY_NAME = "MrBean"
        internal val TEMPLATE_PREFIX = "template"
        internal val SETTINGS = PropertiesComponent.getInstance()

        private fun getSetting(key: String, defaultValue: String): String {
            val k = "$DISPLAY_NAME.$key"
            val v = SETTINGS.getValue(k, defaultValue)
            LOGGER.info("<<< %s:\n%s\n---\n%s".format(k, defaultValue, v))
            return v
        }

        private fun setSetting(key: String, value: String) {
            val k = "$DISPLAY_NAME.$key"
            LOGGER.info(">>> %s:\n%s".format(k, value))
            SETTINGS.setValue(k, value)
        }

        internal fun setTemplate(key: String, value: String) = setSetting("$TEMPLATE_PREFIX.$key", value)
        internal fun getTemplate(key: String): String = getSetting("$TEMPLATE_PREFIX.$key", loadTemplate(key))
        internal fun loadTemplate(key: String): String {
            val input = GenerateToString::class.java.getResourceAsStream("/$key.vm")
            return Scanner(input).useDelimiter("\\A").next()
        }
    }
}
