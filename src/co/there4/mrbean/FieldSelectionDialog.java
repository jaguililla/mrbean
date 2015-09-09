package co.there4.mrbean;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

final class FieldSelectionDialog extends DialogWrapper {
    private CollectionListModel<PsiField> fields;
    private final LabeledComponent<JPanel> component;

    FieldSelectionDialog (PsiClass clazz, String title) {
        super (clazz.getProject ());
        setTitle (title);

        fields = new CollectionListModel<> (clazz.getAllFields ());
        JBList fieldList = new JBList (fields);
        fieldList.setCellRenderer (new DefaultPsiElementCellRenderer ());
        fieldList.getSelectionModel ().setSelectionInterval (0, fields.getSize () - 1);
        fieldList.requestFocus ();

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator (fieldList);
        decorator.disableAddAction ();

        JPanel panel = decorator.createPanel ();
        String message = title + " (Warning: existing method(s) will be replaced):";
        component = LabeledComponent.create (panel, message);

        init ();
    }

    List<PsiField> getFields () {
        return fields.getItems ();
    }

    @Nullable @Override protected JComponent createCenterPanel () {
        return component;
    }
}
