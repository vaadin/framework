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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FetchReleaseNotesTickets {
    private static final String queryURL = "http://dev.vaadin.com/query?status=pending-release&amp;status=released&amp;@milestone@&amp;resolution=fixed&amp;col=id&amp;col=summary&amp;col=owner&amp;col=type&amp;col=priority&amp;col=component&amp;col=version&amp;col=bfptime&col=fv&amp;format=tab&amp;order=id";
    private static final String ticketTemplate = "<tr>"
            + "@badge@" //
            + "<td class=\"ticket\"><a href=\"http://dev.vaadin.com/ticket/@ticket@\">#@ticket@</a></td>" //
            + "<td>@description@</td>" //
            + "</tr>"; //

    public static void main(String[] args) throws IOException {
        String versionsProperty = System.getProperty("vaadin.version");
        if (versionsProperty == null || versionsProperty.equals("")) {
            usage();
        }
        String milestone = "";

        List<String> versions = new ArrayList<String>();
        for (String version : versionsProperty.split(" ")) {
            if (version.endsWith(".0") || version.matches(".*\\.rc\\d+")) {
                // Find all prerelease versions for final or rc

                // Strip potential rc prefix
                version = version.replaceAll("\\.rc\\d+$", "");
                versions.addAll(findPrereleaseVersions(version));
            } else {
                versions.add(version);
            }
        }

        for (String version : versions) {
            if (!milestone.equals("")) {
                milestone += "&amp;";
            }
            milestone += "milestone=Vaadin+" + version;
        }

        printMilestone(milestone);
    }

    private static List<String> findPrereleaseVersions(String baseVersion) {
        List<String> versions = new ArrayList<String>();

        for (int i = 0; i < 50; i++) {
            versions.add(baseVersion + ".alpha" + i);
        }
        for (int i = 0; i < 10; i++) {
            versions.add(baseVersion + ".beta" + i);
        }
        for (int i = 0; i < 10; i++) {
            versions.add(baseVersion + ".rc" + i);
        }

        return versions;
    }

    private static void printMilestone(String milestone)
            throws MalformedURLException, IOException {

        URL url = new URL(queryURL.replace("@milestone@", milestone));
        URLConnection connection = url.openConnection();
        InputStream urlStream = connection.getInputStream();

        List<String> tickets = IOUtils.readLines(urlStream);

        for (String ticket : tickets) {
            // Omit BOM
            if (!ticket.isEmpty() && ticket.charAt(0) == 65279) {
                ticket = ticket.substring(1);
            }
            String[] fields = ticket.split("\t");
            if ("id".equals(fields[0])) {
                // This is the header
                continue;
            }
            String summary = fields[1];
            if (summary.startsWith("\"") && summary.endsWith("\"")) {
                // If a summary starts with " and ends with " then all quotes in
                // the summary are encoded as double quotes
                summary = summary.substring(1, summary.length() - 1);
                summary = summary.replace("\"\"", "\"");
            }
            String badge = "<td></td>";
            if (fields.length >= 8 && !fields[7].equals("")) {
                badge = "<td class=\"bfp\"><span class=\"bfp\">Priority</span></td>";
            } else if (fields.length >= 9 && fields[8].equalsIgnoreCase("true")) {
                badge = "<td class=\"fv\"><span class=\"fv\">Vote</span></td>";
            }

            System.out.println(ticketTemplate.replace("@ticket@", fields[0])
                    .replace("@description@", summary)
                    .replace("@badge@", badge));
        }
        urlStream.close();
    }

    private static void usage() {
        System.err.println("Usage: "
                + FetchReleaseNotesTickets.class.getSimpleName()
                + " -Dvaadin.version=<version>");
        System.exit(1);
    }
}
