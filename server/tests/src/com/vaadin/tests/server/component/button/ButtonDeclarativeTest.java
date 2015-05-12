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
package com.vaadin.tests.server.component.button;

import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

/**
 * Tests declarative support for implementations of {@link Button} and
 * {@link NativeButton}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ButtonDeclarativeTest extends DeclarativeTestBase<Button> {

    @Test
    public void testEmptyPlainText() {
        String design = "<v-button plain-text=''></v-button>";
        testButtonAndNativeButton(design, false, "");
    }

    @Test
    public void testPlainTextCaption() {
        String design = "<v-button plain-text=''>Click</v-button>";
        testButtonAndNativeButton(design, false, "Click");
    }

    @Test
    public void testEmptyHtml() {
        String design = "<v-button />";
        testButtonAndNativeButton(design, true, "");
    }

    @Test
    public void testHtmlCaption() {
        String design = "<v-button><b>Click</b></v-button>";
        testButtonAndNativeButton(design, true, "<b>Click</b>");
    }

    @Test
    public void testWithCaptionAttribute() {
        // The caption attribute should be ignored
        String design = "<v-button caption=Caption>Click</v-button>";
        String expectedWritten = "<v-button>Click</v-button>";
        testButtonAndNativeButton(design, true, "Click", expectedWritten);
    }

    @Test
    public void testWithOnlyCaptionAttribute() {
        String design = "<v-button caption=Click/>";
        String expectedWritten = "<v-button/>";
        testButtonAndNativeButton(design, true, "", expectedWritten);
    }

    public void testButtonAndNativeButton(String design, boolean html,
            String caption) {
        testButtonAndNativeButton(design, html, caption, design);
    }

    public void testButtonAndNativeButton(String design, boolean html,
            String caption, String expectedWritten) {
        // Test Button
        Button b = new Button();
        b.setCaptionAsHtml(html);
        b.setCaption(caption);
        testRead(expectedWritten, b);
        testWrite(expectedWritten, b);
        // Test NativeButton
        design = design.replace("v-button", "v-native-button");
        expectedWritten = expectedWritten
                .replace("v-button", "v-native-button");
        NativeButton nb = new NativeButton();
        nb.setCaptionAsHtml(html);
        nb.setCaption(caption);
        testRead(expectedWritten, nb);
        testWrite(expectedWritten, nb);
    }

    @Test
    public void testAttributes() {
        String design = "<v-button tabindex=3 plain-text='' icon-alt=OK "
                + "click-shortcut=ctrl-shift-o></v-button>";
        Button b = new Button("");
        b.setTabIndex(3);
        b.setIconAlternateText("OK");
        b.setClickShortcut(KeyCode.O, ModifierKey.CTRL, ModifierKey.SHIFT);
        testRead(design, b);
        testWrite(design, b);
    }
}