package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class DeepComponentTrees extends TestBase {

    private Panel root;

    @Override
    protected String getDescription() {
        return "Toolkit should not choke on deep component trees. 15 levels should be minimum to survive.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private int i = 0;
    private Class<?> currentValue = VerticalLayout.class;

    @Override
    protected void setup() {
        Layout main = getLayout();
        main.setSizeUndefined();
        getMainWindow().getLayout().setHeight(null);

        Label l = new Label(
                "This is a nice game to guess how many Layouts your FF2 (or any other browser) can deal with. Due to the worldwide attempt to decrease energy consumption, playing this game is only allowed above 60° longitude betwheen August and May (as excess energy consumed by you CPU is used to heat your room). It is considered wise to save all your work before starting the game.");

        root = new Panel("Test box");
        root.setWidth("600px");
        root.setHeight("200px");
        final Button b = new Button("Go try your luck with " + i + " layouts!");
        b.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                FF2KILLER(i++);
                b.setCaption("Go try your luck with " + i + " layouts!");
            }

        });

        final ComboBox s = new ComboBox("Restart game with select:");
        s.setNullSelectionAllowed(false);
        s.addItem("-- Choose value --");
        s.setValue("-- Choose value --");
        s.addItem(VerticalLayout.class);
        s.addItem(HorizontalLayout.class);
        s.addItem(GridLayout.class);
        s.addListener(new ComboBox.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                Object value = s.getValue();
                if (!value.equals("-- Choose value --")) {
                    currentValue = (Class<?>) value;
                    i = 0;
                    s.setValue("-- Choose value --");
                    b.setCaption("Go try your luck with " + i + " layouts!");
                }

            }
        });
        s.setImmediate(true);

        main.addComponent(l);
        main.addComponent(b);
        main.addComponent(s);
        main.addComponent(root);

    }

    private void FF2KILLER(int layouts) {
        Layout layout = getTestLayout();
        Layout r = layout;
        for (int i = 0; i < layouts; i++) {
            Layout lo = getTestLayout();
            layout.addComponent(lo);
            layout = lo;
        }
        layout.addComponent(new Label(
                "FF did it! Toolkit, Mozilla and you win! Dare to try again?"));
        root.setLayout(r);
    }

    Layout getTestLayout() {
        Layout l = new VerticalLayout();
        if (currentValue == GridLayout.class) {
            l = new GridLayout(1, 1);
        } else {
            try {
                l = (Layout) currentValue.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return l;
    }

}
