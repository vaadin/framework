package com.vaadin.tests.components.menubar;

import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

@Theme("tests-valo")
public class MenuBarIcons extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar fontIconMenu = new MenuBar();
        fontIconMenu.setId("fontIcon");
        fontIconMenu.setWidth("400px");
        fontIconMenu.addItem("Main", FontAwesome.MAIL_REPLY_ALL, null);
        MenuItem hasSub = fontIconMenu.addItem("Has sub", FontAwesome.SUBWAY,
                null);
        hasSub.addItem("Sub item", FontAwesome.AMBULANCE, null);
        for (int i = 0; i < 10; i++) {
            fontIconMenu.addItem("Filler " + i, FontAwesome.ANGELLIST, null);
        }
        MenuItem more = fontIconMenu.getMoreMenuItem();
        more.setText("More");
        more.setIcon(FontAwesome.MOTORCYCLE);
        addComponent(fontIconMenu);

        MenuBar menu = new MenuBar();
        menu.setId("image");
        menu.setWidth("400px");
        Resource imageIcon = new ThemeResource("img/email-reply.png");
        menu.addItem("Main", imageIcon, null);
        hasSub = menu.addItem("Has sub", imageIcon, null);
        hasSub.addItem("Sub item", imageIcon, null);
        for (int i = 0; i < 10; i++) {
            menu.addItem("Filler " + i, imageIcon, null);
        }
        more = menu.getMoreMenuItem();
        more.setText("More");
        more.setIcon(imageIcon);
        addComponent(menu);

    }

}
