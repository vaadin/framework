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
package com.vaadin.tests.tb3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AffectedTB3TestLocator extends TB3TestLocator {

    private final ChangedTB3TestLocator changedTB3TestLocator;

    public AffectedTB3TestLocator() {
        changedTB3TestLocator = new ChangedTB3TestLocator();
    }

    @Override
    protected <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage, String[] ignoredPackages) throws IOException {
        List<Class<? extends T>> allTestClasses = super.findClasses(baseClass,
                basePackage, ignoredPackages);

        List<Class<? extends T>> changedTestClasses = changedTB3TestLocator
                .findClasses(baseClass, basePackage, ignoredPackages);

        return getAffectedTestClasses(allTestClasses, changedTestClasses);
    }

    private <T> List<Class<? extends T>> getAffectedTestClasses(
            List<Class<? extends T>> allTestClasses,
            List<Class<? extends T>> changedTestClasses) throws IOException {

        Set testClasses = new HashSet(changedTestClasses);
        testClasses
                .addAll(getTestClassesWithAffectedPackageName(allTestClasses));

        List<Class<? extends T>> affectedTestClasses = new ArrayList<Class<? extends T>>();
        affectedTestClasses.addAll(testClasses);

        return affectedTestClasses;
    }

    private <T> List<Class<? extends T>> getTestClassesWithAffectedPackageName(
            List<Class<? extends T>> classes) {
        List<Class<? extends T>> affectedTestClasses = new ArrayList<Class<? extends T>>();
        List<String> affectedFiles = getAffectedFiles();

        for (Class c : classes) {
            String[] packageParts = c.getName().split("\\.");
            String lastPart = packageParts[packageParts.length - 2];

            for (String f : affectedFiles) {
                if (f.toLowerCase().contains(lastPart.toLowerCase())) {
                    affectedTestClasses.add(c);

                    // Break here not to accidentally add the same test class
                    // multiple times if it matches more than one file.
                    break;
                }
            }
        }

        return affectedTestClasses;
    }

    private List<String> getAffectedFiles() {
        List<String> affectedFilePaths = new ArrayList<String>();

        for (String path : changedTB3TestLocator.getChangedFilePaths()) {
            if (!path.toLowerCase().contains("test")) {
                affectedFilePaths.add(path);
            }
        }

        return affectedFilePaths;
    }
}