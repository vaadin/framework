package com.vaadin.demo.sampler.features.layouts;

import java.util.Iterator;

import com.vaadin.demo.sampler.ExampleUtil;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;

public class ApplicationLayoutExample extends VerticalLayout {

    Window win = new ApplicationLayoutWindow();
    Button open = new Button("Open sample in subwindow");

    public ApplicationLayoutExample() {
        setMargin(true);

        // We'll open this example in a separate window, configure it
        win.setWidth("70%");
        win.setHeight("70%");
        win.center();
        // Allow opening window again when closed
        win.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
                open.setEnabled(true);
            }
        });

        // 'open sample' button
        addComponent(open);
        open.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                getWindow().addWindow(win);
                open.setEnabled(false);
            }
        });
        addComponent(new Label(
                ("Don't worry: the content of the window is not supposed to make sense...")));

    }

    class ApplicationLayoutWindow extends Window {
        ApplicationLayoutWindow() {
            // Our main layout is a horizontal layout
            HorizontalLayout main = new HorizontalLayout();
            main.setSizeFull();
            setLayout(main);

            // Tree to the left
            Panel treePanel = new Panel(); // for scrollbars
            treePanel.setStyleName(Panel.STYLE_LIGHT);
            treePanel.setHeight("100%");
            treePanel.setWidth(null);
            treePanel.getLayout().setSizeUndefined();
            addComponent(treePanel);

            Tree tree = new Tree();
            tree.setContainerDataSource(ExampleUtil.getHardwareContainer());
            tree.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
            for (Iterator it = tree.rootItemIds().iterator(); it.hasNext();) {
                tree.expandItemsRecursively(it.next());
            }
            treePanel.addComponent(tree);

            // vertically divide the right area
            VerticalLayout left = new VerticalLayout();
            left.setSizeFull();
            addComponent(left);
            main.setExpandRatio(left, 1.0f); // use all available space

            // table on top
            Table tbl = new Table();
            tbl.setWidth("100%");
            tbl.setContainerDataSource(ExampleUtil.getISO3166Container());
            tbl.setSortDisabled(true);
            tbl.setPageLength(7);
            left.addComponent(tbl);

            // Label on bottom
            Panel textPanel = new Panel(); // for scrollbars
            textPanel.setStyleName(Panel.STYLE_LIGHT);
            textPanel.setSizeFull();
            left.addComponent(textPanel);
            left.setExpandRatio(textPanel, 1.0f); // use all available space

            Label text = new Label(ExampleUtil.lorem, Label.CONTENT_XHTML);
            text.setWidth("500px"); // some limit is good for text
            textPanel.addComponent(text);

        }
    }
}