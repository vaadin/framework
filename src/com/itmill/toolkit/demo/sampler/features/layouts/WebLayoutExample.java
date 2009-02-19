package com.itmill.toolkit.demo.sampler.features.layouts;

import java.util.Iterator;

import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WebLayoutExample extends VerticalLayout {

    Window win = new WebLayoutWindow();
    Button open = new Button("Open sample in subwindow");

    public WebLayoutExample() {
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

    class WebLayoutWindow extends Window {
        WebLayoutWindow() {
            // Our main layout is a horiozontal layout
            HorizontalLayout main = new HorizontalLayout();
            main.setMargin(true);
            main.setSpacing(true);
            setLayout(main);

            // Tree to the left
            Tree tree = new Tree();
            tree.setContainerDataSource(ExampleUtil.getHardwareContainer());
            tree.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
            for (Iterator it = tree.rootItemIds().iterator(); it.hasNext();) {
                tree.expandItemsRecursively(it.next());
            }
            addComponent(tree);

            // vertically divide the right area
            VerticalLayout left = new VerticalLayout();
            left.setSpacing(true);
            addComponent(left);

            // table on top
            Table tbl = new Table();
            tbl.setWidth("500px");
            tbl.setContainerDataSource(ExampleUtil.getISO3166Container());
            tbl.setSortDisabled(true);
            tbl.setPageLength(7);
            left.addComponent(tbl);

            // Label on bottom
            Label text = new Label(ExampleUtil.lorem, Label.CONTENT_XHTML);
            text.setWidth("500px"); // some limit is good for text
            left.addComponent(text);

        }
    }

}