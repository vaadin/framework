/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;

/**
 * Declarative test UI. Provides simple instantiation of HTML designs located
 * under {@code uitest/src}. Also provides {@link OnLoad} annotation that lets
 * you easily hook up methods to run after the UI has been created. Note: you
 * <i>must</i> add the {@link DeclarativeUI} annotation to your subclass; not
 * doing this will result in program failure.
 */
@SuppressWarnings("serial")
public class DeclarativeTestUI extends AbstractTestUI {

    private Logger logger;
    private Component component;

    /**
     * Class marker indicating the design .html file to load
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface DeclarativeUI {
        String value();

        /**
         * Set this property to true if you provide an absolute path to your
         * design; otherwise, the DeclarativeTestUI logic will look for the HTML
         * design file under {@code vaadin_project/uitest/src/<package path>/}.
         */
        boolean absolutePath() default false;
    }

    /**
     * Method marker interface indicating that a method should be run after the
     * declarative UI has been created
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface OnLoad {

    }

    /**
     * Figure out the proper path for the HTML design file
     */
    private String getDesignPath() {
        Class<?> clazz = getClass();
        String designFilePath = null;
        if (clazz.getAnnotation(DeclarativeUI.class).absolutePath()) {
            designFilePath = "";
        } else {
            // This is rather nasty.. but it works well enough for now.
            String userDir = System.getProperty("user.dir");
            designFilePath = userDir + "/uitest/src/"
                    + clazz.getPackage().getName().replace('.', '/') + "/";
        }

        String designFileName = clazz.getAnnotation(DeclarativeUI.class)
                .value();

        return designFilePath + designFileName;
    }

    private Component readDesign() throws Exception {
        String path = getDesignPath();
        getLogger().log(Level.INFO, "Reading design from " + path);

        File file = new File(path);
        return Design.read(new FileInputStream(file));
    }

    @Override
    protected void setup(VaadinRequest request) {
        Class<?> clazz = getClass();

        if (clazz.isAnnotationPresent(DeclarativeUI.class)) {

            // Create component
            try {
                component = readDesign();
            } catch (Exception e1) {
                getLogger().log(Level.SEVERE, "Error reading design", e1);
                return;
            }

            addComponent(component);

            // Call on-load methods (if applicable)
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(OnLoad.class)) {
                    try {
                        m.invoke(this, (Object[]) null);
                    } catch (IllegalAccessException e) {
                        getLogger().log(Level.SEVERE,
                                "Error invoking @OnLoad method", e);
                        return;
                    } catch (IllegalArgumentException e) {
                        getLogger().log(Level.SEVERE,
                                "Error invoking @OnLoad method", e);
                        return;
                    } catch (InvocationTargetException e) {
                        getLogger().log(Level.SEVERE,
                                "Error invoking @OnLoad method", e);
                        return;
                    }
                }
            }

        } else {
            throw new IllegalStateException(
                    "Cannot find declarative UI annotation");
        }
    }

    /**
     * Get access to the declaratively created component. This method typecasts
     * the component to the receiving type; if there's a mismatch between what
     * you expect and what's written in the design, this will fail with a
     * ClassCastException.
     * 
     * @return a Vaadin component
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent() {
        try {
            return (T) component;
        } catch (ClassCastException ex) {
            getLogger().log(Level.SEVERE,
                    "Component code/design type mismatch", ex);
        }
        return null;
    }

    /**
     * Get access to the logger of this class
     * 
     * @return a Logger instance
     */
    protected Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(getClass().getName());
        }
        return logger;
    }
}
