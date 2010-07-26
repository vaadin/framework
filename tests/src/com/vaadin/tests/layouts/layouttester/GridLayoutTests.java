package com.vaadin.tests.layouts.layouttester;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.SystemError;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

public class GridLayoutTests extends AbstractLayoutTests {

    private Application application;
    private AbstractComponent rc1, col1, col2, col3, row1, row2, row3, x3, x22;

    public GridLayoutTests(Application application) {
        super();
        this.application = application;
    }

    protected Layout getAlignmentTests() {
        HorizontalLayout hlo = new HorizontalLayout();
        hlo.setSpacing(true);
        GridLayout glo = getTestGrid();
        glo.addStyleName(Reindeer.LAYOUT_WHITE);
        Alignment[] alignments = new Alignment[] { Alignment.BOTTOM_LEFT,
                Alignment.BOTTOM_CENTER, Alignment.BOTTOM_RIGHT,
                Alignment.MIDDLE_LEFT, Alignment.MIDDLE_CENTER,
                Alignment.MIDDLE_RIGHT, Alignment.TOP_LEFT,
                Alignment.TOP_CENTER, Alignment.TOP_RIGHT };

        glo.replaceComponent(col1, col1 = new TextField());
        glo.replaceComponent(col2, col2 = new TextField());
        glo.replaceComponent(col3, col3 = new TextField());
        ((TextField) col1).setValue("BOTTOM_RIGHT");
        ((TextField) col2).setValue("BOTTOM_LEFT");
        ((TextField) col3).setValue("BOTTOM_CENTER");
        glo.setComponentAlignment(col2, alignments[0]);
        glo.setComponentAlignment(col3, alignments[1]);
        glo.setComponentAlignment(col1, alignments[2]);

        glo.setComponentAlignment(row1, alignments[3]);
        glo.setComponentAlignment(row2, alignments[4]);
        glo.setComponentAlignment(row3, alignments[5]);
        hlo.addComponent(glo);
        glo = getTestGrid();
        glo.replaceComponent(row1, row1 = new DateField());
        glo.replaceComponent(row2, row2 = new DateField());
        glo.replaceComponent(row3, row3 = new DateField());
        glo.setComponentAlignment(col2, alignments[6]);
        glo.setComponentAlignment(col3, alignments[7]);
        glo.setComponentAlignment(col1, alignments[8]);
        hlo.addComponent(glo);
        return hlo;
    }

    protected Layout getCaptionsTests() {
        GridLayout glo = getTestGrid();
        glo.setWidth("600px");
        String[] captions = new String[] {
                "",
                "abcdefghijklmnopq",
                "abc def hij klm nop qrs tuv xyz qaz wsx edc rfv tgb yhn ujm mko nji bhu vgy cft cde" };
        glo.replaceComponent(col1, col1 = new TextField());
        glo.replaceComponent(col2, col2 = new TextField());
        glo.replaceComponent(col3, col3 = new TextField());

        col1.setCaption(captions[0]);
        col2.setCaption(captions[1]);
        col3.setCaption(captions[2]);
        col3.setIcon(new ClassResource("help.png", application));

        glo.replaceComponent(row1, row1 = new Label());
        glo.replaceComponent(row2, row2 = new Label());
        glo.replaceComponent(row3, row3 = new Label());

        row1.setCaption(captions[0]);
        row2.setCaption(captions[1]);
        row3.setCaption(captions[2]);

        glo.replaceComponent(x3, x3 = getTestTabsheet());
        glo.replaceComponent(x22, x22 = getTestTable());
        x22.setComponentError(new UserError("component error, user error"));

        x3.setCaption(captions[1]);
        x22.setCaption(captions[2]);

        return glo;
    }

