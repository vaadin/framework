/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.menubar;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Tests declarative support for menu bars.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class MenuBarDeclarativeTest extends DeclarativeTestBase<MenuBar> {

    @Test
    // #16328
    public void testReadWrite() throws IOException {
        String design = "<v-menu-bar auto-open='true' tabindex=5>"
                + "<menu checkable='true'>Save</menu>"
                + "<menu description='Open a file'>Open</menu>"
                + "<menu disabled='true'>Close</menu>"
                + "<menu icon='http://foo.bar/ico.png'>Help</menu>"
                + "<menu visible='false'>About</menu>"
                + "<menu>Sub<menu>Item</menu></menu>"
                + "<menu more>WTF?!</menu>" + "</v-menu-bar>";
        MenuBar bar = new MenuBar();
        bar.setAutoOpen(true);
        bar.setHtmlContentAllowed(true);
        bar.setTabIndex(5);

        bar.addItem("Save", null).setCheckable(true);
        bar.addItem("Open", null).setDescription("Open a file");
        bar.addItem("Close", null).setEnabled(false);
        bar.addItem("Help", null).setIcon(
                new ExternalResource("http://foo.bar/ico.png"));
        bar.addItem("About", null).setVisible(false);

        bar.addItem("Sub", null).addItem("Item", null);

        bar.setMoreMenuItem(bar.new MenuItem("WTF?!", null, null));

        testWrite(design, bar);
        testRead(design, bar);
    }

    @Test
    // #16328
    public void testTicketSpec1() throws IOException {
        String design = "<v-menu-bar auto-open='true' plain-text tabindex=5> "
                + "<menu>File"
                + "<menu>Save</menu>"
                + "<menu icon=\"theme://../runo/icons/16/folder.png\">Open</menu>"
                + "<menu separator='true' />"
                + "<menu disabled='true'>Exit</menu>"
                + "<menu visible='false'>Not for everybody</menu>"
                + "</menu>"
                + "<menu description=\"This contains many items in sub menus\">Other"
                + "<menu style-name=\"fancy\">Sub"
                + "<menu checkable='true' checked='true'>Option 1 - no <b>html</b></menu>"
                + "<menu checkable='true'>Option 2</menu>"
                + "<menu checkable='true'>Option 3</menu>" //
                + "</menu>" //
                + "</menu>" //
                + "<menu more icon=\"theme://icon.png\">foo</menu>"
                + "</v-menu-bar>";
        // for one reason or another, no component has a correct .equals
        // implementation, which makes tests a bit annoying
        MenuBar menuBar = new MenuBar();
        menuBar.setHtmlContentAllowed(false);
        menuBar.setTabIndex(5);
        menuBar.setAutoOpen(true);
        // File menu
        MenuItem fileMenu = menuBar.addItem("File", null);
        fileMenu.addItem("Save", null);
        fileMenu.addItem("Open", new ThemeResource(
                "../runo/icons/16/folder.png"), null);
        fileMenu.addSeparator();
        fileMenu.addItem("Exit", null).setEnabled(false);
        fileMenu.addItem("Not for everybody", null).setVisible(false);
        MenuItem otherMenu = menuBar.addItem("Other", null);
        otherMenu.setDescription("This contains many items in sub menus");
        MenuItem subMenu = otherMenu.addItem("Sub", null);
        subMenu.setStyleName("fancy");
        MenuItem option1 = subMenu.addItem("Option 1 - no <b>html</b>", null);
        option1.setCheckable(true);
        option1.setChecked(true);
        subMenu.addItem("Option 2", null).setCheckable(true);
        subMenu.addItem("Option 3", null).setCheckable(true);
        menuBar.setMoreMenuItem(null);
        MenuItem moreMenu = menuBar.getMoreMenuItem();
        moreMenu.setIcon(new ThemeResource("icon.png"));
        moreMenu.setText("foo");
        testRead(design, menuBar);
        testWrite(design, menuBar);
    }

    @Test
    // #16328
    public void testTicketSpec2() throws IOException {
        String design = "<v-menu-bar>"
                + "<menu><b>File</b>"
                + "<menu><font style=\"color: red\">Save</font></menu>"
                + "<menu icon=\"theme://../runo/icons/16/folder.png\">Open</menu>"
                + "<menu separator='true' />"
                + "<menu disabled='true'>Exit</menu>" //
                + "</menu></v-menu-bar>";
        MenuBar menuBar = new MenuBar();
        menuBar.setHtmlContentAllowed(true);
        MenuItem fileMenu = menuBar.addItem("<b>File</b>", null);
        fileMenu.addItem("<font style=\"color: red\">Save</font>", null);
        fileMenu.addItem("Open", new ThemeResource(
                "../runo/icons/16/folder.png"), null);
        fileMenu.addSeparator();
        fileMenu.addItem("Exit", null).setEnabled(false);
        testRead(design, menuBar);
        testWrite(design, menuBar);
    }

    @Override
    public MenuBar testRead(String design, MenuBar expected) {
        MenuBar result = super.testRead(design, expected);

        List<MenuItem> expectedMenuItems = expected.getItems();
        List<MenuItem> actualMenuItems = result.getItems();
        Assert.assertEquals("Different amount of menu items",
                expectedMenuItems.size(), actualMenuItems.size());

        for (int i = 0; i < expectedMenuItems.size(); ++i) {
            compareMenus(expectedMenuItems.get(i), actualMenuItems.get(i));
        }

        return result;
    }

    private void compareMenus(MenuItem expected, MenuItem actual) {
        String baseError = "Error Comparing MenuItem " + expected.getText()
                + ": ";
        Assert.assertEquals(baseError + "Visibile", expected.isVisible(),
                actual.isVisible());
        Assert.assertEquals(baseError + "Checkable", expected.isCheckable(),
                actual.isCheckable());
        Assert.assertEquals(baseError + "Checked", expected.isChecked(),
                actual.isChecked());
        Assert.assertEquals(baseError + "Separator", expected.isSeparator(),
                actual.isSeparator());
        Assert.assertEquals(baseError + "Enabled", expected.isEnabled(),
                actual.isEnabled());

        Assert.assertEquals(baseError + "Text", expected.getText(),
                actual.getText());
        Assert.assertEquals(baseError + "Description",
                expected.getDescription(), actual.getDescription());
        Assert.assertEquals(baseError + "Style Name", expected.getStyleName(),
                actual.getStyleName());

        if (expected.getIcon() != null) {
            Assert.assertNotNull(baseError + "Icon was null", actual.getIcon());
        } else {
            if (actual.getIcon() != null) {
                Assert.fail(baseError + "Icon should've been null");
            }
        }

        Assert.assertEquals(baseError + "Has Children", expected.hasChildren(),
                actual.hasChildren());
        if (expected.hasChildren()) {
            List<MenuItem> children = expected.getChildren();
            List<MenuItem> actualChildren = actual.getChildren();
            Assert.assertEquals(baseError + "Child count", children.size(),
                    actualChildren.size());
            for (int i = 0; i < children.size(); ++i) {
                compareMenus(children.get(i), actualChildren.get(i));
            }
        }
    }
}