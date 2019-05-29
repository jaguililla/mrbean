package co.there4.mrbean

import java.util.Arrays.asList
import org.apache.velocity.app.Velocity.evaluate

import java.io.IOException
import java.io.StringWriter

import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.apache.velocity.VelocityContext

abstract class GenerateManyAction
internal constructor(text: String, dialogTitle: String, method: String) :
        GenerateAction(text, dialogTitle, method) {

    override fun generate(clazz: PsiClass, fields: List<PsiField>, template: String) {
        val templates = asList(*template.split("##~".toRegex()).dropLastWhile(String::isEmpty).toTypedArray())

        fields.forEach { f ->
            val context = VelocityContext()
            context.put("clazz", clazz)
            context.put("fields", fields)

            val fn = f.name

            val fName = fn.substring(0, 1).toUpperCase() + fn.substring(1)

            context.put("field", f)
            context.put("fieldName", fName)

            val type = f.type
            if (type is PsiClassReferenceType) {
                context.put("fieldType", type.className)
                context.put("fieldTypeParameters", type.reference.typeParameters)
            }

            templates.forEach { ct ->
                try {
                    StringWriter().use { output ->
                        evaluate(context, output, "mrbean", ct)
                        val body = output.toString().trim { it <= ' ' }
                        output.flush()
                        if (!body.trim { it <= ' ' }.isEmpty())
                            setNewMethod(clazz, body)
                    }
                } catch (e: IOException) {
                }
            }
        }
    }
}
