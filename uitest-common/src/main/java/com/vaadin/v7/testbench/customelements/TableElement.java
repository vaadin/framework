package com.vaadin.v7.testbench.customelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.v7.ui.Table")
public class TableElement extends com.vaadin.testbench.elements.TableElement {
    public CollapseMenu openCollapseMenu() {
        getCollapseMenuToggle().click();
        WebElement cm = getDriver()
                .findElement(By.xpath("//*[@id='PID_VAADIN_CM']"));
        return wrapElement(cm, getCommandExecutor()).wrap(CollapseMenu.class);
    }

    public static class CollapseMenu extends ContextMenuElement {
    }

    public WebElement getCollapseMenuToggle() {
        return findElement(By.className("v-table-column-selector"));
    }

    public static class ContextMenuElement extends AbstractElement {

        public WebElement getItem(int index) {
            return findElement(
                    By.xpath(".//table//tr[" + (index + 1) + "]//td/*"));
        }

    }

    public ContextMenuElement getContextMenu() {
        WebElement cm = getDriver().findElement(By.className("v-contextmenu"));
        return wrapElement(cm, getCommandExecutor())
                .wrap(ContextMenuElement.class);
    }

}
