package com.vaadin.tests.customelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ProgressBar")
public class CustomProgressBarElement extends ProgressBarElement {

    public double getValue() {
        WebElement indicator = findElement(
                By.className("v-progressbar-indicator"));
        String width = getStyleAttribute(indicator, "width");
        if (!width.endsWith("%")) {
            return 0;
        }

        return Double.parseDouble(width.replace("%", "")) / 100.0;
    }

    /**
     * @since 7.5.6
     * @param indicator
     * @param string
     * @return
     */
    private String getStyleAttribute(WebElement element, String styleName) {
        String style = element.getAttribute("style");
        String[] styles = style.split(";");
        for (String s : styles) {
            if (s.startsWith(styleName + ":")) {
                return s.substring(styleName.length() + 1).trim();
            }
        }

        return null;
    }

}
