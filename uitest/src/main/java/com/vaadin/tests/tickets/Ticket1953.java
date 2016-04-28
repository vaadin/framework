package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket1953 extends LegacyApplication {
    public static final String cellStyle = "test-cell";
    public static final String colHeadStyle = "test-col-head";
    public static final String headingStyle = "test-heading";
    public static final String spacerStyle = "test-spacer";
    public static final String pageButtonStyle = "test-page-change";

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        setTheme("tests-tickets");
        GridLayout gl = new GridLayout(5, 5);

        gl.setStyleName("borders");

        gl.addComponent(new Label("0,0"), 0, 0);
        gl.addComponent(new Label("0,1"), 0, 1);
        gl.addComponent(new Label("0,2"), 0, 2);
        gl.addComponent(new Label("0,3"), 0, 3);
        gl.addComponent(new Label("0,4"), 0, 4);
        gl.addComponent(new Label("1,0"), 1, 0);
        gl.addComponent(new Label("2,0"), 2, 0);
        gl.addComponent(new Label("3,0"), 3, 0);
        gl.addComponent(new Label("4,0"), 4, 0);

        gl.addComponent(new Label("1,4"), 1, 4);
        gl.addComponent(new Label("2,4"), 2, 4);
        gl.addComponent(new Label("3,4"), 3, 4);
        gl.addComponent(new Label("4,4"), 4, 4);

        gl.addComponent(new Label("1-1 -> 2-2"), 1, 1, 2, 2);
        gl.addComponent(new Label("3,1"), 3, 1);
        gl.addComponent(new Label("3,2"), 3, 2);
        gl.addComponent(new Label("3,3"), 3, 3);

        main.addComponent(gl);

        // create grid
        GridLayout grid = new GridLayout(7, 7);

        grid.setStyleName("borders");

        // add upper row
        Button up = new Button("UP");

        up.setStyleName(pageButtonStyle);
        grid.addComponent(up, 0, 0);

        Label space = new Label();
        space.setStyleName(spacerStyle);
        grid.addComponent(space, 1, 0);

        Button single = null;
        String headingStyle = "foo";
        for (int i = 1; i < grid.getColumns() - 2; i++) {
            single = new Button(Integer.toString(i));
            single.setStyleName(headingStyle);
            grid.addComponent(single, i + 1, 0);
        }

        space = new Label();
        space.setStyleName(spacerStyle);
        grid.addComponent(space, grid.getColumns() - 1, 0);

        // middle rows
        char rowChar = 'A';
        for (int i = 1; i < grid.getRows() - 1; i++) {
            space = new Label(Character.toString(rowChar++));
            space.setStyleName(colHeadStyle);
            grid.addComponent(space, 0, i);

            space = new Label();
            space.setStyleName(spacerStyle);
            grid.addComponent(space, 1, i);

            space = new Label();
            space.setStyleName(spacerStyle);
            grid.addComponent(space, grid.getColumns() - 1, i);
        }

        // bottom row
        Button dn = new Button("DOWN");
        dn.setStyleName(pageButtonStyle);
        grid.addComponent(dn, 0, grid.getRows() - 1);

        space = new Label();
        space.setStyleName(spacerStyle);
        grid.addComponent(space, 1, grid.getRows() - 1);

        for (int i = 1; i < grid.getColumns() - 2; i++) {
            single = new Button(Integer.toString(i));
            single.setStyleName(headingStyle);
            grid.addComponent(single, i + 1, grid.getRows() - 1);
        }

        space = new Label();
        space.setStyleName(spacerStyle);
        grid.addComponent(space, grid.getColumns() - 1, grid.getRows() - 1);

        main.addComponent(grid);
    }
}
