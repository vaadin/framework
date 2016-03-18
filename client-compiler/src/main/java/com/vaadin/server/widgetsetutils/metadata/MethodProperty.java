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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class MethodProperty extends Property {

    private final JMethod setter;

    private final String getter;

    private MethodProperty(JClassType beanType, JMethod setter, String getter) {
        super(getTransportFieldName(setter), beanType, setter
                .getParameterTypes()[0]);
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public boolean hasAccessorMethods() {
        return getter != null;
    }

    public static Collection<MethodProperty> findProperties(JClassType type) {
        Collection<MethodProperty> properties = new ArrayList<MethodProperty>();

        Set<String> getters = new HashSet<String>();
        List<JMethod> setters = getSetters(type, getters);
        for (JMethod setter : setters) {
            String getter = findGetter(type, setter);
            properties.add(new MethodProperty(setter.getEnclosingType(),
                    setter, getters.contains(getter) ? getter : null));
        }

        return properties;
    }

    /**
     * Returns a list of all setters found in the beanType or its parent class
     * 
     * @param beanType
     *            The type to check
     * @param getters
     *            Set that will be filled with names of getters.
     * @return A list of setter methods from the class and its parents
     */
    private static List<JMethod> getSetters(JClassType beanType,
            Set<String> getters) {
        List<JMethod> setterMethods = new ArrayList<JMethod>();

        while (beanType != null
                && !beanType.getQualifiedSourceName().equals(
                        Object.class.getName())) {
            for (JMethod method : beanType.getMethods()) {
                // Process all setters that have corresponding fields
                if (!method.isPublic() || method.isStatic()) {
                    // Not getter/setter, skip to next method
                    continue;
                }
                String methodName = method.getName();
                if (methodName.startsWith("set")
                        && method.getParameterTypes().length == 1) {
                    setterMethods.add(method);
                } else if (method.getParameterTypes().length == 0
                        && methodName.startsWith("is")
                        || methodName.startsWith("get")) {
                    getters.add(methodName);
                }
            }
            beanType = beanType.getSuperclass();
        }

        return setterMethods;
    }

    @Override
    public void writeGetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable) {
        String value = String.format("%s.@%s::%s()()", beanVariable,
                getBeanType().getQualifiedSourceName(), getter);
        w.print("return ");
        w.print(boxValue(value));
        w.println(";");
    }

    @Override
    public void writeSetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable, String valueVariable) {
        w.println("%s.@%s::%s(%s)(%s);", beanVariable, getBeanType()
                .getQualifiedSourceName(), setter.getName(), setter
                .getParameterTypes()[0].getJNISignature(),
                unboxValue(valueVariable));

    }

    private static String findGetter(JClassType beanType, JMethod setterMethod) {
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
