/**
 * 
 */
package com.itmill.toolkit.demo.sampler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gwt.i18n.client.Messages.Example;

abstract public class Feature {

    public static final Object PROPERTY_ICON = "Icon";
    public static final Object PROPERTY_NAME = "Name";
    public static final Object PROPERTY_DESCRIPTION = "Description";

    protected Example example = null;

    /** Get the name of the feature. Override if needed. */
    public String getName() {
        String[] cn = this.getClass().getName().split("\\.");
        return cn[cn.length - 1];
    }

    abstract public String getDescription();

    public String getIconName() {
        String[] cn = this.getClass().getName().split("\\.");
        String icon = cn[cn.length - 1] + ".gif";
        return icon;
    }

    /** Get the example instance. Override if instantiation needs parameters. */
    public Example getExample() {
        if (example == null) {
            String className = this.getClass().getName() + "Example";
            try {
                Class<?> classObject = getClass().getClassLoader().loadClass(
                        className);
                example = (Example) classObject.newInstance();
            } catch (ClassNotFoundException e) {
                return null;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
        return example;
    }

    public Reader getSource() {
        String className = this.getClass().getName() + "Example";
        String javaFileName = className.replace(".", "/");
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                javaFileName);
        return new InputStreamReader(is);
    }

    public Reader getSourceHTML() {
        return getSource();
    }

    public String toString() {
        return getName();
    }

}