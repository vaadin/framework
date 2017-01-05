/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.declarative.DesignContext;

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
        String design = "<vaadin-button plain-text></vaadin-button>";
        testButtonAndNativeButton(design, false, "");
    }

    @Test
    public void testPlainTextCaption() {
        String design = "<vaadin-button plain-text>Click</vaadin-button>";
        testButtonAndNativeButton(design, false, "Click");
    }

    @Test
    public void testEmptyHtml() {
        String design = "<vaadin-button />";
        testButtonAndNativeButton(design, true, "");
    }

    @Test
    public void testHtmlCaption() {
        String design = "<vaadin-button><b>Click</b></vaadin-button>";
        testButtonAndNativeButton(design, true, "<b>Click</b>");
    }

    @Test
    public void testWithCaptionAttribute() {
        // The caption attribute should be ignored
        String design = "<vaadin-button caption=Caption>Click</vaadin-button>";
        String expectedWritten = "<vaadin-button>Click</vaadin-button>";
        testButtonAndNativeButton(design, true, "Click", expectedWritten);
    }

    @Test
    public void testWithOnlyCaptionAttribute() {
        String design = "<vaadin-button caption=Click/>";
        String expectedWritten = "<vaadin-button/>";
        testButtonAndNativeButton(design, true, "", expectedWritten);
    }

    @Test
    public void testHtmlEntitiesInCaption() {
        String designPlainText = "<vaadin-button plain-text=\"true\">&gt; One</vaadin-button>";
        String expectedCaptionPlainText = "> One";

        Button read = read(designPlainText);
        Assert.assertEquals(expectedCaptionPlainText, read.getCaption());

        designPlainText = designPlainText.replace("vaadin-button",
                "vaadin-native-button");
        Button nativeButton = read(designPlainText);
        Assert.assertEquals(expectedCaptionPlainText,
                nativeButton.getCaption());

        String designHtml = "<vaadin-button>&gt; One</vaadin-button>";
        String expectedCaptionHtml = "&gt; One";
        read = read(designHtml);
        Assert.assertEquals(expectedCaptionHtml, read.getCaption());

        designHtml = designHtml.replace("vaadin-button",
                "vaadin-native-button");
        nativeButton = read(designHtml);
        Assert.assertEquals(expectedCaptionHtml, nativeButton.getCaption());

        read = new Button("&amp; Test");
        read.setCaptionAsHtml(true);
        Element root = new Element(Tag.valueOf("vaadin-button"), "");
        read.writeDesign(root, new DesignContext());
        assertEquals("&amp; Test", root.html());

        read.setCaptionAsHtml(false);
        root = new Element(Tag.valueOf("vaadin-button"), "");
        read.writeDesign(root, new DesignContext());
        assertEquals("&amp;amp; Test", root.html());

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
        design = design.replace("vaadin-button", "vaadin-native-button");
        expectedWritten = expectedWritten.replace("vaadin-button",
                "vaadin-native-button");
        NativeButton nb = new NativeButton();
        nb.setCaptionAsHtml(html);
        nb.setCaption(caption);
        testRead(expectedWritten, nb);
        testWrite(expectedWritten, nb);
    }

    @Test
    public void testAttributes() {
        String design = "<vaadin-button tabindex=3 plain-text icon-alt=OK "
                + "click-shortcut=shift-ctrl-o></vaadin-button>";
        Button b = new Button("");
        b.setTabIndex(3);
        b.setIconAlternateText("OK");
        b.setClickShortcut(KeyCode.O, ModifierKey.CTRL, ModifierKey.SHIFT);
        testRead(design, b);
        testWrite(design, b);
    }
}
