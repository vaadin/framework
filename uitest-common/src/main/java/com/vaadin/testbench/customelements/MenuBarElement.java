package com.vaadin.testbench.customelements;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.MenuBar")
public class MenuBarElement
        extends com.vaadin.testbench.elements.MenuBarElement {

    public void openMenuPath(String... captions) {
        for (String c : captions) {
            findElement(By.vaadin("#" + c)).click();
        }
    }
}
