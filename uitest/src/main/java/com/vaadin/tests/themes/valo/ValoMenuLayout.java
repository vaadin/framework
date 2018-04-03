package com.vaadin.tests.themes.valo;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class ValoMenuLayout extends HorizontalLayout {

    CssLayout contentArea = new CssLayout();

    CssLayout menuArea = new CssLayout();

    public ValoMenuLayout() {
        setSizeFull();
        setSpacing(false);

        menuArea.setPrimaryStyleName(ValoTheme.MENU_ROOT);

        contentArea.setPrimaryStyleName("valo-content");
        contentArea.addStyleName("v-scrollable");
        contentArea.setSizeFull();

        addComponents(menuArea, contentArea);
        setExpandRatio(contentArea, 1);
    }

    public ComponentContainer getContentContainer() {
        return contentArea;
    }

    public void addMenu(Component menu) {
        menu.addStyleName(ValoTheme.MENU_PART);
        menuArea.addComponent(menu);
    }

}
