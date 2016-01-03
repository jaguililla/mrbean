package co.there4.mrbean

import java.util.Arrays.asList

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField

abstract class GenerateCompoundAction
    internal constructor(text: String, dialogTitle: String, vararg actions: GenerateAction) :
        GenerateAction(text, dialogTitle, null) {

    private val actions: List<GenerateAction> = asList(*actions)

    protected override fun generate(clazz: PsiClass, fields: List<PsiField>) {
        actions.forEach { action -> action.generate(clazz, fields) }
    }
}
