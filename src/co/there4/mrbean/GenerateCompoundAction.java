package co.there4.mrbean;

import static java.util.Arrays.asList;

import java.util.List;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

abstract class GenerateCompoundAction extends GenerateAction {
    private final List<GenerateAction> actions;

    GenerateCompoundAction (String text, String dialogTitle, GenerateAction... actions) {
        super (text, dialogTitle, null);
        this.actions = asList (actions);
    }

    @Override protected void generate (
        PsiClass clazz, List<PsiField> fields) {
        actions.stream ().forEach (action -> action.generate (clazz, fields));
    }
}
