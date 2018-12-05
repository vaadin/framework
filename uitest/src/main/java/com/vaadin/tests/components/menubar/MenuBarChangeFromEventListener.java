package com.vaadin.tests.components.menubar;

import com.vaadin.event.FieldEvents;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;

public class MenuBarChangeFromEventListener extends ComponentTestCase<MenuBar> {
    public final static String MENU_CLICKED = "Menu Selected";
    public final static String MENU_CLICKED_BLUR = "Menu Selected after TF Blur event";


    @Override
    protected Class<MenuBar> getTestClass() {
        return MenuBar.class;
    }

    @Override
    protected void initializeComponents() {
       final MenuBar mb = new MenuBar();
        mb.setCaption("");

        MenuBar.MenuItem mi = mb.addItem("Item to click", null,
                new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem selectedItem) {
                            Label label=new Label(MENU_CLICKED);
                            label.addStyleName("menuClickedLabel");
                             addComponent(label);
                    }
                });
        mb.setId("menuBar");
        TextField tf = new TextField(
                "2. Focus this TextField and then click the menu");
        tf.setId("textField");
        tf.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if (mb.getDescription().isEmpty()) {
                    mb.setDescription("Some Text here");
                } else {
                    mb.setDescription("");
                }
                Label label=new Label(MENU_CLICKED_BLUR);
                label.addStyleName("blurListenerLabel");
                addComponent(label);
            }

        });

        addComponent(mb);
        addComponent(tf);
    }
}
