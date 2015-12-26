package co.there4.mrbean;

import static co.there4.mrbean.Settings.*;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.intellij.psi.JavaPsiFacade.getElementFactory;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static java.util.stream.Collectors.toList;
import static org.apache.velocity.app.Velocity.evaluate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.apache.velocity.VelocityContext;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Stream;

abstract class GenerateAction extends AnAction {
    private final String dialogTitle;
    private final String method;

    GenerateAction (String text, String dialogTitle, String method) {
        super (text);

        this.dialogTitle = dialogTitle;
        this.method = method;
    }

    protected void setNewMethod (PsiClass psiClass, String newMethodBody) {
        PsiElementFactory elementFactory = getElementFactory (psiClass.getProject ());
        PsiMethod newEqualsMethod =
            elementFactory.createMethodFromText (newMethodBody, psiClass);
        PsiElement method = addOrReplaceMethod (psiClass, newEqualsMethod);
        Project project = psiClass.getProject ();
        JavaCodeStyleManager.getInstance (project).shortenClassReferences (method);
    }

    protected PsiElement addOrReplaceMethod (PsiClass clazz, PsiMethod method) {
        PsiMethod existingEqualsMethod = findMethod (clazz, method);
        return existingEqualsMethod != null?
            existingEqualsMethod.replace (method) :
            clazz.add (method);
    }

    protected PsiMethod findMethod (PsiClass clazz, PsiMethod searchedMethod) {
        String methodName = searchedMethod.getName ();
        String psiClassName = clazz.getName ();
        for (PsiMethod method : clazz.getAllMethods ()) {
            if (method == null || method.getContainingClass () == null)
                continue;

            String currentClass = method.getContainingClass ().getName ();
            String currentMethod = method.getName ();

            if (psiClassName != null &&
                psiClassName.equals (currentClass) &&
                methodName.equals (currentMethod) &&
                getParameterList (method).equals (getParameterList (searchedMethod)))
                return method;
        }
        return null;
    }

    private List<String> getParameterList (PsiMethod method) {
        return Stream.of (method.getParameterList ().getParameters ())
            .map (p -> p.getType ().getCanonicalText ())
            .collect (toList());
    }

    protected void generate (PsiClass clazz, List<PsiField> fields, String template) {
        VelocityContext context = new VelocityContext();
        context.put("clazz", clazz);
        context.put("fields", fields);

        StringWriter output = new StringWriter();
        evaluate (context, output, "mrbean", template);
        setNewMethod (clazz, output.toString ());
    }

    private PsiClass getPsiClassFromContext (AnActionEvent evt) {
        PsiFile psiFile = evt.getData (PSI_FILE);
        Editor editor = evt.getData (EDITOR);
        if (psiFile == null || editor == null)
            return null;

        int offset = editor.getCaretModel ().getOffset ();
        PsiElement elementAt = psiFile.findElementAt (offset);
        return getParentOfType (elementAt, PsiClass.class);
    }

    @Override public void actionPerformed (@NotNull AnActionEvent evt) {
        PsiClass clazz = getPsiClassFromContext (evt);
        FieldSelectionDialog dlg = new FieldSelectionDialog (clazz, dialogTitle);
        dlg.show ();
        if (dlg.isOK ())
            generate (clazz, dlg.getFields ());
    }

    @Override public void update (@NotNull AnActionEvent evt) {
        PsiClass clazz = getPsiClassFromContext (evt);
        evt.getPresentation ().setEnabled (clazz != null);
    }

    protected void generate (final PsiClass clazz, final List<PsiField> fields) {
        new WriteCommandAction.Simple (clazz.getProject (), clazz.getContainingFile ()) {
            @Override protected void run () throws Throwable {
                generate (clazz, fields, method == null? "" : getTemplate (method));
            }
        }.execute ();
    }
}
