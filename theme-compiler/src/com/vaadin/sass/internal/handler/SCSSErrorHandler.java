package com.vaadin.sass.internal.handler;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

public class SCSSErrorHandler implements ErrorHandler {

    @Override
    public void error(CSSParseException arg0) throws CSSException {
        System.out.println("Error when parsing file \n" + arg0.getURI()
                + " on line " + arg0.getLineNumber() + ", column "
                + arg0.getColumnNumber());
        System.out.println(arg0.getMessage() + "\n");
    }

    @Override
    public void fatalError(CSSParseException arg0) throws CSSException {
        System.out.println("FATAL Error when parsing file \n" + arg0.getURI()
                + " on line " + arg0.getLineNumber() + ", column "
                + arg0.getColumnNumber());
        System.out.println(arg0.getMessage() + "\n");
    }

    @Override
    public void warning(CSSParseException arg0) throws CSSException {
        System.out.println("Warning when parsing file \n" + arg0.getURI()
                + " on line " + arg0.getLineNumber() + ", column "
                + arg0.getColumnNumber());
        System.out.println(arg0.getMessage() + "\n");
    }

}
