package co.there4.mrbean;

import static org.apache.velocity.app.Velocity.evaluate;

import java.io.StringWriter;
import java.util.List;

import com.intellij.psi.*;
import org.apache.velocity.VelocityContext;

abstract class GenerateManyAction extends GenerateAction {
    GenerateManyAction (String text, String dialogTitle, String method) {
        super (text, dialogTitle, method);
    }

    @Override protected void generate (
        PsiClass clazz, List<PsiField> fields, String template, String name) {
        fields.stream ().forEach (f -> {
            VelocityContext context = new VelocityContext();
            context.put("clazz", clazz);
            context.put("fields", fields);

            StringWriter output = new StringWriter();

            String fn = f.getName ();
            String fName = name == null || name.isEmpty () || name.equals ("_")?
                fn : name + fn.substring(0, 1).toUpperCase() + fn.substring (1);

            context.put("field", f);
            context.put("fieldName", fName);

            f.getType().getCanonicalText ();

            evaluate (context, output, "mrbean", template);

            setNewMethod (clazz, output.toString (), fName);
        });
    }
}
