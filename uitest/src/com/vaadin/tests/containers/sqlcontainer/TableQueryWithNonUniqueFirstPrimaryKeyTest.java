package com.vaadin.tests.containers.sqlcontainer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableQueryWithNonUniqueFirstPrimaryKeyTest extends
        MultiBrowserTest {

    private static final String[] DATA = { "TARUSCIO GIOVANNI",
            "RUSSO GAETANO AUTORICAMBI", "AMORUSO LUIGI SRL", "CARUSO ROCCO",
            "F.LLI RUSSO DI GAETANO RUSSO & C", "RUSSO GIUSEPPE",
            "TRUSCELLI ANTONIO", "CARUSO CALOGERO" };

    @Test
    public void testComboBoxSuggestionsListedCorrectly() throws Exception {
        openTestURL();
        $(ComboBoxElement.class).first().findElement(By.vaadin("#textbox"))
                .sendKeys("rus", Keys.ENTER);

        List<String> result = new ArrayList<String>();

        // pick list items that are shown in suggestion popup
        List<WebElement> elems = findElements(By
                .cssSelector("td[role=\"listitem\"]"));
        Assert.assertEquals("not enough suggestions shown", DATA.length,
                elems.size());

        for (WebElement elem : elems) {
            result.add(elem.getText());
        }

        Assert.assertArrayEquals("popup items not what they should be", DATA,
                result.toArray());

    }
}
