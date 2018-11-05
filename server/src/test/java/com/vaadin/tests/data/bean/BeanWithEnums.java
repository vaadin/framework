package com.vaadin.tests.data.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BeanWithEnums implements Serializable {
    private Set<TestEnum> enums = new HashSet<>();

    public Set<TestEnum> getEnums() {
        return enums;
    }

    public void setEnums(Set<TestEnum> enums) {
        this.enums = enums;
    }
}
