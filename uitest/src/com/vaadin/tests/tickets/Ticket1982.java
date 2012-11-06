package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket1982 extends LegacyApplication {

    private List<TitleBar> components = new ArrayList<TitleBar>();

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeFull();
        main.setContent(gl);
        gl.setMargin(true);

        TitleBar t1 = new TitleBar("Title 1", gl);
        TitleBar t2 = new TitleBar("Title 2", gl);
        TitleBar t3 = new TitleBar("Title 3", gl);
        TitleBar t4 = new TitleBar("Title 4", gl);
        components.add(t1);
        components.add(t2);
        components.add(t3);
        components.add(t4);

        restoreComponents(gl);

    }

    private void restoreComponents(GridLayout gl) {
        gl.removeAllComponents();
        gl.addComponent(components.get(0));
        gl.addComponent(components.get(1));
        gl.addComponent(components.get(2));
        gl.addComponent(components.get(3));
    }

    private class TitleBar extends HorizontalLayout {

        private Button max = new Button("Max");
        private Button min = new Button("Min");
        private GridLayout layout;

        public TitleBar(String title, GridLayout layout) {
            super();
            this.layout = layout;
            addComponent(new Label(title));
            addComponent(max);
            addComponent(min);
            min.setVisible(false);

            max.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    min.setVisible(true);
                    max.setVisible(false);
                    TitleBar.this.layout.removeAllComponents();
                    TitleBar.this.layout
                            .addComponent(TitleBar.this, 0, 0, 1, 1);
                }
            });
            min.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    min.setVisible(false);
                    max.setVisible(true);
                    restoreComponents(TitleBar.this.layout);
                }
            });
        }

    }

}
