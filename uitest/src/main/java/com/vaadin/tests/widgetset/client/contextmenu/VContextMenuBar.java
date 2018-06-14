package com.vaadin.tests.widgetset.client.contextmenu;

import com.vaadin.client.ui.VMenuBar;

public class VContextMenuBar extends VMenuBar {
    public VContextMenuBar() {
        setPixelSize(0, 0);
        setVisible(false);
    }

    @Override
    public void showChildMenuAt(CustomMenuItem item, int top, int left) {
        super.showChildMenuAt(item, top, left);
    }
}
