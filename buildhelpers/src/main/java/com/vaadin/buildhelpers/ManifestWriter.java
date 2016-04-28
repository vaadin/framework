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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.jar.Manifest;

public class ManifestWriter {
    StringBuffer buffer = new StringBuffer();

    public ManifestWriter() {
    }

    /**
     * Writes a manifest attribute to a temporary buffer.
     * 
     * @param name
     *            Attribute name
     * @param value
     *            Attribute value
     */
    public void writeAttribute(String name, String value) {
        int linelen = name.length() + 2;
        buffer.append(name);
        buffer.append(": ");

        String remainingValue = value;
        while (linelen + remainingValue.length() > 72) {
            int fitsLine = 72 - linelen;
            buffer.append(remainingValue.substring(0, fitsLine) + "\n ");
            remainingValue = remainingValue.substring(fitsLine);
            linelen = 1;
        }
        buffer.append(remainingValue + "\n");
    }

    /**
     * Writes the manifest to given JAR file.
     * 
     * The manifest must be created with {@code #writeAttribute(String, String)}
     * before calling this write.
     * 
     * @param jarFilename
     *            File name of the JAR in which the manifest is written
     * @return 0 on success, nonzero value on error
     */
    int updateJar(String jarFilename) {
        int status = 0;

        // Determine a temporary file name
        String newMfPrefix = "vaadin-manifest-" + (new Date()).getTime();
        File newMfFile = null;
        try {
            newMfFile = File.createTempFile(newMfPrefix, ".mf");
        } catch (IOException e) {
            System.err.println("Creating temp file failed");
            status = 1;
        }

        // Write the manifest to the temporary file
        if (status == 0) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(newMfFile);
                fos.write(getBytes());
                fos.close();
            } catch (IOException e) {
                System.err.println("Writing to file '"
                        + newMfFile.getAbsolutePath() + "' failed because: "
                        + e.getMessage());
                status = 1;
            }
        }

        // Check that the manifest is OK
        if (status == 0) {
            Manifest checkMf = new Manifest();
            FileInputStream is;
            try {
                is = new FileInputStream(newMfFile);
                checkMf.read(is);
            } catch (IOException e) {
                System.err.println("Reading from file '"
                        + newMfFile.getAbsolutePath() + "' failed because: "
                        + e.getMessage());
                status = 1;
            }
        }

        // Update the manifest in the Jar
        if (status == 0) {
            System.out.println("Updating manifest in JAR " + jarFilename);
            try {
                // The "mf" order must correspond with manifest-jarfile order
                Process process = Runtime.getRuntime().exec(
                        new String[] { "jar", "umf",
                                newMfFile.getAbsolutePath(), jarFilename });
                int exitValue = process.waitFor();
                if (exitValue != 0) {
                    InputStream jarErr = process.getErrorStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(jarErr));
                    while (reader.ready()) {
                        System.err.println("jar: " + reader.readLine());
                    }
                    System.err
                            .println("The 'jar' command returned with exit value "
                                    + exitValue);
                    status = 1;
                }
            } catch (IOException e) {
                System.err.println("Failed to execute 'jar' command. "
                        + e.getMessage());
                status = 1;
            } catch (InterruptedException e) {
                System.err
                        .println("Execution of 'jar' command was interrupted. "
                                + e.getMessage());
                status = 1;
            }
        }

        // Remove the temporary file
        if (newMfFile != null) {
            newMfFile.delete();
        }

        return status;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    public byte[] getBytes() {
        return buffer.toString().getBytes();
    }
}
