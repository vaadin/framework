/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: MediaListImpl.java,v 1.4 2000/04/26 13:40:19 plehegar Exp $
 */
package com.vaadin.sass.internal.parser;

import java.io.Serializable;

import org.w3c.css.sac.SACMediaList;

/**
 * @version $Revision: 1.4 $
 * @author Philippe Le Hegaret
 */
public class MediaListImpl implements SACMediaList, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String[] array = new String[10];
    int current;

    @Override
    public int getLength() {
        return current;
    }

    @Override
    public String item(int index) {
        if ((index < 0) || (index >= current)) {
            return null;
        }
        return array[index];
    }

    void addItem(String medium) {
        if (medium.equals("all")) {
            array[0] = "all";
            current = 1;
            return;
        }
        for (int i = 0; i < current; i++) {
            if (medium.equals(array[i])) {
                return;
            }
        }
        if (current == array.length) {
            String[] old = array;
            array = new String[current + current];
            System.arraycopy(old, 0, array, 0, current);
        }
        array[current++] = medium;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        switch (current) {
        case 0:
            return "";
        case 1:
            return array[0];
        default:
            boolean not_done = true;
            int i = 0;
            StringBuffer buf = new StringBuffer(50);
            do {
                buf.append(array[i++]);
                if (i == current) {
                    not_done = false;
                } else {
                    buf.append(", ");
                }
            } while (not_done);
            return buf.toString();
        }
    }
}
