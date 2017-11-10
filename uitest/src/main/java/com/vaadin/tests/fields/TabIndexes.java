package com.vaadin.tests.fields;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TabIndexes extends AbstractTestUIWithLog {

    public static final String FIELD_CONTAINER_ID = "field-container";
    private List<Focusable> fields = new ArrayList<>();

    @Override
    protected void setup(VaadinRequest request) {
        fields.add(new ComboBox());
        fields.add(new NativeSelect());
        fields.add(new ListSelect());
        fields.add(new TextField());
        fields.add(new DateField());
        fields.add(new InlineDateField());
        TreeGrid<String> tt = new TreeGrid<>();
        tt.addColumn(s -> s);
        tt.setItems("Foo", "Bar");

        fields.add(tt);
        fields.add(new TwinColSelect<String>());
        fields.add(new PasswordField());
        fields.add(new TextArea());
        fields.add(new RichTextArea());
        fields.add(new CheckBox());
        fields.add(new Slider());
        MenuBar menubar = new MenuBar();
        menubar.addItem("foo", item -> {
        });
        fields.add(menubar);
        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(new Label("Tab content"), "Tab 1");
        fields.add(tabSheet);
        Accordion accordion = new Accordion();
        accordion.addTab(new Label("Tab content"), "Tab 1");
        fields.add(accordion);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        addComponent(buttonLayout);
        Button clearTabIndexes = new Button("Set all tab indexes to 0");
        clearTabIndexes.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Setting tab indexes to 0");
                for (Focusable f : fields) {
                    f.setTabIndex(0);
                }
                updateCaptions();
            }
        });
        Button setTabIndexesToOne = new Button("Set all tab indexes to 1");
        setTabIndexesToOne.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Setting tab indexes to 1");
                for (Focusable f : fields) {
                    f.setTabIndex(1);
                }
                updateCaptions();
            }
        });
        Button setTabIndexesInOrder = new Button("Set tab indexes to 1..N");
        setTabIndexesInOrder.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                int tabIndex = 1;
                log("Setting tab indexes to 1..N");
                for (Focusable f : fields) {
                    f.setTabIndex(tabIndex++);
                }
                updateCaptions();
            }
        });
        Button setTabIndexesInReverseOrder = new Button(
                "Set tab indexes to N..1");
        setTabIndexesInReverseOrder.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                int tabIndex = fields.size();
                log("Setting tab indexes to N..1");
                for (Focusable f : fields) {
                    f.setTabIndex(tabIndex--);
                }
                updateCaptions();
            }
        });

        clearTabIndexes.click();

        buttonLayout.addComponents(clearTabIndexes, setTabIndexesToOne,
                setTabIndexesInOrder, setTabIndexesInReverseOrder);

        VerticalLayout vl = new VerticalLayout();
        vl.setId(FIELD_CONTAINER_ID);
        for (Component f : fields) {
            f.setId("field-" + f.getClass().getSimpleName());
            vl.addComponent(f);
        }
        addComponent(vl);

    }

    protected void updateCaptions() {
        for (Focusable f : fields) {
            f.setCaption(f.getClass().getSimpleName() + " Tab index: "
                    + f.getTabIndex());
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 10315;
    }

    @Override
    protected String getTestDescription() {
        return "Tab index should be propagated into html";
    }

}
