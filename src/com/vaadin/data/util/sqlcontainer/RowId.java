/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer;

import java.io.Serializable;

/**
 * RowId represents identifiers of a single database result set row.
 * 
 * The data structure of a RowId is an Object array which contains the values of
 * the primary key columns of the identified row. This allows easy equals()
 * -comparison of RowItems.
 */
public class RowId implements Serializable {
    private static final long serialVersionUID = -3161778404698901258L;
    protected Object[] id;

    /**
     * Prevent instantiation without required parameters.
     */
    protected RowId() {
    }

    public RowId(Object[] id) {
        if (id == null) {
            throw new IllegalArgumentException("id parameter must not be null!");
        }
        this.id = id;
    }

    public Object[] getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int result = 31;
        if (id != null) {
            for (Object o : id) {
                if (o != null) {
                    result += o.hashCode();
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RowId)) {
            return false;
        }
        Object[] compId = ((RowId) obj).getId();
        if (id == null && compId == null) {
            return true;
        }
        if (id.length != compId.length) {
            return false;
        }
        for (int i = 0; i < id.length; i++) {
            if ((id[i] == null && compId[i] != null)
                    || (id[i] != null && !id[i].equals(compId[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < id.length; i++) {
            s.append(id[i]);
            if (i < id.length - 1) {
                s.append("/");
            }
        }
        return s.toString();
    }
}
