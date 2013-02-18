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
            System.out.println(ticketTemplate.replace("@ticket@", fields[0])
                    .replace("@description@", fields[1]));
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
