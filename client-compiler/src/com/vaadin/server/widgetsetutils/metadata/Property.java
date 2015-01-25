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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public abstract class Property implements Comparable<Property> {
    private final String name;
    private final JClassType beanType;
    private final JType propertyType;

    protected Property(String name, JClassType beanType, JType propertyType) {
        this.name = name;
        this.beanType = beanType;
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public JType getPropertyType() {
        return propertyType;
    }

    public String getUnboxedPropertyTypeName() {
        JType propertyType = getPropertyType();
        JPrimitiveType primitive = propertyType.isPrimitive();
        if (primitive != null) {
            return primitive.getQualifiedBoxedSourceName();
        } else {
            return propertyType.getQualifiedSourceName();
        }
    }

    public String boxValue(String codeSnippet) {
        JPrimitiveType primitive = propertyType.isPrimitive();
        if (primitive == null) {
            return codeSnippet;
        } else {
            return String.format("@%s::valueOf(%s)(%s)",
                    primitive.getQualifiedBoxedSourceName(),
                    propertyType.getJNISignature(), codeSnippet);
        }
    }

    public String unboxValue(String codeSnippet) {
        JPrimitiveType primitive = propertyType.isPrimitive();
        if (primitive == null) {
            return codeSnippet;
        } else {
            return String.format("%s.@%s::%sValue()()", codeSnippet,
                    primitive.getQualifiedBoxedSourceName(),
                    primitive.getSimpleSourceName());
        }
    }

    public JClassType getBeanType() {
        return beanType;
    }

    public abstract void writeSetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable, String valueVariable);

    public abstract void writeGetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable);

    public abstract boolean hasAccessorMethods();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Property) {
            Property other = (Property) obj;
            return other.getClass() == getClass()
                    && other.getBeanType().equals(getBeanType())
                    && other.getName().equals(getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() * 31 ^ 2 + getBeanType().hashCode() * 31
                + getName().hashCode();
    }

    @Override
    public int compareTo(Property o) {
        int comp = getName().compareTo(o.getName());
        if (comp == 0) {
            comp = getBeanType().getQualifiedSourceName().compareTo(
                    o.getBeanType().getQualifiedSourceName());
        }
        if (comp == 0) {
            comp = getClass().getCanonicalName().compareTo(
                    o.getClass().getCanonicalName());
        }
        return comp;
    }

    public abstract <T extends Annotation> T getAnnotation(
            Class<T> annotationClass);

}
