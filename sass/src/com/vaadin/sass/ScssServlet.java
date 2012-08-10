/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScssServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String cssPath = req.getRequestURI();
        if (cssPath.endsWith(".css")) {
            File cssFile = new File(cssPath);
            if (cssFile.exists()) {

            } else {
                String scssPath = cssPath.replace(".css", ".scss");
                File scssFile = new File(scssPath);
                if (scssFile.exists()) {
                    ScssStylesheet scss = ScssStylesheet.get(new File(cssPath));
                    try {
                        scss.compile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resp.setContentType("text/css");
                    OutputStream fout = resp.getOutputStream();
                    OutputStream bos = new BufferedOutputStream(fout);
                    OutputStreamWriter outputwriter = new OutputStreamWriter(
                            bos);
                    outputwriter.write(scss.toString());
                }

            }
        }
    }
}
