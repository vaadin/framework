package com.vaadin.tests.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
    private String name = "";
    private Set<Role> roles = new HashSet<Role>();

    public User() {
    }

    public User(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns an unmodifiable set of roles. To modify the roles a user has,
     * replace the whole set using {@link #setRoles(Set)}.
     * 
     * @return set of roles (unmodifiable, not null)
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Replaces the set of roles with another collection. User references in
     * roles are automatically updated when setting the roles of a user.
     * 
     * @param roles
     *            non-null set of roles
     */
    public void setRoles(Set<Role> roles) {
        for (Role role : this.roles) {
            role.getUsers().remove(this);
        }
        this.roles = roles;
        for (Role role : this.roles) {
            role.getUsers().add(this);
        }
    }

}
