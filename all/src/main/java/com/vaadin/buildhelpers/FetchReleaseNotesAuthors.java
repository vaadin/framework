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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class FetchReleaseNotesAuthors {
    private static final String template = "<li class=\"author\">@author@</li>";

    public static void main(String[] args) throws IOException,
            InterruptedException {
        Properties authorMap = new Properties();

        String authorsFilename = FetchReleaseNotesAuthors.class.getPackage()
                .getName().replace(".", "/")
                + "/authormap.properties";
        InputStream s = FetchReleaseNotesAuthors.class.getClassLoader()
                .getResourceAsStream(authorsFilename);
        if (s == null) {
            System.err.println("Author mapping file " + authorsFilename
                    + " not found!");
        }
        authorMap.load(s);

        String version = System.getProperty("vaadin.version");
        String previousVersion = getPreviousVersion(version);
        // System.out.println("Using previous version: " + previousVersion);
        // List all commits which are in this version but not in
        // "previousVersion"
        String cmd = "git log --pretty=%an HEAD ^origin/" + previousVersion;
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        if (p.exitValue() != 0) {
            System.err.println("Exit code: " + p.exitValue());
        }
        BufferedReader b = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line = "";

        List<String> authors = new ArrayList<String>();
        while ((line = b.readLine()) != null) {
            String author = line;
            if (authorMap.containsKey(author)) {
                author = authorMap.getProperty(author);
            }
            if (author != null && !author.equals("")
                    && !authors.contains(author)) {
                authors.add(author);
            }
        }
        Collections.sort(authors);
        for (String author : authors) {
            System.out.println(template.replace("@author@", author));
        }
    }

    private static String getPreviousVersion(String version) {
        String[] versionNumbers = version.split("\\.");
        if (versionNumbers.length > 4 || versionNumbers.length < 3) {
            throw new IllegalArgumentException("Cannot parse version: "
                    + version);
        }
        int major = Integer.parseInt(versionNumbers[0]);
        int minor = Integer.parseInt(versionNumbers[1]);
        int maintenance = Integer.parseInt(versionNumbers[2]);
        // String qualifier = versionNumbers[3];

        if (minor == 0) {
            // Major release, can't know what the previous minor was
            throw new IllegalArgumentException(
                    "Can't know what previous minor version was");
        }
        if (maintenance == 0) {
            // Minor release, use last minor
            return major + "." + (minor - 1);
        } else {
            // Maintenance, use last maintenance
            return major + "." + minor + "." + (maintenance - 1);
        }
    }
}
