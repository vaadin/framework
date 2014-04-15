package com.vaadin.tests.components.tree;

import java.util.Date;

import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.Reindeer;

public class TreeWithIcons extends TestBase {

    @Override
    protected void setup() {
        ThemeResource notCachedFolderIconHuge = new ThemeResource(
                "../runo/icons/64/folder.png?" + new Date().getTime());
        ThemeResource notCachedFolderIconLarge = new ThemeResource(
                "../runo/icons/32/folder.png?" + new Date().getTime());
        ThemeResource notCachedFolderIconLargeOther = new ThemeResource(
                "../runo/icons/32/ok.png?" + new Date().getTime());
        Tree t = new Tree();
        t.setImmediate(true);

        t.addItem("Root 1");
        t.addItem("Root 11");
        t.addItem("Root 111");
        t.addItem("Root 1111");
        t.addItem("Sub 1");
        t.setItemIcon("Sub 1", notCachedFolderIconLargeOther);
        t.setParent("Sub 1", "Root 1");
        String longItemId = LoremIpsum.get(50);
        t.addItem(longItemId);
        t.setItemIcon(longItemId, notCachedFolderIconHuge);
        t.setParent(longItemId, "Root 11");
        t.addItem("abcdefghijklmn");

        String first = "abcdefghijklmnop";
        String second = "abcdefghijklmnopqrst";
        t.addItem(first);
        t.addItem(second);
        t.setParent(second, first);
        t.setItemIcon(first, notCachedFolderIconLarge);

        HorizontalLayout hlay = new HorizontalLayout();
        hlay.setStyleName(Reindeer.LAYOUT_BLUE);
        hlay.addComponent(t);
        hlay.setWidth(-1, Sizeable.UNITS_PIXELS);

        Panel p = new Panel();
        p.setSizeUndefined();
        p.setContent(hlay);

        addComponent(p);
    }

    @Override
    protected String getDescription() {
        return "A tree with icons should resize itself correctly so the nodes are not cut either horizontally or vertically.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3529;
    }

}
