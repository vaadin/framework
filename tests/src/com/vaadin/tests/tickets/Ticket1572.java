package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1572 extends com.vaadin.Application {

    private Label state;
    private GridLayout gl;
    private Label spacingstate;

    @Override
    public void init() {

        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        Panel p = new Panel("Test wrapper for gridlayout margin/spacing");

        p.setLayout(new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));

        gl = new GridLayout(3, 3);
        gl.setMargin(true);
        for (int i = 0; i < 3 * 3; i++) {
            gl.addComponent(new Button("test"));
        }
        p.addComponent(gl);
        p.addComponent(new Label("| next component"));

        Button b = new Button("next margin state");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                nextMarginState();
            }

        });

        state = new Label();
        state.setCaption("Current margin state:");
        main.addComponent(state);
        main.addComponent(b);

        Button b2 = new Button("next spacing state");
        b2.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                nextSpacingState();
            }

        });

        spacingstate = new Label();
        spacingstate.setCaption("Current Spacing State:");
        main.addComponent(spacingstate);
        main.addComponent(b2);

        main.addComponent(p);

        nextMarginState();
        nextSpacingState();

    }

    private int stateCounter = -1;

    private void nextMarginState() {
        stateCounter++;
        switch (stateCounter) {
        case 0:
            gl.setMargin(false);
            state.setValue("Margin off");
            break;
        case 1:
            gl.setMargin(true);
            state.setValue("Margin on");
            break;
        case 2:
            gl.setMargin(true, false, false, false);
            state.setValue("Margin top");
            break;
        case 3:
            gl.setMargin(false, true, false, false);
            state.setValue("Margin right");
            break;
        case 4:
            gl.setMargin(false, false, true, false);
            state.setValue("Margin bottom");
            break;
        case 5:
            gl.setMargin(false, false, false, true);
            state.setValue("Margin left");
            break;
        default:
            stateCounter = -1;
            nextMarginState();
            break;
        }
    }

    private boolean spacing = true;

    private void nextSpacingState() {
        spacing = !spacing;
        if (spacing) {
            gl.setSpacing(true);
            spacingstate.setValue("Spacing on");
        } else {
            gl.setSpacing(false);
            spacingstate.setValue("Spacing off");
        }
    }

}
