/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.util;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.tests.VaadinClasses;

public class GraphVizClassHierarchyCreator {

    public static void main(String[] args) {
        String gv = getGraphVizHierarchy((List) VaadinClasses.getComponents(),
                "com.vaadin");
        System.out.println(gv);
    }

    private static String getGraphVizHierarchy(List<Class> classes,
            String packageToInclude) {
        boolean includeInterfaces = false;

        StringBuilder header = new StringBuilder();
        header.append("digraph finite_state_machine {\n"
                + "        rankdir=BT;\n" + "        dpi=\"150\";\n"
                + "                ratio=\"0.25\";\n");

        StringBuilder sb = new StringBuilder();

        Set<Class> classesAndParents = new HashSet<>();
        for (Class<?> cls : classes) {
            addClassAndParents(classesAndParents, cls, packageToInclude);
        }

        Set<Class> interfaces = new HashSet<>();
        for (Object cls : classesAndParents.toArray()) {
            for (Class<?> c : ((Class) cls).getInterfaces()) {
                addClassAndParentInterfaces(classesAndParents, c,
                        packageToInclude);
            }
        }

        for (Class<?> c : classesAndParents) {
            appendClass(sb, c, c.getSuperclass(), packageToInclude,
                    includeInterfaces);
            for (Class ci : c.getInterfaces()) {
                appendClass(sb, c, ci, packageToInclude, includeInterfaces);
            }
        }

        header.append("    node [shape = ellipse, style=\"dotted\"] ");
        for (Class c : classesAndParents) {
            if (!c.isInterface() && Modifier.isAbstract(c.getModifiers())) {
                header.append(c.getSimpleName()).append(" ");
            }
        }
        if (includeInterfaces) {
            System.out.print("    node [shape = ellipse, style=\"solid\"] ");
            for (Class c : classesAndParents) {
                if (c.isInterface()) {
                    header.append(c.getSimpleName()).append(" ");
                }
            }
            header.append(";\n");
        }
        header.append(";\n");
        header.append("    node [shape = rectangle, style=\"solid\"];\n");
        return header.toString() + sb.toString() + "}";
    }

    private static void addClassAndParents(Set<Class> classesAndParents,
            Class<?> cls, String packageToInclude) {

        if (cls == null) {
            return;
        }

        if (classesAndParents.contains(cls)) {
            return;
        }

        if (!cls.getPackage().getName().startsWith(packageToInclude)) {
            return;
        }

        classesAndParents.add(cls);
        addClassAndParents(classesAndParents, cls.getSuperclass(),
                packageToInclude);

    }

    private static void addClassAndParentInterfaces(
            Set<Class> classesAndParents, Class<?> cls,
            String packageToInclude) {

        if (cls == null) {
            return;
        }

        if (classesAndParents.contains(cls)) {
            return;
        }

        if (!cls.getPackage().getName().startsWith(packageToInclude)) {
            return;
        }

        classesAndParents.add(cls);
        for (Class iClass : cls.getInterfaces()) {
            addClassAndParentInterfaces(classesAndParents, iClass,
                    packageToInclude);
        }

    }

    private static void appendClass(StringBuilder sb, Class<?> c,
            Class<?> superClass, String packageToInclude,
            boolean includeInterfaces) {
        if (superClass == null) {
            return;
        }
        if (!c.getPackage().getName().startsWith(packageToInclude)) {
            return;
        }
        if (!superClass.getPackage().getName().startsWith(packageToInclude)) {
            return;
        }
        if (!includeInterfaces
                && (c.isInterface() || superClass.isInterface())) {
            return;
        }

        sb.append(c.getSimpleName()).append(" -> ")
                .append(superClass.getSimpleName()).append("\n");

    }

    private static void addInterfaces(Set<Class> interfaces, Class<?> cls) {
        if (interfaces.contains(cls)) {
            return;
        }

        if (cls.isInterface()) {
            interfaces.add(cls);
        }

        for (Class c : cls.getInterfaces()) {
            addInterfaces(interfaces, c);
        }
    }

}
