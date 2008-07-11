/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TableExample3 extends CustomComponent {
    /* A layout needed for the example. */
    OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
    
    TableExample3() {
        setCompositionRoot(layout);

        // Create a table and add a style to allow setting the row height in theme.
        final Table table = new Table();
        table.addStyleName("components-inside");
        
        /* Define the names and data types of columns.
         * The "default value" parameter is meaningless here. */
        table.addContainerProperty("Sum",            Label.class,     null);
        table.addContainerProperty("Is Transferred", CheckBox.class,  null);
        table.addContainerProperty("Comments",       TextField.class, null);
        table.addContainerProperty("Details",        Button.class,    null);

        /* Add a few items in the table. */
        for (int i=0; i<100; i++) {
            // Create the fields for the current table row
            Label sumField = new Label(String.format("Sum is <b>$%04.2f</b><br/><i>(VAT incl.)</i>",
                                  new Object[] {new Double(Math.random()*1000)}),
                                  Label.CONTENT_XHTML);
            CheckBox transferredField = new CheckBox("is transferred");
            
            // Multiline text field. This required modifying the height of the
            // table row.
            TextField commentsField = new TextField();
            commentsField.setRows(3);
            
            // The Table item identifier for the row.
            Integer itemId = new Integer(i);
            
            // Create a button and handle its click. A Button does not know
            // the item it is contained in, so we have to store the item
            // ID as user-defined data.
            Button detailsField = new Button("show details");
            detailsField.setData(itemId);
            detailsField.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    // Get the item identifier from the user-defined data.
                    Integer itemId = (Integer)event.getButton().getData();
                    getWindow().showNotification("Link "+itemId.intValue()+" clicked.");
                } 
            });
            detailsField.addStyleName("link");
            
            // Create the table row.
            table.addItem(new Object[] {sumField, transferredField,
                                        commentsField, detailsField},
                          itemId);
        }
        
        /* Show just three rows because they are so high. */
        table.setPageLength(3);

        layout.addComponent(table);
    }
}
