package com.vaadin.testbench.customelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Table")
public class TableElement extends com.vaadin.testbench.elements.TableElement {
    @Override
    public CollapseMenu openCollapseMenu() {
        getCollapseMenuToggle().click();
        WebElement cm = getDriver()
                .findElement(By.xpath("//*[@id='PID_VAADIN_CM']"));
        return wrapElement(cm, getCommandExecutor()).wrap(CollapseMenu.class);
    }

    @Override
    public WebElement getCollapseMenuToggle() {
        return findElement(By.className("v-table-column-selector"));
    }

}
