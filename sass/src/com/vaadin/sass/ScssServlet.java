/*
 * Copyright 2011 Vaadin Ltd.
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
