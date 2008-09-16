/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Container.Indexed;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * This example demonstrates the use of generated columns in a table.
 * Generated columns can be used for formatting values or calculating
 * them from other columns (or properties of the items).
 * 
 * For the data model, we use POJOs bound to a custom Container
 * with BeanItem items.
 * 
 * @author magi
 */
public class GeneratedColumnExample extends CustomComponent {
    /**
     * The business model: fill-up at a gas station.
     */
    public class FillUp {
        Date date;
        double quantity;
        double total;

        public FillUp() {
        }

        public FillUp(int day, int month, int year, double quantity, double total) {
            date = new GregorianCalendar(year, month-1, day).getTime();
            this.quantity = quantity;
            this.total = total;
        }

        /** Calculates price per unit of quantity (€/l). */
        public double price() {
            if (quantity != 0.0)
                return total / quantity;
            else
                return 0.0;
        }

        /** Calculates average daily consumption between two fill-ups. */
        public double dailyConsumption(FillUp other) {
            double difference_ms = date.getTime() - other.date.getTime();
            double days = difference_ms / 1000 / 3600 / 24;
            if (days < 0.5)
                days = 1.0; // Avoid division by zero if two fill-ups on the same day.
            return quantity / days;
        }

        /** Calculates average daily consumption between two fill-ups. */
        public double dailyCost(FillUp other) {
            return price() * dailyConsumption(other);
        }

        // Getters and setters

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    };

    /**
     * This is a custom container that allows adding BeanItems inside it. The
     * BeanItem objects must be bound to an object. The item ID is an Integer
     * from 0 to 99.
     * 
     * Most of the interface methods are implemented with just dummy
     * implementations, as they are not needed in this example.
     */
    public class MySimpleIndexedContainer implements Container,Indexed {
        Vector items;
        Object itemtemplate;
        
        public MySimpleIndexedContainer(Object itemtemplate) {
            this.itemtemplate = itemtemplate;
            items = new Vector(); // Yeah this is just a test
        }

        public boolean addContainerProperty(Object propertyId, Class type,
                Object defaultValue) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Item addItem(Object itemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Object addItem() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * This addItem method is specific for this container and allows adding
         * BeanItem objects. The BeanItems must be bound to MyBean objects.
         */
        public void addItem(BeanItem item) throws UnsupportedOperationException {
            items.add(item);
        }

        public boolean containsId(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < items.size())
                    return items.get(pos) != null;
            }
            return false;
        }

