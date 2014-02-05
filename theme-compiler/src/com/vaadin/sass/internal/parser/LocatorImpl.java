/*
 * Copyright 2000-2013 Vaadin Ltd.
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
/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: LocatorImpl.java,v 1.2 2000/02/14 16:59:06 plehegar Exp $
 */
package com.vaadin.sass.internal.parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.css.sac.Locator;

import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;

/**
 * @version $Revision: 1.2 $
 * @author Philippe Le Hegaret
 */
public class LocatorImpl implements Locator {

    // W3C DEBUG mode
    private static boolean W3CDebug;
    static {
        try {
            W3CDebug = (Boolean.getBoolean("debug")
                    || Boolean
                            .getBoolean("org.w3c.flute.parser.LocatorImpl.debug")
                    || Boolean.getBoolean("org.w3c.flute.parser.debug")
                    || Boolean.getBoolean("org.w3c.flute.debug")
                    || Boolean.getBoolean("org.w3c.debug") || Boolean
                    .getBoolean("org.debug"));
        } catch (Exception e) {
            // nothing
        }
    }

    String uri;
    int line;
    int column;

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public int getLineNumber() {
        return line;
    }

    @Override
    public int getColumnNumber() {
        return column;
    }

    /**
     * Creates a new LocatorImpl
     */
    public LocatorImpl(Parser p) {
        if (W3CDebug) {
            log("LocatorImpl::newLocator(" + p + ");");
        }
        uri = p.source.getURI();
        line = p.token.beginLine;
        column = p.token.beginColumn;
    }

    /**
     * Reinitializes a LocatorImpl
     */
    public LocatorImpl(Parser p, Token tok) {
        if (W3CDebug) {
            log("LocatorImpl::newLocator(" + p + ", " + tok + ");");
        }
        uri = p.source.getURI();
        line = tok.beginLine;
        column = tok.beginColumn;
    }

    /**
     * Reinitializes a LocatorImpl
     */
    public LocatorImpl(Parser p, int line, int column) {
        if (W3CDebug) {
            log("LocatorImpl::newLocator(" + p + ", " + line + ", " + column
                    + ");");
        }
        uri = p.source.getURI();
        this.line = line;
        this.column = column;
    }

    /**
     * Reinitializes a LocatorImpl
     */
    public LocatorImpl reInit(Parser p) {
        if (W3CDebug) {
            log("LocatorImpl::reInit(" + p + ");");
        }
        uri = p.source.getURI();
        line = p.token.beginLine;
        column = p.token.beginColumn;
        return this;
    }

    /**
     * Reinitializes a LocatorImpl
     */
    public LocatorImpl reInit(Parser p, Token tok) {
        if (W3CDebug) {
            log("LocatorImpl::reInit(" + p + ", " + tok + ");");
        }
        uri = p.source.getURI();
        line = tok.beginLine;
        column = tok.beginColumn;
        return this;
    }

    /**
     * Reinitializes a LocatorImpl
     */
    public LocatorImpl reInit(Parser p, int line, int column) {
        if (W3CDebug) {
            log("LocatorImpl::reInit(" + p + ", " + line + ", " + column + ");");
        }
        uri = p.source.getURI();
        this.line = line;
        this.column = column;
        return this;
    }

    private void log(String msg) {
        Logger.getLogger(SCSSDocumentHandlerImpl.class.getName()).log(
                Level.SEVERE, msg);
    }
}
