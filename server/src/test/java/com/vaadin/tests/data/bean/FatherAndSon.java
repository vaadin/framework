package com.vaadin.tests.data.bean;

import java.io.Serializable;

public class FatherAndSon implements Serializable {
    private String firstName;
    private String lastName;
    private FatherAndSon father;
    private FatherAndSon son;

    public FatherAndSon() {

    }

    @Override
    public String toString() {
        return "FatherAndSon [firstName=" + firstName + ", lastName=" + lastName
                + ", father=" + father + ", son=" + son + "]";
    }

    public FatherAndSon(String firstName, String lastName, FatherAndSon father,
            FatherAndSon son) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.father = father;
        if (this.father != null)
            this.father.setSon(this);
        else
            this.son = son;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public FatherAndSon getFather() {
        return father;
    }

    public void setFather(FatherAndSon father) {
        this.father = father;
    }

    public FatherAndSon getSon() {
        return son;
    }

    public void setSon(FatherAndSon son) {
        this.son = son;
    }

}
