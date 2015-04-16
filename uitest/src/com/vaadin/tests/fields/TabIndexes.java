package com.vaadin.tests.fields;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.TwinColSelect;

@SuppressWarnings("rawtypes")
public class TabIndexes extends AbstractTestUIWithLog {

    private List<AbstractField> fields;

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        addComponent(buttonLayout);
        Button clearTabIndexes = new Button("Set all tab indexes to 0");
        clearTabIndexes.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Setting tab indexes to 0");
                for (AbstractField f : fields) {
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
                for (AbstractField f : fields) {
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
                for (AbstractField f : fields) {
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
                for (AbstractField f : fields) {
                    f.setTabIndex(tabIndex--);
                }
                updateCaptions();
            }
        });
        fields = new ArrayList<AbstractField>();
        Table t = new Table();
        t.setSelectable(true);
        t.addContainerProperty("foo", String.class, "bar");
        t.addItem();
        fields.add(t);
        fields.add(new ComboBox());
        fields.add(new NativeSelect());
        fields.add(new ListSelect());
        fields.add(new TextField());
        fields.add(new PopupDateField());
        fields.add(new InlineDateField());
        OptionGroup og = new OptionGroup();
        og.addItem("Item 1");
        og.addItem("Item 2");
        fields.add(og);
        TreeTable tt = new TreeTable();
        tt.setSelectable(true);
        tt.addContainerProperty("foo", String.class, "bar");
        tt.addItem();

        fields.add(tt);
        Tree tree = new Tree();
        tree.addItem("Item 1");
        fields.add(tree);
        fields.add(new TwinColSelect());
        fields.add(new PasswordField());
        fields.add(new TextField());
        fields.add(new TextArea());
        fields.add(new RichTextArea());
        fields.add(new CheckBox());
        fields.add(new Slider());

        clearTabIndexes.click();

        buttonLayout.addComponents(clearTabIndexes, setTabIndexesToOne,
                setTabIndexesInOrder, setTabIndexesInReverseOrder);

        int fieldId = 1;
        GridLayout gl = new GridLayout(4, 4);
        for (Field f : fields) {
            f.setId("field-" + fieldId++);
            gl.addComponent(f);
        }
        addComponent(gl);

    }

    protected void updateCaptions() {
        for (Field f : fields) {
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
