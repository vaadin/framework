/*
 * Copyright 2000-2022 Vaadin Ltd.
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
/*
 * Copyright 2007 Google Inc.
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
package com.vaadin.client.ui.richtextarea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * A modified version of sample toolbar for use with {@link RichTextArea}. It
 * provides a simple UI for all rich text formatting, dynamically displayed only
 * for the available functionality.
 */
public class VRichTextToolbar extends Composite {

    /**
     * This {@link ClientBundle} is used for all the button icons. Using a
     * bundle allows all of these images to be packed into a single image, which
     * saves a lot of HTTP requests, drastically improving startup time.
     */
    public interface Images extends ClientBundle {

        /** @return the icon for bold */
        ImageResource bold();

        /** @return the icon for link creation */
        ImageResource createLink();

        /** @return the icon for horizontal break */
        ImageResource hr();

        /** @return the icon for indent */
        ImageResource indent();

        /** @return the icon for image insert */
        ImageResource insertImage();

        /** @return the icon for italic */
        ImageResource italic();

        /** @return the icon for center-justification */
        ImageResource justifyCenter();

        /** @return the icon for left-justification */
        ImageResource justifyLeft();

        /** @return the icon for right-justification */
        ImageResource justifyRight();

        /** @return the icon for ordered list */
        ImageResource ol();

        /** @return the icon for indent removal */
        ImageResource outdent();

        /** @return the icon for formating removal */
        ImageResource removeFormat();

        /** @return the icon for link removal */
        ImageResource removeLink();

        /** @return the icon for strike-through */
        ImageResource strikeThrough();

        /** @return the icon for subscript */
        ImageResource subscript();

        /** @return the icon for superscript */
        ImageResource superscript();

        /** @return the icon for unordered list */
        ImageResource ul();

        /** @return the icon for underlining */
        ImageResource underline();
    }

    /**
     * This {@link Constants} interface is used to make the toolbar's strings
     * internationalizable.
     */
    public interface Strings extends Constants {

        /** @return the constant for black */
        String black();

        /** @return the constant for blue */
        String blue();

        /** @return the constant for bold */
        String bold();

        /** @return the constant for color */
        String color();

        /** @return the constant for link creation */
        String createLink();

        /** @return the constant for font */
        String font();

        /** @return the constant for green */
        String green();

        /** @return the constant for horizontal break */
        String hr();

        /** @return the constant for indent */
        String indent();

        /** @return the constant for image insert */
        String insertImage();

        /** @return the constant for italic */
        String italic();

        /** @return the constant for center-justification */
        String justifyCenter();

        /** @return the constant for left-justification */
        String justifyLeft();

        /** @return the constant for right-justification */
        String justifyRight();

        /** @return the constant for large */
        String large();

        /** @return the constant for medium */
        String medium();

        /** @return the constant for normal */
        String normal();

        /** @return the constant for ordered list */
        String ol();

        /** @return the constant for indent removal */
        String outdent();

        /** @return the constant for red */
        String red();

        /** @return the constant for formating removal */
        String removeFormat();

        /** @return the constant for link removal */
        String removeLink();

        /** @return the constant for size */
        String size();

        /** @return the constant for small */
        String small();

        /** @return the constant for strike-through */
        String strikeThrough();

        /** @return the constant for subscript */
        String subscript();

        /** @return the constant for superscript */
        String superscript();

        /** @return the constant for unordered list */
        String ul();

        /** @return the constant for underline */
        String underline();

        /** @return the constant for white */
        String white();

        /** @return the constant for extra-large */
        String xlarge();

        /** @return the constant for extra-small */
        String xsmall();

        /** @return the constant for extra-extra-large */
        String xxlarge();

        /** @return the constant for extra-extra-small */
        String xxsmall();

        /** @return the constant for yellow */
        String yellow();
    }

