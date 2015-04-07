package co.there4.mrbean;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.intellij.psi.JavaPsiFacade.getElementFactory;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
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

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

public abstract class GenerateAction extends AnAction {
    private final String dialogTitle;
    private final String method, template;

    public GenerateAction (String text, String dialogTitle, String method, String template) {
        super (text);

        this.dialogTitle = dialogTitle;
        this.method = method;
        this.template = loadTemplate (template);
    }

    private String loadTemplate (String templateName) {
        InputStream input = GenerateToString.class.getResourceAsStream (templateName);
        return new Scanner (input).useDelimiter ("\\A").next ();
    }

    protected void setNewMethod (PsiClass psiClass, String newMethodBody, String methodName) {
        PsiElementFactory elementFactory = getElementFactory (psiClass.getProject ());
        PsiMethod newEqualsMethod =
            elementFactory.createMethodFromText (newMethodBody, psiClass);
        PsiElement method = addOrReplaceMethod (psiClass, newEqualsMethod, methodName);
        Project project = psiClass.getProject ();
        JavaCodeStyleManager.getInstance (project).shortenClassReferences (method);
    }

    protected PsiElement addOrReplaceMethod (
        PsiClass clazz, PsiMethod equalsMethod, String methodName) {

        PsiMethod existingEqualsMethod = findMethod (clazz, methodName);
        return existingEqualsMethod != null?
            existingEqualsMethod.replace (equalsMethod) :
            clazz.add (equalsMethod);
    }

    protected PsiMethod findMethod (PsiClass clazz, String methodName) {
        String psiClassName = clazz.getName ();
        for (PsiMethod method : clazz.getAllMethods ()) {
            if (method == null || method.getContainingClass () == null)
                continue;

            String currentClass = method.getContainingClass ().getName ();
            String currentMethod = method.getName ();
            if (psiClassName.equals (currentClass) && methodName.equals (currentMethod))
                return method;
        }
        return null;
    }

    protected void generate (
        PsiClass clazz, List<PsiField> fields, String template, String name) {

        VelocityContext context = new VelocityContext();
        context.put("clazz", clazz);
        context.put("fields", fields);

        StringWriter output = new StringWriter();
        evaluate (context, output, "mrbean", template);

        setNewMethod (clazz, output.toString (), name);
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

    public void generate (final PsiClass clazz, final List<PsiField> fields) {
        new WriteCommandAction.Simple (clazz.getProject (), clazz.getContainingFile ()) {
            @Override protected void run () throws Throwable {
                generate (clazz, fields, template, method);
            }
        }.execute ();
    }
}
