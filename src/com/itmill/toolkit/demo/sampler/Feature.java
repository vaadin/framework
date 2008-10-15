package com.itmill.toolkit.demo.sampler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.itmill.toolkit.ui.Component;

abstract public class Feature {

    public static final Object PROPERTY_ICON = "Icon";
    public static final Object PROPERTY_NAME = "Name";
    public static final Object PROPERTY_DESCRIPTION = "Description";

    /**
     * Gets the name of this feature. Defaults to class simplename, override if
     * needed.
     * 
     * @return
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the description for this feature. May contain HTML.
     * 
     * @return
     */
    abstract public String getDescription();

    abstract public NamedExternalResource[] getRelatedResources();

    abstract public APIResource[] getRelatedAPI();

    abstract public Class[] getRelatedFeatures();

    /**
     * 
     * @return
     */
    public String getIconName() {
        String icon = getClass().getSimpleName() + ".gif";
        return icon;
    }

    /** Get the example instance. Override if instantiation needs parameters. */
    public Component getExample() {

        String className = this.getClass().getName() + "Example";
        try {
            Class<?> classObject = getClass().getClassLoader().loadClass(
                    className);
            return (Component) classObject.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

    }

    public BufferedReader getSource() {

        // TODO get's .txt for now, change to .java!
        try {
            InputStream is = getClass().getResourceAsStream(
                    getClass().getSimpleName() + "Example.txt");
            return new BufferedReader(new InputStreamReader(is));
        } catch (Exception e) {
            System.err.println("Could not read source for " + getPathName());
        }
        return null;
    }

    public BufferedReader getSourceHTML() {
        return getSource();
    }

    public String toString() {
        return getName();
    }

    public String getPathName() {
        return getClass().getSimpleName();
    }

}