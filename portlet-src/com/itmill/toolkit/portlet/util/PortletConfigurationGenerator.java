/**
 * 
 */
package com.itmill.toolkit.portlet.util;

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
 * @author marc
 */
public class PortletConfigurationGenerator {
    // can be changed for debugging:
    private static final String WEB_XML_FILE = "web.xml";
    private static final String PORTLET_XML_FILE = "portlet.xml";
    private static final String LIFERAY_PORTLET_XML_FILE = "liferay-portlet.xml";
    private static final String LIFERAY_DISPLAY_XML_FILE = "liferay-display.xml";

    // "templates" follow;
    private static final String PORTLET_XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            + "<portlet-app\r\n"
            + "        xmlns=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\"\r\n"
            + "        version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
            + "        xsi:schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\">\r\n";
    private static final String PORTLET_XML_SECTION = "        <portlet>\r\n"
            + "                <portlet-name>%PORTLETNAME%</portlet-name>\r\n"
            + "                <display-name>IT Mill Toolkit %NAME%</display-name>\r\n"
            + "                <portlet-class>com.itmill.toolkit.terminal.gwt.server.ApplicationPortlet</portlet-class>\r\n"
            + "                <init-param>\r\n"
            + "                        <name>application</name>\r\n"
            + "                        <value>%URL%</value>\r\n"
            + "                </init-param>\r\n"
            + "                <supports>\r\n"
            + "                        <mime-type>text/html</mime-type>\r\n"
            + "                        <portlet-mode>view</portlet-mode>\r\n"
            + "                        <portlet-mode>edit</portlet-mode>\r\n"
            + "                        <portlet-mode>help</portlet-mode>\r\n"
            + "                </supports>\r\n"
            + "                <portlet-info>\r\n"
            + "                        <title>IT Mill Toolkit %NAME%</title>\r\n"
            + "                        <short-title>%NAME%</short-title>\r\n"
            + "                </portlet-info>\r\n"
            + "                \r\n"
            + "                <security-role-ref>\r\n"
            + "                        <role-name>administrator</role-name>\r\n"
            + "                </security-role-ref>\r\n"
            + "                <security-role-ref>\r\n"
            + "                        <role-name>guest</role-name>\r\n"
            + "                </security-role-ref>\r\n"
            + "                <security-role-ref>\r\n"
            + "                        <role-name>power-user</role-name>\r\n"
            + "                </security-role-ref>\r\n"
            + "                <security-role-ref>\r\n"
            + "                        <role-name>user</role-name>\r\n"
            + "                </security-role-ref>\r\n"
            + "        </portlet>\r\n";
    private static final String PORTLET_XML_FOOT = "</portlet-app>";

    private static final String LIFERAY_PORTLET_XML_HEAD = "<?xml version=\"1.0\"?>\r\n"
            + "<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//DTD Portlet Application 4.3.0//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_4_3_0.dtd\">\r\n"
            + "\r\n" + "<liferay-portlet-app>\r\n" + "";
    private static final String LIFERAY_PORTLET_XML_SECTION = "        <portlet>\r\n"
            + "                <portlet-name>%PORTLETNAME%</portlet-name>\r\n"
            + "                <instanceable>true</instanceable>       \r\n"
            + "                <ajaxable>false</ajaxable>\r\n"
            + "        </portlet>\r\n" + "";
    private static final String LIFERAY_PORTLET_XML_FOOT = "    \r\n"
            + "        <role-mapper>\r\n"
            + "                <role-name>administrator</role-name>\r\n"
            + "                <role-link>Administrator</role-link>\r\n"
            + "        </role-mapper>\r\n" + "        <role-mapper>\r\n"
            + "                <role-name>guest</role-name>\r\n"
            + "                <role-link>Guest</role-link>\r\n"
            + "        </role-mapper>\r\n" + "        <role-mapper>\r\n"
            + "                <role-name>power-user</role-name>\r\n"
            + "                <role-link>Power User</role-link>\r\n"
            + "        </role-mapper>\r\n" + "        <role-mapper>\r\n"
            + "                <role-name>user</role-name>\r\n"
            + "                <role-link>User</role-link>\r\n"
            + "        </role-mapper>\r\n" + "        \r\n"
            + "</liferay-portlet-app>";
    private static final String LIFERAY_DISPLAY_XML_HEAD = "<?xml version=\"1.0\"?>\r\n"
            + "<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display 4.0.0//EN\" \"http://www.liferay.com/dtd/liferay-display_4_0_0.dtd\">\r\n"
            + "\r\n"
            + "<display>\r\n"
            + "        <category name=\"IT Mill Toolkit\">\r\n" + "";
    private static final String LIFERAY_DISPLAY_XML_SECTION = "                <portlet id=\"%PORTLETNAME%\" />\r\n";
    private static final String LIFERAY_DISPLAY_XML_FOOT = "\r\n"
            + "        </category>\r\n" + "</display>";

    /**
     * @param args
     *                <path to directory with web.xml>
     */
    public static void main(String[] args) {
        if (args.length < 1 || !new File(args[0]).isDirectory()) {
            System.err
                    .println("Usage: PortletConfigurationGenerator <directory>");
            return;
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

            Pattern p = Pattern
                    .compile(
                            "<servlet-mapping>.*?<servlet-name>(.*?)<\\/servlet-name>.*?<url-pattern>(.*?)<\\/url-pattern>.*?<\\/servlet-mapping>",
                            Pattern.MULTILINE);
            Matcher m = p.matcher(webXml);
            while (m.find()) {
                if (m.groupCount() != 2) {
                    System.out
                            .println("Could not find servlet-name and url-pattern for: "
                                    + m.group());
                    continue;
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

            pstring += PORTLET_XML_FOOT;
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
