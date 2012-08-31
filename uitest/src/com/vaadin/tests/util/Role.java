package com.vaadin.tests.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Role implements Serializable {
    private String name = "";
    private Set<User> users = new HashSet<User>();

    public Role() {
    }

    public Role(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * In this direction, the users for a role can be queried and the returned
     * collection modified, but the whole collection of users cannot be set
     * directly.
     * 
     * @return set of users having the role (not null)
     */
    public Set<User> getUsers() {
        return users;
    }

}
