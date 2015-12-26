package co.there4.mrbean;

import static java.awt.BorderLayout.NORTH;
import static java.lang.String.format;
import static java.util.logging.Logger.getLogger;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.*;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public final class Settings implements Configurable {
    private static final Logger LOGGER = getLogger (Settings.class.getName ());

    static final String DISPLAY_NAME = "MrBean";
    static final String TEMPLATE_PREFIX = "template";
    static final PropertiesComponent SETTINGS = PropertiesComponent.getInstance ();

    private static String getSetting (String key) {
        final String k = DISPLAY_NAME + '.' + key;
        final String v = SETTINGS.getValue (k);
        LOGGER.info (format ("<<< %s:\n%s", k, v));
        return v;
    }

    private static String getSetting (String key, String defaultValue) {
        final String k = DISPLAY_NAME + '.' + key;
        final String v = SETTINGS.getValue (k, defaultValue);
        LOGGER.info (format ("<<< %s:\n%s\n---\n%s", k, defaultValue, v));
        return v;
    }

    private static void setSetting (String key, String value) {
        final String k = DISPLAY_NAME + '.' + key;
        LOGGER.info (format (">>> %s:\n%s", k, value));
        SETTINGS.setValue (k, value);
    }

    private static void setTemplate (String key, String value) {
        setSetting (TEMPLATE_PREFIX + '.' + key, value);
    }

    static String getTemplate (String key) {
        return getSetting (TEMPLATE_PREFIX + '.' + key, loadTemplate (key));
    }

    private static String loadTemplate (String key) {
        String templateFile = '/' + key + ".vm";
        InputStream input = GenerateToString.class.getResourceAsStream (templateFile);
        return new Scanner (input).useDelimiter ("\\A").next ();
    }

    JPanel component;
    List<TemplateArea> templates = new ArrayList<> ();

    class TemplateArea extends JPanel {
        final String key;
        final EditorTextField txtaTemplate;

        TemplateArea (String key) {
            this.key = key;

            setLayout (new BorderLayout (5, 5));
            add (NORTH, new JLabel (key));

            txtaTemplate = new EditorTextField (getTemplate (key));
            txtaTemplate.setOneLineMode (false);

            add (BorderLayout.CENTER, new JBScrollPane (txtaTemplate));
            final JButton defaultTemplate = new JButton ("Default");
            defaultTemplate.addActionListener (evt -> reset ());
            add (BorderLayout.EAST, defaultTemplate);
        }

        void reset () {
            txtaTemplate.setText (loadTemplate (key));
        }

        void save () {
            setTemplate (key, txtaTemplate.getText ());
        }
    }

    @Nls @Override public String getDisplayName () { return DISPLAY_NAME; }
    @Nullable @Override public String getHelpTopic () { return null; }

    @Nullable @Override public JComponent createComponent () {
        JPanel pnlSettings = new JPanel ();
        pnlSettings.setLayout (new BoxLayout (pnlSettings, BoxLayout.PAGE_AXIS));
        Stream.of ("equals", "get", "hashCode", "set", "toString", "with").forEach (template -> {
            final TemplateArea templateArea = new TemplateArea (template);
            pnlSettings.add (templateArea);
            templates.add (templateArea);
        });
        return pnlSettings;
    }

    @Override public boolean isModified () {
        return templates.stream ()
            .map (this::modified)
            .reduce (false, (a, b) -> a || b);
    }

    private boolean modified (TemplateArea ta) {
        return !ta.txtaTemplate.getText ().equals (getTemplate (ta.key));
    }

    @Override public void apply () throws ConfigurationException {
        templates.stream ().forEach (TemplateArea::save);
    }

    @Override public void reset () {
//        templates.stream ().forEach (TemplateArea::reset);
    }

    @Override public void disposeUIResources () {}
}
