package co.there4.mrbean;

import javax.swing.*;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * TODO .
 *
 * @author jam
 */
@State (
    name = "MrBeanSettings",
    storages = @Storage (id = "other", file = "$APP_CONFIG$/mrbean.xml")
)
public class Settings implements Configurable, PersistentStateComponent<Element> {
    @Nls @Override public String getDisplayName () {
        return "MrBean";
    }

    @Nullable @Override public String getHelpTopic () {
        return "MrBean";
    }

    @Nullable @Override public JComponent createComponent () {
        return new JLabel ("MrBean");
    }

    @Override public boolean isModified () {
        return false;
    }

    @Override public void apply () throws ConfigurationException {

    }

    @Override public void reset () {

    }

    @Override public void disposeUIResources () {

    }

    @Nullable @Override public Element getState () {
        return null;
    }

    @Override public void loadState (Element s) {

    }
}
