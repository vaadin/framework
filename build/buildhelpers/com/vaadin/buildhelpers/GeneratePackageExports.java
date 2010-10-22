package com.vaadin.buildhelpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

/**
 * Generates Export-Packages attribute for OSGi compatible manifest.
 * 
 * Reads the included Java packages in Vaadin JAR, generates a corresponding
 * MANIFEST.MF file, and replaces the dummy one in the JAR with the
 * generated one.
 * 
 * See #3521 for details.
 * 
 * @author magi
 */
public class GeneratePackageExports {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Invalid number of parameters\n"+
                               "Usage: java -cp .. GenerateManifest <package.jar>");
            System.exit(1);
        }

        // Open tje JAR
        String jarFilename = args[0];
        JarFile jar = null;
        try {
            jar = new JarFile(jarFilename);
        } catch (IOException e) {
            System.err.println("Unable to open JAR '"+jarFilename+"'");
            System.exit(1);
        }

        // List the included Java packages
        HashSet<String> packages = new HashSet<String>();
        for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
            JarEntry entry = it.nextElement();
            if (entry.getName().startsWith("com") && entry.getName().endsWith(".class")) {
                int lastSlash = entry.getName().lastIndexOf('/');
                String pkg = entry.getName().substring(0, lastSlash).replace('/', '.');
                packages.add(pkg);
            }
        }

        // List theme packages
        for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
            JarEntry entry = it.nextElement();
            if (entry.isDirectory() && entry.getName().startsWith("VAADIN/themes")) {
                // Strip ending slash
                int lastSlash = entry.getName().lastIndexOf('/');
                String pkg = entry.getName().substring(0, lastSlash).replace('/', '.');
                packages.add(pkg);
            }
        }
        
        // Replacement for the "Export-Package" attribute in the manifest
        String exportPackage = "";

        // Produce an ordered listing of the package names
        String packageArray[] = new String[packages.size()];
        packages.toArray(packageArray);
        Arrays.sort(packageArray);
        for (int i=0; i<packageArray.length; i++) {
            if (i == 0)
                exportPackage = packageArray[i];
            else
                exportPackage += ", " + packageArray[i];
        }
        
        // Read old manifest
        Manifest oldMF = null;
        try {
            oldMF = jar.getManifest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Read main attributes
        Attributes mainAtts = oldMF.getMainAttributes();
        Vector<String> keys = new Vector<String>(mainAtts.size());
        for (Iterator<Object> attrit = mainAtts.keySet().iterator(); attrit.hasNext();) {
            Name name = (Name) attrit.next();
            keys.add(name.toString());
        }
        
        // Put the manifest version as the first line
        String orderedKeys[] = new String[keys.size()]; 
        keys.toArray(orderedKeys);
        Arrays.sort(orderedKeys); // Must sort to be able to search
        int mvPos = Arrays.binarySearch(orderedKeys, "Manifest-Version");
        orderedKeys[mvPos] = orderedKeys[0]; // Swap
        orderedKeys[0] = "Manifest-Version";

        // This final ordering is just for esthetic reasons and
        // in practice unnecessary and will actually be messed up 
        // when the 'jar' command reads the manifest
        Arrays.sort(orderedKeys, 1, orderedKeys.length-1);

        // Create the modified manifest
        ManifestWriter manifest = new ManifestWriter();
        for (int i=0; i<orderedKeys.length; i++) {
            // Skip an existing Export-Package attribute
            if (orderedKeys[i].equals("Export-Package")) {
                // Copy the attribute to the modified manifest
                manifest.writeAttribute(orderedKeys[i], mainAtts.getValue(orderedKeys[i]));
            }
        }
        
        // Add the Export-Package attribute at the end of the manifest.
        // The alternative would be replacing an existing attribute in
        // the loop above, but it's not guaranteed that it exists.
        manifest.writeAttribute("Export-Package", exportPackage);

        // Update the manifest in the Jar
        int status = manifest.updateJar(jarFilename);

        if (status != 0)
            System.exit(status);
    }
    
}
