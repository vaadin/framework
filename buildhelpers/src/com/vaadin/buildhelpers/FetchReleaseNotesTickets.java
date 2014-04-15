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
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FetchReleaseNotesTickets {
    private static final String queryURL = "http://dev.vaadin.com/query?status=closed&amp;milestone=Vaadin+@version@&amp;resolution=fixed&amp;format=tab&amp;order=id";
    private static final String ticketTemplate = "  <li><a href=\"http://dev.vaadin.com/ticket/@ticket@\">#@ticket@</a>: @description@</li>";

    public static void main(String[] args) throws IOException {
        String version = System.getProperty("vaadin.version");
        if (version == null || version.equals("")) {
            usage();
        }

        URL url = new URL(queryURL.replace("@version@", version));
        URLConnection connection = url.openConnection();
        InputStream urlStream = connection.getInputStream();

        @SuppressWarnings("unchecked")
        List<String> tickets = IOUtils.readLines(urlStream);

        for (String ticket : tickets) {
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
            System.out.println(ticketTemplate.replace("@ticket@", fields[0])
                    .replace("@description@", summary));
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
