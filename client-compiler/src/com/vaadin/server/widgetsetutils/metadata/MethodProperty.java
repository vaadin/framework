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

package com.vaadin.server.widgetsetutils.metadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class MethodProperty extends Property {

    private final JMethod setter;

    private MethodProperty(JClassType beanType, JMethod setter) {
        super(getTransportFieldName(setter), beanType, setter
                .getParameterTypes()[0]);
        this.setter = setter;
    }

    public static Collection<MethodProperty> findProperties(JClassType type) {
        Collection<MethodProperty> properties = new ArrayList<MethodProperty>();

        List<JMethod> setters = getSetters(type);
        for (JMethod setter : setters) {
            properties.add(new MethodProperty(type, setter));
        }

        return properties;
    }

    /**
     * Returns a list of all setters found in the beanType or its parent class
     * 
     * @param beanType
     *            The type to check
     * @return A list of setter methods from the class and its parents
     */
    private static List<JMethod> getSetters(JClassType beanType) {
        List<JMethod> setterMethods = new ArrayList<JMethod>();

        while (beanType != null
                && !beanType.getQualifiedSourceName().equals(
                        Object.class.getName())) {
            for (JMethod method : beanType.getMethods()) {
                // Process all setters that have corresponding fields
                if (!method.isPublic() || method.isStatic()
                        || !method.getName().startsWith("set")
                        || method.getParameterTypes().length != 1) {
                    // Not setter, skip to next method
                    continue;
                }
                setterMethods.add(method);
            }
            beanType = beanType.getSuperclass();
        }

        return setterMethods;
    }

    @Override
    public void writeSetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable, String valueVariable) {
        w.print("((");
        w.print(getBeanType().getQualifiedSourceName());
        w.print(") ");
        w.print(beanVariable);
        w.print(").");
        w.print(setter.getName());
        w.print("((");
        w.print(getUnboxedPropertyTypeName());
        w.print(") ");
        w.print(valueVariable);
        w.println(");");
    }

    @Override
    public void writeGetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable) {
        w.print("return ((");
        w.print(getBeanType().getQualifiedSourceName());
        w.print(") ");
        w.print(beanVariable);
        w.print(").");
        w.print(findGetter(getBeanType(), setter));
        w.print("();");
    }

    private String findGetter(JClassType beanType, JMethod setterMethod) {
        JType setterParameterType = setterMethod.getParameterTypes()[0];
        String fieldName = setterMethod.getName().substring(3);
        if (setterParameterType.getQualifiedSourceName().equals(
                boolean.class.getName())) {
            return "is" + fieldName;
        } else {
            return "get" + fieldName;
        }
    }

    private static String getTransportFieldName(JMethod setter) {
        String baseName = setter.getName().substring(3);
        return Character.toLowerCase(baseName.charAt(0))
                + baseName.substring(1);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return setter.getAnnotation(annotationClass);
    }

}
