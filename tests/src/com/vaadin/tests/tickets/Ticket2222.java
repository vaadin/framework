package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

public class Ticket2222 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        horiz.setSpacing(true);
        horiz.setMargin(true);
        horiz.setStyleName("ticket2222");

        horiz.addComponent(new Label("Horiz spacing: 60px;"));
        horiz.addComponent(new Label("Margin-left: 40px"));
        horiz.addComponent(new Label("Margin-top: 100px;"));
        horiz.addComponent(new Label("Margin-right: 20px;"));
        horiz.addComponent(new Label("Margin-bottom: 30px;"));
        horiz.addStyleName("borders");

        OrderedLayout vert = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        vert.setSizeUndefined();
        vert.setSpacing(true);
        vert.setMargin(false);
        vert.setStyleName("ticket2222");
        vert.addComponent(new Label("Vert spacing: 50px;"));
        vert.addComponent(new Label("No margins"));
        vert.addComponent(new Label("label 3"));
        vert.addStyleName("borders");

        GridLayout gl = new GridLayout(3, 2);
        gl.setStyleName("borders");
        gl.setSpacing(true);
        gl.setMargin(true);
        gl.setStyleName("ticket2222");
        gl.addComponent(new Label("Vert spacing: 50px; horiz 20px;"));
        gl.addComponent(new Label("Margin-left: 40px"));
        gl.addComponent(new Label("Margin-top: 100px;"));
        gl.addComponent(new Label("Margin-right: 20px;"));
        gl.addComponent(new Label("Margin-bottom: 30px;"));
        gl.addComponent(new Label("label 3"));
        gl.addStyleName("borders");

        layout.addComponent(horiz);
        layout.addComponent(new Label(" "));
        layout.addComponent(vert);
        layout.addComponent(new Label(" "));
        layout.addComponent(gl);
    }

}
