package com.vaadin.tests.themes.valo;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.TreeDragMode;

public class Trees extends VerticalLayout implements View {
    public Trees() {
        setSpacing(false);

        Label h1 = new Label("Trees");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        Tree tree = new Tree();
        tree.setSelectable(true);
        tree.setMultiSelect(true);
        Container generateContainer = ValoThemeUI.generateContainer(10, true);
        tree.setContainerDataSource(generateContainer);
        tree.setDragMode(TreeDragMode.NODE);
        row.addComponent(tree);
        tree.setItemCaptionPropertyId(ValoThemeUI.CAPTION_PROPERTY);
        tree.setItemIconPropertyId(ValoThemeUI.ICON_PROPERTY);
        tree.expandItem(generateContainer.getItemIds().iterator().next());

        tree.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });

        // Add actions (context menu)
        tree.addActionHandler(ValoThemeUI.getActionHandler());
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
