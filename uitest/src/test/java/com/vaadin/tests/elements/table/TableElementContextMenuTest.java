package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableElementContextMenuTest extends MultiBrowserTest {

    private TableElement tableElement;

    @Before
    public void init() {
        openTestURL();
        tableElement = $(TableElement.class).first();
    }

    @Test
    public void tableContextMenu_menuOpenFetchMenu_contextMenuFetchedCorrectly() {
        tableElement.contextClick();
        TableElement.ContextMenuElement contextMenu = tableElement
                .getContextMenu();
        Assert.assertNotNull(
                "There is no context menu open by tableElement.contextClick()",
                contextMenu);
    }

    @Test(expected = NoSuchElementException.class)
    public void tableContextMenu_menuClosedfetchContextMenu_exceptionThrown() {
        tableElement.getContextMenu();
    }
}
