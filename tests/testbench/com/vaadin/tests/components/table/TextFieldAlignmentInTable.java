package com.vaadin.tests.components.table;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.Reindeer;

public class TextFieldAlignmentInTable extends TestBase {

    private static final Object STRING = "string";
    private static final Object ENUM = "enum";
    private static final Object DATE = "date";
    private static final Object BUTTON = "button";

    public static enum MyEnum {
        VALUE1, VALUE2
    }

    @Override
    protected void setup() {
        addComponent(buildEditablePair("Basic", "Editable", false, STRING));
        addComponent(buildEditablePair("Full height fields",
                "Full height fields + editable", true, STRING));

        addComponent(buildEditablePair("Basic", "Editable", false, ENUM));
        addComponent(buildEditablePair("Full height fields",
                "Full height fields + editable", true, ENUM));

        Table fullTable = buildTable("Editable", false);
        fullTable.setEditable(true);
        addComponent(fullTable);

        Table alignedFullTable = buildTable("Full height fields", true);
        alignedFullTable.setEditable(true);
        addComponent(alignedFullTable);
    }

    public HorizontalLayout buildEditablePair(String basicCaption,
            String editableCaption, boolean addAlignmentStyle, Object property) {
        HorizontalLayout holder = new HorizontalLayout();
        Table basicTable = buildTable(basicCaption, addAlignmentStyle, property);

        Table editableTable = buildTable(editableCaption, addAlignmentStyle,
                property);
        editableTable.setEditable(true);

        Table buttonTable = buildTable(basicCaption + " + button",
                addAlignmentStyle, property, BUTTON);

        holder.addComponent(basicTable);
        holder.addComponent(editableTable);
        holder.addComponent(buttonTable);
        return holder;
    }

    private Table buildTable(String caption,
            final boolean addAlignmentStylename, Object... visibleColumns) {
        Table table = new Table(caption, buildDataSource());
        if (addAlignmentStylename) {
            table.addStyleName(Reindeer.TABLE_FULL_HEIGHT_FIELDS);
        }
        table.setTableFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Container container, Object itemId,
                    Object propertyId, Component uiContext) {
                Field field = super.createField(container, itemId, propertyId,
                        uiContext);
                if (ENUM.equals(propertyId)) {
                    field = new ComboBox(field.getCaption(), Arrays
                            .asList(MyEnum.values()));
                }
                return field;
            }
        });

        table.addGeneratedColumn(BUTTON, new ColumnGenerator() {
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("Button");
            }
        });

        int columnWidth = 160;
        table.setColumnWidth(STRING, columnWidth);
        table.setColumnWidth(DATE, columnWidth);
        table.setColumnWidth(ENUM, columnWidth);
        table.setColumnWidth(BUTTON, columnWidth);

        table.setPageLength(0);
        if (visibleColumns != null && visibleColumns.length != 0) {
            table.setVisibleColumns(visibleColumns);
        }
        return table;
    }

    private Container buildDataSource() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(STRING, String.class, "");
        container.addContainerProperty(DATE, Date.class, new Date(
                1348056073440l));
        container.addContainerProperty(ENUM, MyEnum.class, MyEnum.VALUE1);

        Item item1 = container.addItem(Integer.valueOf(1));
        item1.getItemProperty(STRING).setValue("String 1");

        Item item2 = container.addItem(Integer.valueOf(2));
        item2.getItemProperty(STRING).setValue("String 2");
        item2.getItemProperty(ENUM).setValue(MyEnum.VALUE2);

        return container;
    }

    @Override
    protected String getDescription() {
        return "Test case for checking how various fields are aligned in the rows of a Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9195);
    }

}
