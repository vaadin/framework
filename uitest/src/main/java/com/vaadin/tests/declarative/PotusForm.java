package com.vaadin.tests.declarative;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.PropertyId;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

@DesignRoot
public class PotusForm extends VerticalLayout {

    @PropertyId("firstName")
    public TextField firstName;
    @PropertyId("lastName")
    public TextField lastName;
    @PropertyId("party")
    public ComboBox party;
    @PropertyId("tookOffice")
    public DateField tookOffice;
    @PropertyId("leftOffice")
    public DateField leftOffice;

    public Button save;
    public Button revert;
    public Button delete;

    public PotusForm() {
        Design.read(this);
        party.addItems("Democratic Party");
        party.addItems("Republican Party");
        party.addItems("Independent");
    }
}
