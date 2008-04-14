/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusServlet extends HttpServlet {

    private static final long serialVersionUID = -6764317622536660947L;

    public static DateFormat dfHuman = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * Version number of this release. For example "5.0.0".
     */
    public static final String VERSION;

    /**
     * Major version number. For example 5 in 5.1.0.
     */
    public static final int VERSION_MAJOR;

    /**
     * Minor version number. For example 1 in 5.1.0.
     */
    public static final int VERSION_MINOR;

    /**
     * Builds number. For example 0-custom_tag in 5.0.0-custom_tag.
     */
    public static final String VERSION_BUILD;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("@VERSION@".equals("@" + "VERSION" + "@")) {
            VERSION = "5.9.9-INTERNAL-NONVERSIONED-DEBUG-BUILD";
        } else {
            VERSION = "@VERSION@";
        }
        final String[] digits = VERSION.split("\\.");
        VERSION_MAJOR = Integer.parseInt(digits[0]);
        VERSION_MINOR = Integer.parseInt(digits[1]);
        VERSION_BUILD = digits[2];
    }

    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Writer w = response.getWriter();

        // not cacheable
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html");

        String p = "";
        p += "<p>StatusServlet " + dfHuman.format(new Date()) + "</p>";
        for (int i = 0; i < 30; i++)
            System.gc();
        long inUse = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                .freeMemory());
        p += "<p>Memory:<br />\n<memused>" + inUse
                + "</memused> (Used)<br />\n" + "<memtotal>"
                + Runtime.getRuntime().totalMemory()
                + "<memtotal> (Total)<br />\n" + "<memfree>"
                + Runtime.getRuntime().freeMemory() + "<memfree> (Free)</p>\n";

        w.write("<html>\n" + p + "</html>\n");
    }
}