    protected Layout getComponentAddReplaceMoveTests() {
        final GridLayout glo = getTestGrid();
        Layout baseLayout = getBaseLayout();
        final Button button1 = new Button("Test add");
        final Button button2 = new Button("Test replace");
        final Button button3 = new Button("Test move");
        final Button button4 = new Button("Test remove comp 1,1");
        final Button button5 = new Button("Test remove row 0");
        final Button button6 = new Button("Test remove comp row3");

        baseLayout.addComponent(button1);
        baseLayout.addComponent(button2);
        baseLayout.addComponent(button3);
        baseLayout.addComponent(button4);
        baseLayout.addComponent(button5);
        baseLayout.addComponent(button6);
        baseLayout.addComponent(glo);
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);

        final HorizontalLayout source = new HorizontalLayout();
        source.addComponent(new Label("MOVE LABEL 1"));
        source.addComponent(new Label("MOVE LABEL 2"));

        final AbstractComponent cc1 = getTestTabsheet();
        cc1.setCaption("ADDED COMPONENT");

        final AbstractComponent cc2 = getTestTabsheet();
        cc2.setCaption("REPLACEMENT COMPONENT");

        button1.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.addComponent(cc1);
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.replaceComponent(x22, cc2);
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });
        button3.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.moveComponentsFrom(source);
                button3.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button4.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.removeComponent(1, 1);
                button4.setEnabled(false);
                button5.setEnabled(true);
            }
        });

        button5.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.removeRow(0);
                button5.setEnabled(false);
                button6.setEnabled(true);
            }
        });
        button6.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                glo.removeComponent(row3);
                button6.setEnabled(false);
            }
        });

        return baseLayout;
    }

    protected Layout getComponentSizingTests() {
        final GridLayout glo = getTestGrid();
        Layout baseLayout = getBaseLayout();
        final Button button1 = new Button("full size, 3x1");
        final Button button2 = new Button("200 px width, 3x1");
        final Button button3 = new Button("200 px width, table");
        final Button button4 = new Button("undefined size+add, table");

        glo.replaceComponent(x22, x22 = getTestTable());

        baseLayout.addComponent(button1);
        baseLayout.addComponent(button2);
        baseLayout.addComponent(button3);
        baseLayout.addComponent(button4);
        baseLayout.addComponent(glo);
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);

        button1.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                x3.setSizeFull();
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            @Override
            public void buttonClick(ClickEvent event) {
                x3.setWidth("200px");
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });
        button3.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            public void buttonClick(ClickEvent event) {
                x22.setWidth("200px");
                button3.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button4.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            public void buttonClick(ClickEvent event) {
                x22.setSizeUndefined();
                ((Table) x22).addItem(new Object[] { "NEW ROW1" }, 3);
                ((Table) x22).addItem(new Object[] { "NEW ROW2" }, 4);
                button4.setEnabled(false);
            }
        });

        return baseLayout;
    }

    
    protected Layout getExpandRatiosTests() {
        final GridLayout glo = getTestGrid();
        Layout baseLayout = getBaseLayout();
        final Button button1 = new Button("set col 3 expand 1");
        final Button button2 = new Button("set all cols expand 0.25");
        final Button button3 = new Button("set row 0 expand 0.5");
        final Button button4 = new Button("set row 3 expand 0.2");

        glo.replaceComponent(x22, x22 = getTestTable());

        baseLayout.addComponent(button1);
        baseLayout.addComponent(button2);
        baseLayout.addComponent(button3);
        baseLayout.addComponent(button4);
        baseLayout.addComponent(glo);
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);

        button1.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setColumnExpandRatio(3, 1);
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setColumnExpandRatio(0, 0.25f);
                glo.setColumnExpandRatio(1, 0.25f);
                glo.setColumnExpandRatio(2, 0.25f);
                glo.setColumnExpandRatio(3, 0.25f);
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });
        button3.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setRowExpandRatio(0, 0.5f);
                button3.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button4.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setRowExpandRatio(3, 0.3f);
                button4.setEnabled(false);
            }
        });

        return baseLayout;
    }

    
    protected Layout getIconsTests() {
        GridLayout glo = getTestGrid();
        glo.setWidth("600px");
        Resource[] icons = new Resource[] {
                new ClassResource("alert.png", application),
                new ClassResource("help.png", application) };

        glo.replaceComponent(col1, col1 = new TextField("TEXTFIELD"));
        glo.replaceComponent(col2, col2 = new Label("LABEL"));
        glo.replaceComponent(col3, col3 = new Link("LINK", null));

        col1.setIcon(icons[0]);
        col2.setIcon(icons[1]);
        col3.setIcon(icons[0]);
        rc1.setIcon(icons[1]);
        col3
                .setCaption("long test caption bewucbwuebco or bmort b cbwecubw wbeucwe asdasd asdasda asdasd");
        col3.setComponentError(new UserError("component error, user error"));

        glo.replaceComponent(row1, row1 = new DateField());
        glo.replaceComponent(row2, row2 = new NativeSelect());
        glo.replaceComponent(row3, row3 = getTestTabsheet());

        row1.setIcon(icons[1]);
        row2.setIcon(icons[0]);
        row3.setIcon(icons[1]);

        glo.replaceComponent(x3, x3 = new CheckBox("CHECKBOX"));
        glo.replaceComponent(x22, x22 = new Panel("PANEL"));
        x22.setIcon(new ClassResource("alert.png", application));

        x3.setIcon(icons[0]);
        x22.setIcon(icons[1]);

        return glo;
    }

    
    protected Layout getLayoutSizingTests() {
        final GridLayout glo = getTestGrid();
        Layout baseLayout = getBaseLayout();
        baseLayout.setWidth("500px");
        baseLayout.setHeight("500px");
        final Button button1 = new Button("Set fixed height 350px");
        final Button button2 = new Button(
                "Set undefined size and add component");
        final Button button3 = new Button("Set fixed width and height 75%");
        final Button button4 = new Button("Set size full");

        glo.replaceComponent(x22, x22 = getTestTable());

        baseLayout.addComponent(button1);
        baseLayout.addComponent(button2);
        baseLayout.addComponent(button3);
        baseLayout.addComponent(button4);
        baseLayout.addComponent(glo);
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);

        button1.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setHeight("350px");
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setSizeUndefined();
                glo.addComponent(new Label("--- NEW LABEL ---"));
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });
        button3.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setWidth("75%");
                glo.setHeight("75%");
                button3.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button4.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setSizeFull();
                button4.setEnabled(false);
            }
        });

        return baseLayout;
    }

    
    protected Layout getMarginSpacingTests() {
        final GridLayout glo = getTestGrid();
        Layout baseLayout = getBaseLayout();
        baseLayout.setWidth("500px");
        baseLayout.setHeight("500px");
        final Button button1 = new Button("Set margin on");
        final Button button2 = new Button("Set spacing on");
        final Button button3 = new Button("Set margin off");
        final Button button4 = new Button("Set spacing off");

        baseLayout.addComponent(button1);
        baseLayout.addComponent(button2);
        baseLayout.addComponent(button3);
        baseLayout.addComponent(button4);
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);

        baseLayout.addComponent(glo);

        button1.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setMargin(true);
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setSpacing(true);
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });
        button3.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setMargin(false);
                button3.setEnabled(false);
                button4.setEnabled(true);
            }
        });
        button4.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 7716267156088629379L;

            
            public void buttonClick(ClickEvent event) {
                glo.setSpacing(false);
                button4.setEnabled(false);
            }
        });

        return baseLayout;
    }

    
    protected Layout getRequiredErrorIndicatorsTests() {
        GridLayout glo = getTestGrid();
        glo.setWidth("600px");
        Resource[] icons = new Resource[] {
                new ClassResource("alert.png", application),
                new ClassResource("help.png", application) };

        glo.replaceComponent(col1, col1 = new TextField("TEXTFIELD"));
        glo.replaceComponent(col2, col2 = new Label("LABEL"));
        glo.replaceComponent(col3, col3 = new Link("LINK", null));

        col1.setIcon(icons[0]);
        col1.setComponentError(new UserError("component error, user error"));
        col2
                .setComponentError(new SystemError(
                        "component error, system error"));
        col3.setComponentError(new UserError("component error, user error"));

        glo.replaceComponent(row1, row1 = new DateField());
        glo.replaceComponent(row2, row2 = new NativeSelect());
        glo.replaceComponent(row3, row3 = getTestTabsheet());

        ((AbstractField) col1).setRequired(true);
        ((AbstractField) col1).setValidationVisible(true);
        ((AbstractField) col1).setRequiredError("required error");

        ((AbstractField) row1).setRequired(true);
        ((AbstractField) row1).setValidationVisible(true);
        ((AbstractField) row1).setRequiredError("required error");

        ((AbstractField) row2).setRequired(true);
        ((AbstractField) row2).setValidationVisible(true);
        ((AbstractField) row2).setRequiredError("required error");
        row2.setComponentError(new UserError("component error, user error"));

        row3.setComponentError(new UserError("component error, user error"));
        row3.setIcon(icons[1]);
        row3
                .setCaption("long test caption bewucbwuebco or bmort b cbwecubw wbeucwe asdasd asdasda asdasd");

        glo.replaceComponent(x3, x3 = new CheckBox("CHECKBOX"));
        glo.replaceComponent(x22, x22 = new Panel("PANEL"));

        x3.setComponentError(new UserError("component error, user error"));
        x22.setComponentError(new UserError("component error, user error"));
        x22.setIcon(icons[0]);

        return glo;
    }

    private GridLayout getTestGrid() {
        // Create a 4 by 4 grid layout.
        GridLayout grid = new GridLayout(4, 4);
        // Fill out the first row using the cursor.
        grid.addComponent(rc1 = new Button("R/C 1"));

        grid.addComponent(col1 = new Button("Col " + (grid.getCursorX() + 1)));
        grid.addComponent(col2 = new Button("Col " + (grid.getCursorX() + 1)));
        grid.addComponent(col3 = new Button("Col " + (grid.getCursorX() + 1)));

        // Fill out the first column using coordinates.

        grid.addComponent(row1 = new Button("Row " + 1), 0, 1);
        grid.addComponent(row2 = new Button("Row " + 2), 0, 2);
        grid.addComponent(row3 = new Button("Row " + 3), 0, 3);

        // Add some components of various shapes.
        grid.addComponent(x3 = new Button("3x1 button"), 1, 1, 3, 1);
        grid.addComponent(new Label("1x2 cell"), 1, 2, 1, 3);
        x22 = new InlineDateField("A 2x2 date field");
        ((InlineDateField) x22).setResolution(DateField.RESOLUTION_DAY);
        grid.addComponent(x22, 2, 2, 3, 3);
        grid.setWidth("450px");
        grid.setHeight("450px");
        return grid;
    }

    private VerticalLayout getBaseLayout() {
        VerticalLayout vlo = new VerticalLayout();
        vlo.setSizeUndefined();
        return vlo;
    }

    private AbstractComponent getTestTabsheet() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeUndefined();
        tabsheet.addTab(new Label("TAB1"), "TAB1", new ClassResource(
                "alert.png", application));
        tabsheet.addTab(new Label("TAB2"), "TAB2", null);
        return tabsheet;
    }

    private Table getTestTable() {
        Table t = new Table();
        t.setSizeUndefined();
        t.setPageLength(5);
        t.addContainerProperty("test", String.class, null);
        t.addItem(new Object[] { "qwertyuiop asdfghjklöä zxccvbnm,m,." }, 1);
        t.addItem(new Object[] { "YGVYTCTCTRXRXRXRX" }, 2);
        return t;
    }

}
