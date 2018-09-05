package com.vaadin.tests.tb3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

        List<Class<? extends T>> affectedTestClasses = new ArrayList<>();
        affectedTestClasses.addAll(testClasses);

        return affectedTestClasses;
    }

    private <T> List<Class<? extends T>> getTestClassesWithAffectedPackageName(
            List<Class<? extends T>> classes) {
        List<Class<? extends T>> affectedTestClasses = new ArrayList<>();
        List<String> affectedFiles = getAffectedFiles();

        for (Class c : classes) {
            String[] packageParts = c.getName().split("\\.");
            String lastPart = packageParts[packageParts.length - 2];

            for (String f : affectedFiles) {
                if (f.toLowerCase(Locale.ROOT)
                        .contains(lastPart.toLowerCase(Locale.ROOT))) {
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
        List<String> affectedFilePaths = new ArrayList<>();

        for (String path : changedTB3TestLocator.getChangedFilePaths()) {
            if (!path.toLowerCase(Locale.ROOT).contains("test")) {
                affectedFilePaths.add(path);
            }
        }

        return affectedFilePaths;
    }
}