    /**
     * We use an inner EventHandler class to avoid exposing event methods on the
     * RichTextToolbar itself.
     */
    private class EventHandler
            implements ClickHandler, ChangeHandler, KeyUpHandler {

        @Override
        @SuppressWarnings("deprecation")
        public void onChange(ChangeEvent event) {
            Object sender = event.getSource();
            if (sender == backColors) {
                basic.setBackColor(
                        backColors.getValue(backColors.getSelectedIndex()));
                backColors.setSelectedIndex(0);
            } else if (sender == foreColors) {
                basic.setForeColor(
                        foreColors.getValue(foreColors.getSelectedIndex()));
                foreColors.setSelectedIndex(0);
            } else if (sender == fonts) {
                basic.setFontName(fonts.getValue(fonts.getSelectedIndex()));
                fonts.setSelectedIndex(0);
            } else if (sender == fontSizes) {
                basic.setFontSize(
                        FONT_SIZES_CONSTANTS[fontSizes.getSelectedIndex() - 1]);
                fontSizes.setSelectedIndex(0);
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            if (sender == bold) {
                basic.toggleBold();
            } else if (sender == italic) {
                basic.toggleItalic();
            } else if (sender == underline) {
                basic.toggleUnderline();
            } else if (sender == subscript) {
                basic.toggleSubscript();
            } else if (sender == superscript) {
                basic.toggleSuperscript();
            } else if (sender == strikethrough) {
                extended.toggleStrikethrough();
            } else if (sender == indent) {
                extended.rightIndent();
            } else if (sender == outdent) {
                extended.leftIndent();
            } else if (sender == justifyLeft) {
                basic.setJustification(RichTextArea.Justification.LEFT);
            } else if (sender == justifyCenter) {
                basic.setJustification(RichTextArea.Justification.CENTER);
            } else if (sender == justifyRight) {
                basic.setJustification(RichTextArea.Justification.RIGHT);
            } else if (sender == insertImage) {
                final String url = Window.prompt("Enter an image URL:",
                        "http://");
                if (url != null) {
                    extended.insertImage(url);
                }
            } else if (sender == createLink) {
                final String url = Window.prompt("Enter a link URL:",
                        "http://");
                if (url != null) {
                    createLinkViaJSNI(extended, url);
                }
            } else if (sender == removeLink) {
                extended.removeLink();
            } else if (sender == hr) {
                extended.insertHorizontalRule();
            } else if (sender == ol) {
                extended.insertOrderedList();
            } else if (sender == ul) {
                extended.insertUnorderedList();
            } else if (sender == removeFormat) {
                extended.removeFormat();
            } else if (sender == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar
                // status. This will catch any cases where the user moves the
                // cursur using the keyboard, or uses one of the browser's
                // built-in keyboard shortcuts.
                updateStatus();
            }
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getSource() == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar
                // status. This will catch any cases where the user moves the
                // cursor using the keyboard, or uses one of the browser's
                // built-in keyboard shortcuts.
                updateStatus();
            }
        }

        @SuppressWarnings("deprecation")
        private native void createLinkViaJSNI(
                RichTextArea.ExtendedFormatter formatter, String url)
        /*-{
            var elem = formatter.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
            var wnd = elem.contentWindow;
            var selectedText = "";
            if (wnd.getSelection) {
                selectedText = wnd.getSelection().toString();
            }

            wnd.focus();
            if (selectedText) {
                // Add url as the href property of the highlighted text
                wnd.document.execCommand("createLink", false, url);
            } else {
                // Insert url both as a new text and its href-property value
                var range = wnd.document.getSelection().getRangeAt(0)
                var node = wnd.document.createElement("a");
                node.innerHTML = url;
                node.setAttribute("href", url);
                range.insertNode(node);
            }
          }-*/;
    }

    private static final RichTextArea.FontSize[] FONT_SIZES_CONSTANTS = {
            RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL,
            RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM,
            RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE,
            RichTextArea.FontSize.XX_LARGE };

    private final Images images = (Images) GWT.create(Images.class);
    private final Strings strings = (Strings) GWT.create(Strings.class);
    private final EventHandler handler = new EventHandler();

    private final RichTextArea richText;
    @SuppressWarnings("deprecation")
    private final RichTextArea.BasicFormatter basic;
    @SuppressWarnings("deprecation")
    private final RichTextArea.ExtendedFormatter extended;

    private final FlowPanel outer = new FlowPanel();
    private final FlowPanel topPanel = new FlowPanel();
    private final FlowPanel bottomPanel = new FlowPanel();
    private ToggleButton bold;
    private ToggleButton italic;
    private ToggleButton underline;
    private ToggleButton subscript;
    private ToggleButton superscript;
    private ToggleButton strikethrough;
    private PushButton indent;
    private PushButton outdent;
    private PushButton justifyLeft;
    private PushButton justifyCenter;
    private PushButton justifyRight;
    private PushButton hr;
    private PushButton ol;
    private PushButton ul;
    private PushButton insertImage;
    private PushButton createLink;
    private PushButton removeLink;
    private PushButton removeFormat;

    private ListBox backColors;
    private ListBox foreColors;
    private ListBox fonts;
    private ListBox fontSizes;

