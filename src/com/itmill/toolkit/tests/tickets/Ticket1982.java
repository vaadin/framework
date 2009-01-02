package com.itmill.toolkit.tests.tickets;

import java.util.ArrayList;
import java.util.List;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket1982 extends Application {

    private List components = new ArrayList();

    @Override
    public void init() {
        Window main = new Window();
        setMainWindow(main);

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeFull();
        main.setLayout(gl);
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
        gl.addComponent((TitleBar) components.get(0));
        gl.addComponent((TitleBar) components.get(1));
        gl.addComponent((TitleBar) components.get(2));
        gl.addComponent((TitleBar) components.get(3));
    }

    private class TitleBar extends ExpandLayout {

        private Button max = new Button("Max");
        private Button min = new Button("Min");
        private GridLayout layout;

        public TitleBar(String title, GridLayout layout) {
            super(ExpandLayout.ORIENTATION_HORIZONTAL);
            this.layout = layout;
            addComponent(new Label(title));
            addComponent(max);
            addComponent(min);
            min.setVisible(false);

            max.addListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    min.setVisible(true);
                    max.setVisible(false);
                    TitleBar.this.layout.removeAllComponents();
                    TitleBar.this.layout
                            .addComponent(TitleBar.this, 0, 0, 1, 1);
                }
            });
            min.addListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    min.setVisible(false);
                    max.setVisible(true);
                    restoreComponents(TitleBar.this.layout);
                }
            });
        }

    }

}
