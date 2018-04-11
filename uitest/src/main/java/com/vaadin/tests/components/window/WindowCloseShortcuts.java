package com.vaadin.tests.components.window;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

@SuppressWarnings("serial")
public class WindowCloseShortcuts extends AbstractTestUI {

    private Window window;
    private Label designLabel;
    private VerticalLayout buttonLayout;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        window = new Window("Test window");
        window.setVisible(true);
        window.setModal(true);
        window.setContent(new RichTextArea());

        Panel buttonPanel = new Panel();
        buttonLayout = new VerticalLayout();
        buttonLayout.setSizeFull();
        buttonPanel.setCaption("Demo controls");

        addButton(new Button("Open window", event -> {
            UI.getCurrent().addWindow(window);
            window.center();
            updateDesign();
        }));

        addButton(new Button("Add ENTER close shortcut", event -> {
            window.addCloseShortcut(KeyCode.ENTER);
            updateDesign();
        }));

        addButton(new Button("Add TAB close shortcut", event -> {
            window.addCloseShortcut(KeyCode.TAB);
            updateDesign();
        }));

        addButton(new Button("Remove ESC close shortcut", event -> {
            window.removeCloseShortcut(KeyCode.ESCAPE);
            updateDesign();
        }));

        addButton(new Button("Clear all close shortcuts", event -> {
            window.removeAllCloseShortcuts();
            updateDesign();
        }));

        addButton(new Button("Reset to default state", event -> {
            window.removeCloseShortcut();
            updateDesign();
        }));

        buttonPanel.setContent(buttonLayout);
        buttonPanel.setSizeUndefined();
        addComponent(buttonPanel);
        buttonPanel.setWidth("400px");

        Panel designPanel = new Panel();
        designPanel.setCaption("Window design");
        designLabel = new Label("");
        VerticalLayout designLayout = new VerticalLayout();
        designLayout.addComponent(designLabel);
        designPanel.setContent(designLayout);
        addComponent(designPanel);

        updateDesign();
    }

    private void addButton(Button b) {
        b.setWidth("100%");
        buttonLayout.addComponent(b);
    }

    //
    // The following code is adapted from DeclarativeTestBaseBase.java
    // (that's not a typo)
    //

    private void updateDesign() {
        String design = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DesignContext dc = new DesignContext();
            dc.setRootComponent(window);
            Design.write(dc, outputStream);
            design = outputStream.toString(UTF_8.name());
        } catch (Exception e) {
            return;
        }
        Element producedElem = Jsoup.parse(design).body().child(0);
        design = elementToHtml(producedElem);
        designLabel.setCaption(design);
    }

    //
    // The following code is copied directly from DeclarativeTestBaseBase.java
    // (that's not a typo, either)
    //

    private String elementToHtml(Element producedElem) {
        StringBuilder stringBuilder = new StringBuilder();
        elementToHtml(producedElem, stringBuilder);
        return stringBuilder.toString();
    }

    private String elementToHtml(Element producedElem, StringBuilder sb) {
        List<String> names = new ArrayList<>();
        for (Attribute a : producedElem.attributes().asList()) {
            names.add(a.getKey());
        }
        Collections.sort(names);

        sb.append("<" + producedElem.tagName() + "");
        for (String attrName : names) {
            sb.append(' ').append(attrName).append('=').append("\'")
                    .append(producedElem.attr(attrName)).append("\'");
        }
        sb.append('>');
        for (Node child : producedElem.childNodes()) {
            if (child instanceof Element) {
                elementToHtml((Element) child, sb);
            } else if (child instanceof TextNode) {
                String text = ((TextNode) child).text();
                sb.append(text.trim());
            }
        }
        sb.append("</").append(producedElem.tagName()).append('>');
        return sb.toString();
    }

    @Override
    protected Integer getTicketNumber() {
        return 17383;
    }

}
