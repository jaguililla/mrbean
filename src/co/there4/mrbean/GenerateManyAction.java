package co.there4.mrbean;

import static java.util.Arrays.asList;
import static org.apache.velocity.app.Velocity.evaluate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.velocity.VelocityContext;

abstract class GenerateManyAction extends GenerateAction {
    GenerateManyAction (String text, String dialogTitle, String method) {
        super (text, dialogTitle, method);
    }

    @Override protected void generate (PsiClass clazz, List<PsiField> fields, String template) {
        List<String> templates = asList (template.split ("##~"));

        fields.stream ().forEach (f -> {
            VelocityContext context = new VelocityContext();
            context.put("clazz", clazz);
            context.put("fields", fields);

            String fn = f.getName ();
            if (fn == null)
                throw new IllegalStateException ();

            String fName = fn.substring(0, 1).toUpperCase() + fn.substring (1);

            context.put("field", f);
            context.put("fieldName", fName);

            PsiType type = f.getType ();
            if (type instanceof PsiClassReferenceType) {
                final PsiClassReferenceType classType = (PsiClassReferenceType)type;
                context.put("fieldType", classType.getClassName ());
                context.put("fieldTypeParameters", classType.getReference ().getTypeParameters ());
            }

            templates.forEach (ct -> {
                try (StringWriter output = new StringWriter()) {
                    evaluate (context, output, "mrbean", ct);
                    String body = String.valueOf (output).trim();
                    output.flush ();
                    if (!body.trim ().isEmpty ())
                        setNewMethod (clazz, body);
                }
                catch (IOException e) {
                    e.printStackTrace ();
                }
            });
        });
    }
}
