/**
 * 
 */
package com.itmill.toolkit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates portlet.xml, liferay-portlet.xml, liferay-display.xml from web.xml.
 * Currently uses regular expressions to avoid dependencies; does not strictly
 * adhere to xml rules, but should work with a 'normal' web.xml.
 * 
 * To be included, the servlet-mapping must include a special comment: <!--
 * portlet --> If the portlet requires some special styles (i.e height): <!--
 * portlet style=height:400px -->
 * 
 * @author marc
 */
public class PortletConfigurationGenerator {
    // can be changed for debugging:
    private static final String WEB_XML_FILE = "web.xml";
    private static final String PORTLET_XML_FILE = "portlet.xml";
    private static final String LIFERAY_PORTLET_XML_FILE = "liferay-portlet.xml";
    private static final String LIFERAY_DISPLAY_XML_FILE = "liferay-display.xml";

    // "templates" follow;
    private static final String PORTLET_XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<portlet-app\n"
            + "        xmlns=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\"\n"
            + "        version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "        xsi:schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\">\n";
    private static final String PORTLET_XML_SECTION = "        <portlet>\n"
            + "                <portlet-name>%PORTLETNAME%</portlet-name>\n"
            + "                <display-name>IT Mill Toolkit %NAME%</display-name>\n"
            + "                <portlet-class>com.itmill.toolkit.terminal.gwt.server.ApplicationPortlet</portlet-class>\n"
            + "                <init-param>\n"
            + "                        <name>application</name>\n"
            + "                        <value>%URL%</value>\n"
            + "                </init-param>\n"
            + "                %EXTRAPARAMS%\n"
            + "                <supports>\n"
            + "                        <mime-type>text/html</mime-type>\n"
            + "                        <portlet-mode>view</portlet-mode>\n"
            + "                        <portlet-mode>edit</portlet-mode>\n"
            + "                        <portlet-mode>help</portlet-mode>\n"
            + "                </supports>\n"
            + "                <portlet-info>\n"
            + "                        <title>%NAME%</title>\n"
            + "                        <short-title>%NAME%</short-title>\n"
            + "                </portlet-info>\n" + "                \n"
            + "                <security-role-ref>\n"
            + "                        <role-name>administrator</role-name>\n"
            + "                </security-role-ref>\n"
            + "                <security-role-ref>\n"
            + "                        <role-name>guest</role-name>\n"
            + "                </security-role-ref>\n"
            + "                <security-role-ref>\n"
            + "                        <role-name>power-user</role-name>\n"
            + "                </security-role-ref>\n"
            + "                <security-role-ref>\n"
            + "                        <role-name>user</role-name>\n"
            + "                </security-role-ref>\n" + "        </portlet>\n";
    private static final String PORTLET_XML_FOOT = "        %CONTEXTPARAMS%\n"
            + "        <container-runtime-option>\n"
            + "                <name>javax.portlet.escapeXml</name>\n"
            + "                <value>false</value>\n"
            + "        </container-runtime-option>\n" + "</portlet-app>";

    private static final String LIFERAY_PORTLET_XML_HEAD = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//DTD Portlet Application 4.3.0//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_4_3_0.dtd\">\n"
            + "\n" + "<liferay-portlet-app>\n" + "";
    private static final String LIFERAY_PORTLET_XML_SECTION = "        <portlet>\n"
            + "                <portlet-name>%PORTLETNAME%</portlet-name>\n"
            + "                <instanceable>true</instanceable>       \n"
            + "                <ajaxable>false</ajaxable>\n"
            + "        </portlet>\n" + "";
    private static final String LIFERAY_PORTLET_XML_FOOT = "    \n"
            + "        <role-mapper>\n"
            + "                <role-name>administrator</role-name>\n"
            + "                <role-link>Administrator</role-link>\n"
            + "        </role-mapper>\n" + "        <role-mapper>\n"
            + "                <role-name>guest</role-name>\n"
            + "                <role-link>Guest</role-link>\n"
            + "        </role-mapper>\n" + "        <role-mapper>\n"
            + "                <role-name>power-user</role-name>\n"
            + "                <role-link>Power User</role-link>\n"
            + "        </role-mapper>\n" + "        <role-mapper>\n"
            + "                <role-name>user</role-name>\n"
            + "                <role-link>User</role-link>\n"
            + "        </role-mapper>\n" + "        \n"
            + "</liferay-portlet-app>";
    private static final String LIFERAY_DISPLAY_XML_HEAD = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display 4.0.0//EN\" \"http://www.liferay.com/dtd/liferay-display_4_0_0.dtd\">\n"
            + "\n"
            + "<display>\n"
            + "        <category name=\"IT Mill Toolkit\">\n" + "";
    private static final String LIFERAY_DISPLAY_XML_SECTION = "                <portlet id=\"%PORTLETNAME%\" />\n";
    private static final String LIFERAY_DISPLAY_XML_FOOT = "\n"
            + "        </category>\n" + "</display>";

