package co.there4.mrbean.processor

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.*
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.*

@Target (TYPE, CLASS)
@Retention (SOURCE)
annotation class Model

class Processor : AbstractProcessor () {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        roundEnv?.getElementsAnnotatedWith(Model::class.java)
        roundEnv?.rootElements?.forEach { processingEnv.messager.printMessage(NOTE, ">>> ${it.simpleName}" ) }
        return true
    }
}
