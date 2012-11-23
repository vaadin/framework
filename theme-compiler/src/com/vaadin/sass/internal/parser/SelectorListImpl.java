/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: SelectorListImpl.java,v 1.1 2000/08/07 01:16:21 plehegar Exp $
 */
package com.vaadin.sass.internal.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

/**
 * @version $Revision: 1.1 $
 * @author Philippe Le Hegaret
 */
public class SelectorListImpl implements SelectorList {

    Selector[] selectors = new Selector[5];
    int current;

    @Override
    public Selector item(int index) {
        if ((index < 0) || (index >= current)) {
            return null;
        }
        return selectors[index];
    }

    public Selector itemSelector(int index) {
        if ((index < 0) || (index >= current)) {
            return null;
        }
        return selectors[index];
    }

    @Override
    public int getLength() {
        return current;
    }

    public void addSelector(Selector selector) {
        if (current == selectors.length) {
            Selector[] old = selectors;
            selectors = new Selector[old.length + old.length];
            System.arraycopy(old, 0, selectors, 0, old.length);
        }
        selectors[current++] = selector;
    }

    public void replaceSelector(int index, Selector selector) {
        if ((index >= 0) && (index < current)) {
            selectors[index] = selector;
        }
    }
}