    /**
     * @param args
     *                <path to directory with web.xml> [widgetset to use]
     */
    public static void main(String[] args) {
        if (args.length < 1 || !new File(args[0]).isDirectory()) {
            System.err
                    .println("Usage: PortletConfigurationGenerator <directory> [widgetset]");
            return;
        }

        String widgetset = "";
        if (args.length > 1) {
            widgetset = "<context-param><name>widgetset</name><value>"
                    + args[1] + "</value></context-param>";
        }

        /*
         * Read web.xml
         */
        File dir = new File(args[0]);
        File webxmlFile = new File(dir.getAbsolutePath() + File.separatorChar
                + WEB_XML_FILE);
        String webXml = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(webxmlFile));
            String line = in.readLine();
            while (line != null) {
                webXml += line;
                line = in.readLine();
            }
        } catch (FileNotFoundException e1) {
            System.out.println(webxmlFile + " not found!");
            return;
        } catch (IOException e2) {
            System.out.println("IOException while reading " + webxmlFile);
            webXml = null;
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e1) {
            System.out.println("IOException while closing " + webxmlFile);
        }
        if (webXml == null) {
            System.out.println("Could not read web.xml!");
            return;
        }

        /*
         * Open outputs
         */

        // Open portlet.xml
        File portletXmlFile = new File(args[0] + File.separatorChar
                + PORTLET_XML_FILE);
        OutputStreamWriter pout = null;
        try {
            pout = new OutputStreamWriter(new FileOutputStream(portletXmlFile),
                    Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            System.out.println(portletXmlFile + " not found!");
        }
        // open liferay-portlet.xml
        File liferayPortletXmlFile = new File(args[0] + File.separatorChar
                + LIFERAY_PORTLET_XML_FILE);
        OutputStreamWriter lpout = null;
        try {
            lpout = new OutputStreamWriter(new FileOutputStream(
                    liferayPortletXmlFile), Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            System.out.println(liferayPortletXmlFile + " not found!");
        }
        // open liferay-display.xml
        File liferayDisplayXmlFile = new File(args[0] + File.separatorChar
                + LIFERAY_DISPLAY_XML_FILE);
        OutputStreamWriter ldout = null;
        try {
            ldout = new OutputStreamWriter(new FileOutputStream(
                    liferayDisplayXmlFile), Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            System.out.println(liferayDisplayXmlFile + " not found!");
        }

        if (pout != null && lpout != null && ldout != null) {

            String pstring = PORTLET_XML_HEAD;
            String lpstring = LIFERAY_PORTLET_XML_HEAD;
            String ldstring = LIFERAY_DISPLAY_XML_HEAD;

            Pattern p1 = Pattern
                    .compile("<servlet-mapping>.*?<servlet-name>(.*?)<\\/servlet-name>.*?<url-pattern>(.*?)<\\/url-pattern>(.*?)<\\/servlet-mapping>");
            Pattern p2 = Pattern
                    .compile(".*?<!--\\s+portlet\\s?(style=\\S+)?\\s+-->.*?");
            Matcher m = p1.matcher(webXml);
            while (m.find()) {
                if (m.groupCount() < 3) {
                    // don't include
                    continue;
                }
                Matcher m2 = p2.matcher(m.group(3));
                if (!m2.find()) {
                    // don't include
                    continue;
                }

                String style = "";
                if (m2.groupCount() == 1 && m2.group(1) != null) {
                    style = "<init-param><name>style</name><value>"
                            + m2.group(1) + "</value></init-param>";
                }

                String name = m.group(1);
                // remove leading- and trailing whitespace
                name = name.replaceAll("^\\s*", "");
                name = name.replaceAll("\\s*$", "");
                String pname = name + "Portlet";
                String url = m.group(2);
                // remove leading- and trailing whitespace
                url = url.replaceAll("^\\s*", "");
                url = url.replaceAll("\\s*$", "");
                if (url.startsWith("/")) {
                    url = url.substring(1);
                }
                if (url.endsWith("*")) {
                    url = url.substring(0, url.length() - 1);
                }
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }

                System.out.println("Mapping " + pname + " to " + url);

                String s = PORTLET_XML_SECTION;
                s = s.replaceAll("%NAME%", name);
                s = s.replaceAll("%PORTLETNAME%", pname);
                s = s.replaceAll("%URL%", url);
                s = s.replaceAll("%EXTRAPARAMS%", style);

                pstring += s;

                s = LIFERAY_PORTLET_XML_SECTION;
                s = s.replaceAll("%NAME%", name);
                s = s.replaceAll("%PORTLETNAME%", pname);
                s = s.replaceAll("%URL%", url);
                lpstring += s;

                s = LIFERAY_DISPLAY_XML_SECTION;
                s = s.replaceAll("%NAME%", name);
                s = s.replaceAll("%PORTLETNAME%", pname);
                s = s.replaceAll("%URL%", url);
                ldstring += s;

            }

            pstring += PORTLET_XML_FOOT
                    .replaceAll("%CONTEXTPARAMS%", widgetset);
            lpstring += LIFERAY_PORTLET_XML_FOOT;
            ldstring += LIFERAY_DISPLAY_XML_FOOT;

            try {
                pout.write(pstring);
                lpout.write(lpstring);
                ldout.write(ldstring);
            } catch (IOException e) {
                System.out.println("Write FAILED:" + e);
            }

        }

        try {
            if (pout != null) {
                pout.close();
            }
            if (lpout != null) {
                lpout.close();
            }
            if (ldout != null) {
                ldout.close();
            }
        } catch (IOException e) {
            System.out.println("Close FAILED: " + e);
        }
        System.out.println("Done.");
    }
}
