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
                            + "Usage: java -cp .. GenerateManifest <package.jar> <accepted package prefixes>");
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

        // List the included Java packages
        HashSet<String> packages = getPackages(jar, acceptedPackagePrefixes);

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

            joinedPackages.append(packageArray[i]);
        }

        return joinedPackages.toString();
    }

    private static HashSet<String> getPackages(JarFile jar,
            List<String> acceptedPackagePrefixes) {
        HashSet<String> packages = new HashSet<String>();
        for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
            JarEntry entry = it.nextElement();
            if (!entry.getName().endsWith(".class")) {
                continue;
            }

            boolean accept = false;
            for (String prefix : acceptedPackagePrefixes) {
                if (entry.getName().startsWith(prefix)) {
                    accept = true;
                    break;
                }
            }
            if (!accept) {
                continue;
            }

            int lastSlash = entry.getName().lastIndexOf('/');
            String pkg = entry.getName().substring(0, lastSlash)
                    .replace('/', '.');
            packages.add(pkg);
        }

        // List theme packages
        for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
            JarEntry entry = it.nextElement();
            if (entry.isDirectory()
                    && entry.getName().startsWith("VAADIN/themes")) {
                // Strip ending slash
                int lastSlash = entry.getName().lastIndexOf('/');
                String pkg = entry.getName().substring(0, lastSlash)
                        .replace('/', '.');
                packages.add(pkg);
            }
        }

        return packages;
    }
}