    /**
     * Creates a new toolbar that drives the given rich text area.
     *
     * @param richText
     *            the rich text area to be controlled
     */
    @SuppressWarnings("deprecation")
    public VRichTextToolbar(RichTextArea richText) {
        this.richText = richText;
        // NOTE: by default there is only one formatter anymore since the
        // difference was only needed to support older versions of Safari. These
        // deprecated methods are only called in order to support any extended
        // versions that do still implement separate formatters for some reason.
        basic = richText.getBasicFormatter();
        extended = richText.getExtendedFormatter();

        outer.add(topPanel);
        outer.add(bottomPanel);
        topPanel.setStyleName("gwt-RichTextToolbar-top");
        bottomPanel.setStyleName("gwt-RichTextToolbar-bottom");

        initWidget(outer);
        setStyleName("gwt-RichTextToolbar");

        if (basic != null) {
            bold = createToggleButton(images.bold(), strings.bold());
            italic = createToggleButton(images.italic(), strings.italic());
            underline = createToggleButton(images.underline(),
                    strings.underline());
            subscript = createToggleButton(images.subscript(),
                    strings.subscript());
            superscript = createToggleButton(images.superscript(),
                    strings.superscript());
            justifyLeft = createPushButton(images.justifyLeft(),
                    strings.justifyLeft());
            justifyCenter = createPushButton(images.justifyCenter(),
                    strings.justifyCenter());
            justifyRight = createPushButton(images.justifyRight(),
                    strings.justifyRight());
            topPanel.add(bold);
            topPanel.add(italic);
            topPanel.add(underline);
            topPanel.add(subscript);
            topPanel.add(superscript);
            topPanel.add(justifyLeft);
            topPanel.add(justifyCenter);
            topPanel.add(justifyRight);
        }

        if (extended != null) {
            strikethrough = createToggleButton(images.strikeThrough(),
                    strings.strikeThrough());
            indent = createPushButton(images.indent(), strings.indent());
            outdent = createPushButton(images.outdent(), strings.outdent());
            hr = createPushButton(images.hr(), strings.hr());
            ol = createPushButton(images.ol(), strings.ol());
            ul = createPushButton(images.ul(), strings.ul());
            insertImage = createPushButton(images.insertImage(),
                    strings.insertImage());
            createLink = createPushButton(images.createLink(),
                    strings.createLink());
            removeLink = createPushButton(images.removeLink(),
                    strings.removeLink());
            removeFormat = createPushButton(images.removeFormat(),
                    strings.removeFormat());
            topPanel.add(strikethrough);
            topPanel.add(indent);
            topPanel.add(outdent);
            topPanel.add(hr);
            topPanel.add(ol);
            topPanel.add(ul);
            topPanel.add(insertImage);
            topPanel.add(createLink);
            topPanel.add(removeLink);
            topPanel.add(removeFormat);
        }

        if (basic != null) {
            backColors = createColorList("Background");
            foreColors = createColorList("Foreground");
            fonts = createFontList();
            fontSizes = createFontSizes();
            bottomPanel.add(backColors);
            bottomPanel.add(foreColors);
            bottomPanel.add(fonts);
            bottomPanel.add(fontSizes);

            // We only use these handlers for updating status, so don't hook
            // them up unless at least basic editing is supported.
            richText.addKeyUpHandler(handler);
            richText.addClickHandler(handler);
        }
    }

    private ListBox createColorList(String caption) {
        final ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem(caption);
        lb.addItem(strings.white(), "white");
        lb.addItem(strings.black(), "black");
        lb.addItem(strings.red(), "red");
        lb.addItem(strings.green(), "green");
        lb.addItem(strings.yellow(), "yellow");
        lb.addItem(strings.blue(), "blue");
        lb.setTabIndex(-1);
        return lb;
    }

    private ListBox createFontList() {
        final ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem(strings.font(), "");
        lb.addItem(strings.normal(), "inherit");
        lb.addItem("Times New Roman", "Times New Roman");
        lb.addItem("Arial", "Arial");
        lb.addItem("Courier New", "Courier New");
        lb.addItem("Georgia", "Georgia");
        lb.addItem("Trebuchet", "Trebuchet");
        lb.addItem("Verdana", "Verdana");
        lb.setTabIndex(-1);
        return lb;
    }

    private ListBox createFontSizes() {
        final ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem(strings.size());
        lb.addItem(strings.xxsmall());
        lb.addItem(strings.xsmall());
        lb.addItem(strings.small());
        lb.addItem(strings.medium());
        lb.addItem(strings.large());
        lb.addItem(strings.xlarge());
        lb.addItem(strings.xxlarge());
        lb.setTabIndex(-1);
        return lb;
    }

    private PushButton createPushButton(ImageResource img, String tip) {
        final PushButton pb = new PushButton(new Image(img));
        pb.addClickHandler(handler);
        pb.setTitle(tip);
        pb.setTabIndex(-1);
        return pb;
    }

    private ToggleButton createToggleButton(ImageResource img, String tip) {
        final ToggleButton tb = new ToggleButton(new Image(img));
        tb.addClickHandler(handler);
        tb.setTitle(tip);
        tb.setTabIndex(-1);
        return tb;
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    @SuppressWarnings("deprecation")
    private void updateStatus() {
        if (basic != null) {
            bold.setDown(basic.isBold());
            italic.setDown(basic.isItalic());
            underline.setDown(basic.isUnderlined());
            subscript.setDown(basic.isSubscript());
            superscript.setDown(basic.isSuperscript());
        }

        if (extended != null) {
            strikethrough.setDown(extended.isStrikethrough());
        }
    }
}
