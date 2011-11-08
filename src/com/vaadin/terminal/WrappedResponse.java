package com.vaadin.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface WrappedResponse {

    public void setStatus(int statusCode);

    public void setContentType(String contentType);

    public void setHeader(String name, String value);

    public OutputStream getOutputStream() throws IOException;

    public PrintWriter getWriter() throws IOException;

}
