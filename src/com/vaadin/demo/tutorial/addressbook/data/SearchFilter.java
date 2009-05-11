package com.vaadin.demo.tutorial.addressbook.data;

import java.io.Serializable;

public class SearchFilter implements Serializable {

    private final String term;
    private final Object propertyId;
    private String searchName;

    public SearchFilter(Object propertyId, String searchTerm, String name) {
        this.propertyId = propertyId;
        term = searchTerm;
        searchName = name;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @return the propertyId
     */
    public Object getPropertyId() {
        return propertyId;
    }

    /**
     * @return the name of the search
     */
    public String getSearchName() {
        return searchName;
    }

    @Override
    public String toString() {
        return getSearchName();
    }

}
