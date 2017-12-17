package co.there4.mrbean

import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList

import javax.swing.*

internal class FieldSelectionDialog(clazz: PsiClass, title: String) : DialogWrapper(clazz.project) {
    private val fieldList: JBList<Any>
    private val component: LabeledComponent<JPanel>

    val fields: List<PsiField>
        @Suppress("UNCHECKED_CAST") get() = fieldList.selectedValuesList as List<PsiField>

    init {
        setTitle(title)

        val fields = CollectionListModel(*clazz.allFields)
        fieldList = JBList(fields)
        fieldList.cellRenderer = DefaultPsiElementCellRenderer()
        fieldList.selectionModel.setSelectionInterval(0, fields.size - 1)
        fieldList.requestFocus()

        val decorator = ToolbarDecorator.createDecorator(fieldList)
        decorator.disableAddAction()

        val panel = decorator.createPanel()
        val message = "$title (Warning: existing method(s) will be replaced):"
        component = LabeledComponent.create(panel, message)

        init ()
    }

    override fun createCenterPanel(): JComponent = component
}