        /**
         * The Table will call this method to get the property objects for the
         * columns. It uses the property objects to determine the data types of
         * the columns.
         */
        public Property getContainerProperty(Object itemId, Object propertyId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < items.size()) {
                    Item item = (Item) items.get(pos);
                    
                    // The BeanItem provides the property objects for the items.
                    return item.getItemProperty(propertyId);
                }
            }
            return null;
        }

        /** Table calls this to get the column names. */
        public Collection getContainerPropertyIds() {
            Item item = new BeanItem(itemtemplate);

            // The BeanItem knows how to get the property names from the bean.
            return item.getItemPropertyIds();
        }

        public Item getItem(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer)itemId).intValue();
                if (pos >= 0 && pos < items.size())
                    return (Item) items.get(pos);
            }
            return null;
        }

        public Collection getItemIds() {
            Vector ids = new Vector(items.size());
            for (int i = 0; i < items.size(); i++)
                ids.add(Integer.valueOf(i));
            return ids;
        }

        public Class getType(Object propertyId) {
            return BeanItem.class;
        }

        public boolean removeAllItems() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public boolean removeContainerProperty(Object propertyId)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public boolean removeItem(Object itemId)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return items.size();
        }

        public Object addItemAt(int index) throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Item addItemAt(int index, Object newItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getIdByIndex(int index) {
            return Integer.valueOf(index);
        }

        public int indexOfId(Object itemId) {
            return ((Integer) itemId).intValue();
        }

        public Object addItemAfter(Object previousItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Item addItemAfter(Object previousItemId, Object newItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Object firstItemId() {
            return new Integer(0);
        }

        public boolean isFirstId(Object itemId) {
            return ((Integer) itemId).intValue() == 0;
        }

        public boolean isLastId(Object itemId) {
            return ((Integer) itemId).intValue() == (items.size()-1);
        }

        public Object lastItemId() {
            return new Integer(items.size()-1);
        }

        public Object nextItemId(Object itemId) {
            int pos = indexOfId(itemId);
            if (pos >= items.size()-1)
                return null;
            return getIdByIndex(pos+1);
        }

        public Object prevItemId(Object itemId) {
            int pos = indexOfId(itemId);
            if (pos <= 0)
                return null;
            return getIdByIndex(pos-1);
        }
    }

    /** Formats the dates in a column containing Date objects. */
    class DateColumnGenerator implements Table.ColumnGenerator {
        /**
         * Generates the cell containing the Date value. The column is
         * irrelevant in this use case.
         */
        public Component generateCell(Table source, Object itemId, Object columnId) {
            Property prop = source.getItem(itemId).getItemProperty(columnId);
            if (prop.getType().equals(Date.class)) {
                Label label = new Label(String.format("%tF",
                        new Object[] { (Date) prop.getValue() }));
                label.addStyleName("column-type-date");
                return label;
            }

            return null;
        }
    }

    /** Formats the value in a column containing Double objects. */
    class ValueColumnGenerator implements Table.ColumnGenerator {
        String format; /* Format string for the Double values. */
    
        /** Creates double value column formatter with the given format string. */
        public ValueColumnGenerator(String format) {
            this.format = format;
        }
    
        /**
         * Generates the cell containing the Double value. The column is
         * irrelevant in this use case.
         */
        public Component generateCell(Table source, Object itemId, Object columnId) {
            Property prop = source.getItem(itemId).getItemProperty(columnId);
            if (prop.getType().equals(Double.class)) {
                Label label = new Label(String.format(format,
                        new Object[] { (Double) prop.getValue() }));
                
                // Set styles for the column: one indicating that it's a value and a more
                // specific one with the column name in it. This assumes that the column
                // name is proper for CSS.
                label.addStyleName("column-type-value");
                label.addStyleName("column-" + (String) columnId);
                return label;
            }
            return null;
        }
    }

    /** Table column generator for calculating price column. */
    class PriceColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            // Retrieve the item.
            BeanItem item = (BeanItem) source.getItem(itemId);

            // Retrieves the underlying POJO from the item.
            FillUp fillup = (FillUp) item.getBean();

            // Do the business logic
            double price = fillup.price();

            // Create the generated component for displaying the calcucated
            // value.
            Label label = new Label(String.format("%1.2f €",
                    new Object[] { new Double(price) }));

            // We set the style here. You can't use a CellStyleGenerator for
            // generated columns.
            label.addStyleName("column-price");
            return label;
        }
    }

    /** Table column generator for calculating consumption column. */
    class ConsumptionColumnGenerator implements Table.ColumnGenerator {
        /**
         * Generates a cell containing value calculated from the item.
         */
        public Component generateCell(Table source, Object itemId, Object columnId) {
            Indexed indexedSource = (Indexed) source.getContainerDataSource();

            // Can not calculate consumption for the first item.
            if (indexedSource.isFirstId(itemId)) {
                Label label = new Label("N/A");
                label.addStyleName("column-consumption");
                return label;
            }

            // Index of the previous item.
            Object prevItemId = indexedSource.prevItemId(itemId);

            // Retrieve the POJOs.
            FillUp fillup = (FillUp) ((BeanItem) indexedSource.getItem(itemId)).getBean();
            FillUp prev   = (FillUp) ((BeanItem) source.getItem(prevItemId)).getBean();

            // Do the business logic
            return generateCell(fillup, prev);
        }

        public Component generateCell(FillUp fillup, FillUp prev) {
            double consumption = fillup.dailyConsumption(prev);

            // Generate the component for displaying the calculated value.
            Label label = new Label(String.format("%3.2f l",
                    new Object[] { new Double(consumption) }));

            // We set the style here. You can't use a CellStyleGenerator for
            // generated columns.
            label.addStyleName("column-consumption");
            return label;
        }
    }

    /** Table column generator for calculating daily cost column. */
    class DailyCostColumnGenerator extends ConsumptionColumnGenerator {
        public Component generateCell(FillUp fillup, FillUp prev) {
            double dailycost = fillup.dailyCost(prev);

            // Generate the component for displaying the calculated value.
            Label label = new Label(String.format("%3.2f €",
                    new Object[] { new Double(dailycost) }));

            // We set the style here. You can't use a CellStyleGenerator for
            // generated columns.
            label.addStyleName("column-dailycost");
            return label;
        }
    }

    /**
     * Custom field factory that sets the fields as immediate.
     */
    public class ImmediateFieldFactory extends BaseFieldFactory {
        public Field createField(Class type, Component uiContext) {
            // Let the BaseFieldFactory create the fields
            Field field = super.createField(type, uiContext);
            
            // ...and just set them as immediate
            ((AbstractField)field).setImmediate(true);
            
            return field;
        }
    }

    public GeneratedColumnExample() {
        final Table table = new Table();

        // Define table columns. These include also the column for the generated
        // column, because we want to set the column label to something
        // different than the property ID.
        table.addContainerProperty("date",        Date.class,   null, "Date",                null, null);
        table.addContainerProperty("quantity",    Double.class, null, "Quantity (l)",        null, null);
        table.addContainerProperty("price",       Double.class, null, "Price (€/l)",         null, null);
        table.addContainerProperty("total",       Double.class, null, "Total (€)",           null, null);
        table.addContainerProperty("consumption", Double.class, null, "Consumption (l/day)", null, null);
        table.addContainerProperty("dailycost",   Double.class, null, "Daily Cost (€/day)",  null, null);

        // Define the generated columns and their generators.
        table.addGeneratedColumn("date",        new DateColumnGenerator());
        table.addGeneratedColumn("quantity",    new ValueColumnGenerator("%.2f l"));
        table.addGeneratedColumn("price",       new PriceColumnGenerator());
        table.addGeneratedColumn("total",       new ValueColumnGenerator("%.2f €"));
        table.addGeneratedColumn("consumption", new ConsumptionColumnGenerator());
        table.addGeneratedColumn("dailycost",   new DailyCostColumnGenerator());

        // Create a data source and bind it to the table.
        MySimpleIndexedContainer data = new MySimpleIndexedContainer(new FillUp());
        table.setContainerDataSource(data);

        // Generated columns are automatically placed after property columns, so
        // we have to set the order of the columns explicitly.
        table.setVisibleColumns(new Object[] { "date", "quantity", "price", "total", "consumption", "dailycost" });

        // Add some data.
        data.addItem(new BeanItem(new FillUp(19, 2,  2005, 44.96, 51.21)));
        data.addItem(new BeanItem(new FillUp(30, 3,  2005, 44.91, 53.67)));
        data.addItem(new BeanItem(new FillUp(20, 4,  2005, 42.96, 49.06)));
        data.addItem(new BeanItem(new FillUp(23, 5,  2005, 47.37, 55.28)));
        data.addItem(new BeanItem(new FillUp(6,  6,  2005, 35.34, 41.52)));
        data.addItem(new BeanItem(new FillUp(30, 6,  2005, 16.07, 20.00)));
        data.addItem(new BeanItem(new FillUp(2,  7,  2005, 36.40, 36.19)));
        data.addItem(new BeanItem(new FillUp(6,  7,  2005, 39.17, 50.90)));
        data.addItem(new BeanItem(new FillUp(27, 7,  2005, 43.43, 53.03)));
        data.addItem(new BeanItem(new FillUp(17, 8,  2005, 20,    29.18)));
        data.addItem(new BeanItem(new FillUp(30, 8,  2005, 46.06, 59.09)));
        data.addItem(new BeanItem(new FillUp(22, 9,  2005, 46.11, 60.36)));
        data.addItem(new BeanItem(new FillUp(14, 10, 2005, 41.51, 50.19)));
        data.addItem(new BeanItem(new FillUp(12, 11, 2005, 35.24, 40.00)));
        data.addItem(new BeanItem(new FillUp(28, 11, 2005, 45.26, 53.27)));

        // Have a check box that allows the user to make the quantity
        // and total columns editable.
        final CheckBox editable = new CheckBox("Edit the input values - calculated columns are regenerated");
        editable.setImmediate(true);
        editable.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                table.setEditable(editable.booleanValue());
                
                // The columns may not be generated when we want to have them
                // editable.
                if (editable.booleanValue()) {
                    table.removeGeneratedColumn("quantity");
                    table.removeGeneratedColumn("total");
                } else {
                    // In non-editable mode we want to show the formatted values.
                    table.addGeneratedColumn("quantity", new ValueColumnGenerator("%.2f l"));
                    table.addGeneratedColumn("total",    new ValueColumnGenerator("%.2f €"));
                }
                // The visible columns are affected by removal and addition of
                // generated columns so we have to redefine them.
                table.setVisibleColumns(
                        new Object[] { "date","quantity","price","total","consumption", "dailycost" });
            }
        });
        
        // Use a custom field factory to set the edit fields as immediate.
        // This is used when the table is in editable mode.
        table.setFieldFactory(new ImmediateFieldFactory());
        
        // Setting the table itself as immediate has no relevance in this example,
        // because it is relevant only if the table is selectable and we want to
        // get the selection changes immediately.
        table.setImmediate(true);

        table.setHeight("100%");

        ExpandLayout layout = new ExpandLayout();
        layout.addComponent(new Label("Table with column generators that format and calculate cell values."));
        layout.addComponent(table);
        layout.addComponent(editable);
        layout.addComponent(new Label("Columns displayed in blue are calculated from Quantity and Total. "+
                                      "Others are simply formatted."));
        layout.expand(table);
        layout.setSizeFull();
        setCompositionRoot(layout);
        setSizeFull();
    }
}
