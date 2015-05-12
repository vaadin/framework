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
package com.vaadin.buildhelpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Generates Export-Packages attribute for OSGi compatible manifest.
 * <p>
 * Reads the included Java packages in a jar file, generates a corresponding
 * Export-Package attribute, and appends it to the jar's MANIFEST.MF.
 * <p>
 * See #3521 for details.
 * 
 * @author magi
 */
public class GeneratePackageExports {

    private static final String EXPORT_PACKAGE_ATTRIBUTE = "Export-Package";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err
                    .println("Invalid number of parameters\n"
                            + "Usage: java -cp .. GenerateManifest <package.jar> <accepted package prefixes>\n"
                            + "Use -Dvaadin.version to specify the version to be used for the packages\n"
                            + "Use -DincludeNumberPackages=1 to include package names which start with a number (not 100% OSGi compatible)");
            System.exit(1);
        }

        // Open the JAR
        String jarFilename = args[0];
        JarFile jar = null;
        try {
            jar = new JarFile(jarFilename);
        } catch (IOException e) {
            System.err.println("Unable to open JAR '" + jarFilename + "'");
            System.exit(1);
        }

        // Accepted packages
        List<String> acceptedPackagePrefixes = new ArrayList<String>();
        for (int i = 1; i < args.length; i++) {
            acceptedPackagePrefixes.add(args[i]);
        }

        boolean includeNumberPackages = false;
        if ("1".equals(System.getProperty("includeNumberPackages"))) {
            includeNumberPackages = true;
        }

        // List the included Java packages
        HashSet<String> packages = getPackages(jar, acceptedPackagePrefixes,
                includeNumberPackages);

        // Avoid writing empty Export-Package attribute
        if (packages.isEmpty()) {
            return;
        }

        String exportPackage = sortAndJoinPackages(packages);

        // Read old manifest
        Manifest oldMF = null;
        try {
            oldMF = jar.getManifest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Attributes mainAttributes = oldMF.getMainAttributes();

        String existingExportPackage = mainAttributes
                .getValue(EXPORT_PACKAGE_ATTRIBUTE);
        if (existingExportPackage != null) {
            exportPackage = existingExportPackage + "," + exportPackage;
        }

        // Jar must be closed before updating it below, as it's
        // locked in Windows until closed. (#6045)
        try {
            jar.close();
        } catch (IOException e) {
            System.err.println("Unable to close JAR '" + jarFilename + "'");
        }

        // Create the modified manifest
        ManifestWriter manifest = new ManifestWriter();
        manifest.writeAttribute(EXPORT_PACKAGE_ATTRIBUTE, exportPackage);

        // Update the manifest in the Jar. The jar must be closed
        // before this is done.
        int status = manifest.updateJar(jarFilename);

        if (status != 0) {
            System.exit(status);
        }
    }

    private static String sortAndJoinPackages(HashSet<String> packages) {
        // Produce an ordered listing of the package names
        String packageArray[] = new String[packages.size()];
        packages.toArray(packageArray);
        Arrays.sort(packageArray);
        StringBuilder joinedPackages = new StringBuilder();
        for (int i = 0; i < packageArray.length; i++) {
            if (i != 0) {
                joinedPackages.append(",");
            }
            String version = getVersion(packageArray[i]);
            String packageAndVersion = packageArray[i];
            if (version != null) {
                packageAndVersion += ";version=\"" + version + "\"";
            } else {
                Logger.getLogger(GeneratePackageExports.class.getName())
                        .severe("No version defined for " + packageArray[i]);
            }
            joinedPackages.append(packageAndVersion);
        }

        return joinedPackages.toString();
    }

    /**
     * Tries to find version specified using system properties of type
     * version.<java package>. Searches for the packge and then its parents
     * recursively. Falls back to the "vaadin.version" system property if no
     * other properties are found.
     * 
     * @param javaPackage
     *            The package to determine a version for
     * @return A version or null if no version has been defined
     */
    private static String getVersion(String javaPackage) {
        String packageVersion = System.getProperty("version." + javaPackage);
        if (packageVersion != null) {
            return packageVersion;
        }
        String parentPackage = null;
        if (javaPackage.contains(".")) {
            parentPackage = javaPackage.substring(0,
                    javaPackage.lastIndexOf('.'));
            String parentVersion = getVersion(parentPackage);
            if (parentVersion != null) {
                return parentVersion;
            }
        }

        String vaadinVersion = System.getProperty("vaadin.version");
        if (vaadinVersion != null) {
            return vaadinVersion;
        }

        return null;
    }

    private static HashSet<String> getPackages(JarFile jar,
            List<String> acceptedPackagePrefixes, boolean includeNumberPackages) {
        HashSet<String> packages = new HashSet<String>();

        Pattern startsWithNumber = Pattern.compile("\\.\\d");

        for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
            JarEntry entry = it.nextElement();

            boolean classFile = entry.getName().endsWith(".class");
            boolean directory = entry.isDirectory();

            if (!classFile && !directory) {
                continue;
            }

            if (!acceptEntry(entry.getName(), acceptedPackagePrefixes)) {
                continue;
            }

            int lastSlash = entry.getName().lastIndexOf('/');
            String pkg = entry.getName().substring(0, lastSlash)
                    .replace('/', '.');

            if (!includeNumberPackages && startsWithNumber.matcher(pkg).find()) {
                continue;
            }

            packages.add(pkg);
        }

        return packages;
    }

    private static boolean acceptEntry(String name,
            List<String> acceptedPackagePrefixes) {
        for (String prefix : acceptedPackagePrefixes) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
