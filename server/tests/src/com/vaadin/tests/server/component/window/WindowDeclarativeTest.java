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
package com.vaadin.tests.server.component.window;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.shared.ui.window.WindowRole;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.DesignException;

/**
 * Tests declarative support for implementations of {@link Window}.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class WindowDeclarativeTest extends DeclarativeTestBase<Window> {

    @Test
    public void testDefault() {
        String design = "<v-window>";

        Window expected = new Window();

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testFeatures() {

        String design = "<v-window position='100,100' window-mode='maximized' "
                + "center modal=true resizable=false resize-lazy=true closable=false draggable=false "
                + "close-shortcut='ctrl-alt-escape' "
                + "assistive-prefix='Hello' assistive-postfix='World' assistive-role='alertdialog' "
                + "tab-stop-enabled=true "
                + "tab-stop-top-assistive-text='Do not move above the window' "
                + "tab-stop-bottom-assistive-text='End of window'>"
                + "</v-window>";

        Window expected = new Window();

        expected.setPositionX(100);
        expected.setPositionY(100);
        expected.setWindowMode(WindowMode.MAXIMIZED);

        expected.center();
        expected.setModal(!expected.isModal());
        expected.setResizable(!expected.isResizable());
        expected.setResizeLazy(!expected.isResizeLazy());
        expected.setClosable(!expected.isClosable());
        expected.setDraggable(!expected.isDraggable());

        expected.setCloseShortcut(KeyCode.ESCAPE, ModifierKey.CTRL,
                ModifierKey.ALT);

        expected.setAssistivePrefix("Hello");
        expected.setAssistivePostfix("World");
        expected.setAssistiveRole(WindowRole.ALERTDIALOG);
        expected.setTabStopEnabled(!expected.isTabStopEnabled());
        expected.setTabStopTopAssistiveText("Do not move above the window");
        expected.setTabStopBottomAssistiveText("End of window");

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testInvalidPosition() {
        assertInvalidPosition("");
        assertInvalidPosition("1");
        assertInvalidPosition("100,100.1");
        assertInvalidPosition("x");
        assertInvalidPosition("2,foo");
        // Should be invalid, not checked currently
        // assertInvalidPosition("1,2,3");
    }

    protected void assertInvalidPosition(String position) {
        try {
            read("<v-window position='" + position + "'>");
            Assert.fail("Invalid position '" + position + "' should throw");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testChildContent() {

        String design = "<v-window>" + createElement(new Button("OK"))
                + "</v-window>";

        Window expected = new Window();
        expected.setContent(new Button("OK"));

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test(expected = DesignException.class)
    public void testMultipleContentChildren() {

        String design = "<v-window>" + createElement(new Label("Hello"))
                + createElement(new Button("OK")) + "</v-window>";

        read(design);
    }

    @Test
    public void testAssistiveDescription() {

        Label assistive1 = new Label("Assistive text");
        Label assistive2 = new Label("More assistive text");

        String design = "<v-window>"
                + createElement(assistive1).attr(":assistive-description", "")
                + createElement(new Button("OK"))
                + createElement(assistive2).attr(":assistive-description", "");

        Window expected = new Window();
        expected.setContent(new Button("OK"));
        expected.setAssistiveDescription(assistive1, assistive2);

        testRead(design, expected);

        String written = "<v-window>" + createElement(new Button("OK"))
                + createElement(assistive1).attr(":assistive-description", "")
                + createElement(assistive2).attr(":assistive-description", "");

        testWrite(written, expected);
    }
}
