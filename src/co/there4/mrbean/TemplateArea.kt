package co.there4.mrbean

import co.there4.mrbean.Settings.Companion.getTemplate
import java.awt.BorderLayout.*

import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

internal class TemplateArea(val key: String) : JPanel() {
    val txtaTemplate: EditorTextField

    init {
        layout = BorderLayout(5, 5)
        add(NORTH, JLabel(key))

        txtaTemplate = EditorTextField(getTemplate(key))
        txtaTemplate.setOneLineMode(false)

        add(CENTER, JBScrollPane(txtaTemplate))
        val defaultTemplate = JButton("Default")
        defaultTemplate.addActionListener { evt -> reset() }
        add(EAST, defaultTemplate)
    }

    fun reset() {
        txtaTemplate.text = Settings.loadTemplate(key)
    }

    fun save() {
        Settings.setTemplate(key, txtaTemplate.text)
    }
}
