package com.vaadin.tests.applicationservlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@VaadinServletConfiguration(ui = ContextProtocol.class, productionMode = false)
public class VaadinRefreshServlet extends VaadinServlet {

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (request.getRequestURI().contains("/UIDL")) {
            InputStream loginHtml = request.getServletContext()
                    .getResourceAsStream("/statictestfiles/login.html");
            IOUtils.copy(loginHtml, response.getOutputStream());
            return;
        }
        super.service(request, response);
    }
}
