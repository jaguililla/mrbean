package co.there4.mrbean

import co.there4.mrbean.Settings.Companion.getTemplate
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE
import com.intellij.psi.JavaPsiFacade.getElementFactory
import com.intellij.psi.util.PsiTreeUtil.getParentOfType
import org.apache.velocity.app.Velocity.evaluate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.apache.velocity.VelocityContext

import java.io.StringWriter

abstract class GenerateAction
    internal constructor(text: String, private val dialogTitle: String, private val method: String?) :
        AnAction(text) {

    protected fun setNewMethod(psiClass: PsiClass, newMethodBody: String) {
        val elementFactory = getElementFactory(psiClass.project)
        val newMethod = elementFactory.createMethodFromText(newMethodBody, psiClass)
        val method = addOrReplaceMethod(psiClass, newMethod)
        val project = psiClass.project
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(method)
    }

    protected fun addOrReplaceMethod(clazz: PsiClass, method: PsiMethod): PsiElement =
        findMethod(clazz, method)?.replace(method) ?: clazz.add(method)

    protected fun findMethod(clazz: PsiClass, searchedMethod: PsiMethod): PsiMethod? =
        clazz.allMethods.firstOrNull {
            it != null &&
            it.containingClass != null
            it.containingClass!!.name == clazz.name &&
            it.name == searchedMethod.name &&
            getParameterList(it) == getParameterList(searchedMethod)
        }

    private fun getParameterList(method: PsiMethod): List<String> =
        method.parameterList.parameters.map { p -> p.type.canonicalText }.toList()

    protected open fun generate(clazz: PsiClass, fields: List<PsiField>, template: String) {
        val context = VelocityContext()
        context.put("clazz", clazz)
        context.put("fields", fields)

        val output = StringWriter()
        evaluate(context, output, "mrbean", template)
        setNewMethod(clazz, output.toString())
    }

    private fun getPsiClassFromContext(evt: AnActionEvent): PsiClass? {
        val psiFile = evt.getData(PSI_FILE)
        val editor = evt.getData(EDITOR)
        if (psiFile == null || editor == null)
            return null

        val offset = editor.caretModel.offset
        val elementAt = psiFile.findElementAt(offset)
        return getParentOfType(elementAt, PsiClass::class.java)
    }

    override fun actionPerformed(evt: AnActionEvent) {
        val clazz = getPsiClassFromContext(evt) ?: throw IllegalStateException ()
        val dlg = FieldSelectionDialog(clazz, dialogTitle)
        dlg.show()
        if (dlg.isOK)
            generate(clazz, dlg.fields)
    }

    override fun update(evt: AnActionEvent) {
        val clazz = getPsiClassFromContext(evt)
        evt.presentation.isEnabled = clazz != null
    }

    open fun generate(clazz: PsiClass, fields: List<PsiField>) {
        object : WriteCommandAction.Simple<Any>(clazz.project, clazz.containingFile) {
            override fun run() {
                generate(clazz, fields, if (method == null) "" else getTemplate (method))
            }
        }.execute()
    }
}
