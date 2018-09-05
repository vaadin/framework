package com.vaadin.tests.declarative;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.data.Property.ReadOnlyException;
import com.vaadin.v7.data.Property.ValueChangeNotifier;

public class DeclarativeEditor extends UI {

    private VerticalLayout treeHolder;
    private TextArea editor;
    private DesignContext dc;
    private boolean disableEvents = false;
    private HorizontalSplitPanel main;

    @Override
    protected void init(VaadinRequest request) {
        main = new HorizontalSplitPanel();
        editor = new TextArea();
        editor.setSizeFull();
        try {
            editor.setValue(IOUtils.toString(getClass()
                    .getResourceAsStream("DeclarativeEditorInitial.html")));
        } catch (ReadOnlyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.addValueChangeListener(listener -> {
            editor.setComponentError(null);
            updateTree(listener.getValue());
        });

        Panel editorPanel = new Panel(editor);
        editorPanel.setSizeFull();
        treeHolder = new VerticalLayout();
        treeHolder.setSizeFull();

        main.addComponents(editorPanel, treeHolder);
        main.setSizeFull();

        setContent(main);
        updateTree(editor.getValue());
    }

    protected void updateTree(String string) {
        if (disableEvents) {
            return;
        }

        dc = Design.read(new ByteArrayInputStream(string.getBytes()), null);
        treeHolder.removeAllComponents();
        treeHolder.addComponent(dc.getRootComponent());

        addValueChangeListeners(dc.getRootComponent());
    }

    protected void updateCode() {
        if (disableEvents) {
            return;
        }

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        try {
            Design.write(treeHolder.getComponent(0), o);
            disableEvents = true;
            editor.setValue(o.toString(UTF_8.name()));
            disableEvents = false;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void addValueChangeListeners(Component component) {
        if (component instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) component)
                    .addValueChangeListener(event -> updateCode());
        }

        if (component instanceof HasComponents) {
            for (Component c : (HasComponents) component) {
                addValueChangeListeners(c);
            }
        }
    }

}
