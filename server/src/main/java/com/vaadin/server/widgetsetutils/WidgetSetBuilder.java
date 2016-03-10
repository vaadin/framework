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
package com.vaadin.server.widgetsetutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to update widgetsets GWT module configuration file. Can be used
 * command line or via IDE tools.
 * 
 * <p>
 * If module definition file contains text "WS Compiler: manually edited", tool
 * will skip editing file.
 * 
 */
public class WidgetSetBuilder {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
        } else {
            String widgetsetname = args[0];
            updateWidgetSet(widgetsetname);

        }
    }

    public static void updateWidgetSet(final String widgetset)
            throws IOException, FileNotFoundException {
        boolean changed = false;

        Map<String, URL> availableWidgetSets = ClassPathExplorer
                .getAvailableWidgetSets();

        URL sourceUrl = availableWidgetSets.get(widgetset);
        if (sourceUrl == null) {
            // find first/default source directory
            sourceUrl = ClassPathExplorer.getDefaultSourceDirectory();
        }

        String widgetsetfilename = sourceUrl.getFile() + "/"
                + widgetset.replace(".", "/") + ".gwt.xml";

        File widgetsetFile = new File(widgetsetfilename);
        if (!widgetsetFile.exists()) {
            // create empty gwt module file
            File parent = widgetsetFile.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException(
                            "Could not create directory for the widgetset: "
                                    + parent.getPath());
                }
            }
            widgetsetFile.createNewFile();
            PrintStream printStream = new PrintStream(new FileOutputStream(
                    widgetsetFile));
            printStream
                    .print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<!DOCTYPE module PUBLIC \"-//Google Inc.//DTD Google Web Toolkit 2.5.1//EN\" \"http://google-web-toolkit.googlecode.com/svn/tags/2.5.1/distro-source/core/src/gwt-module.dtd\">\n");
            printStream.print("<module>\n");
            printStream
                    .print("    <!--\n"
                            + "     Uncomment the following to compile the widgetset for one browser only.\n\n"
                            + "     Multiple browsers can be specified as a comma separated list. The\n"
                            + "     supported user agents at the moment of writing were:\n"
                            + "     ie8,ie9,gecko1_8,safari,opera\n\n"
                            + "     The value gecko1_8 is used for Firefox and safari is used for webkit\n"
                            + "     based browsers including Google Chrome.\n"
                            + "    -->\n"
                            + "    <!-- <set-property name=\"user.agent\" value=\"safari\"/> -->\n\n"
                            + "    <!--\n"
                            + "    To enable SuperDevMode, uncomment this line.\n\n"
                            + "    See https://vaadin.com/wiki/-/wiki/Main/Using%20SuperDevMode for more\n"
                            + "    information and instructions.\n"
                            + "    -->\n"
                            + "    <!-- <set-configuration-property name=\"devModeRedirectEnabled\" value=\"true\" /> -->\n\n");
            printStream.print("\n</module>\n");
            printStream.close();
            changed = true;
        }

        String content = readFile(widgetsetFile);
        if (isEditable(content)) {
            String originalContent = content;

            Collection<String> oldInheritedWidgetsets = getCurrentInheritedWidgetsets(content);

            // add widgetsets that do not exist
            Iterator<String> i = availableWidgetSets.keySet().iterator();
            while (i.hasNext()) {
                String ws = i.next();
                if (ws.equals(widgetset)) {
                    // do not inherit the module itself
                    continue;
                }
                if (!oldInheritedWidgetsets.contains(ws)) {
                    content = addWidgetSet(ws, content);
                }
            }

            for (String ws : oldInheritedWidgetsets) {
                if (!availableWidgetSets.containsKey(ws)) {
                    // widgetset not available in classpath
                    content = removeWidgetSet(ws, content);
                }
            }

            changed = changed || !content.equals(originalContent);
            if (changed) {
                commitChanges(widgetsetfilename, content);
            }
        } else {
            System.out
                    .println("Widgetset is manually edited. Skipping updates.");
        }
    }

    private static boolean isEditable(String content) {
        return !content.contains("WS Compiler: manually edited");
    }

    private static String removeWidgetSet(String ws, String content) {
        return content.replaceFirst("<inherits name=\"" + ws + "\"[^/]*/>", "");
    }

    private static void commitChanges(String widgetsetfilename, String content)
            throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(widgetsetfilename)));
        bufferedWriter.write(content);
        bufferedWriter.close();
    }

    private static String addWidgetSet(String ws, String content) {
        return content.replace("</module>", "\n    <inherits name=\"" + ws
                + "\" />" + "\n</module>");
    }

    private static Collection<String> getCurrentInheritedWidgetsets(
            String content) {
        HashSet<String> hashSet = new HashSet<String>();
        Pattern inheritsPattern = Pattern.compile(" name=\"([^\"]*)\"");

        Matcher matcher = inheritsPattern.matcher(content);

        while (matcher.find()) {
            String gwtModule = matcher.group(1);
            if (isWidgetset(gwtModule)) {
                hashSet.add(gwtModule);
            }
        }
        return hashSet;
    }

    static boolean isWidgetset(String gwtModuleName) {
        return gwtModuleName.toLowerCase().contains("widgetset");
    }

    private static String readFile(File widgetsetFile) throws IOException {
        Reader fi = new FileReader(widgetsetFile);
        BufferedReader bufferedReader = new BufferedReader(fi);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        fi.close();
        return sb.toString();
    }

    private static void printUsage() {
        PrintStream o = System.out;
        o.println(WidgetSetBuilder.class.getSimpleName() + " usage:");
        o.println("    1. Set the same classpath as you will "
                + "have for the GWT compiler.");
        o.println("    2. Give the widgetsetname (to be created or updated)"
                + " as first parameter");
        o.println();
        o.println("All found vaadin widgetsets will be inherited in given widgetset");

    }

}
