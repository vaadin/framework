/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TableEditableBean extends CustomComponent {
	public class MyBean {
		boolean selected1;
		boolean selected2;
		
		public MyBean() {
			selected1 = false;
			selected2 = false;
		}

		public boolean isSelected1() {
			return selected1;
		}

		public void setSelected1(boolean selected) {
			this.selected1 = selected;
			System.out.println("setSelected1("+selected1+") called.");
		}

		public boolean isSelected2() {
			return selected2;
		}

		public void setSelected2(boolean selected) {
			this.selected2 = selected;
			System.out.println("setSelected2("+selected+") called.");
		}
	};

    TableEditableBean() {
        /* A layout needed for the example. */
        OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        setCompositionRoot(layout);

        // Create a table. It is by default not editable.
        final Table table = new Table();
        layout.addComponent(table);
        
        // Define the names and data types of columns.
        table.addContainerProperty("selected1", Boolean.class, null);
        table.addContainerProperty("selected2", Boolean.class, null);
        
        // Add a few items in the table.
        for (int i=0; i<100; i++) {
        	MyBean item = new MyBean();
        	BeanItem bitem = new BeanItem(item);
            table.addItem(new Object[] {bitem,bitem},
            		new Integer(i)); // Item identifier
        }
        
        table.setWriteThrough(true);
        table.setReadThrough(true);
        table.setPageLength(8);
        
        final CheckBox switchEditable = new CheckBox("Editable");
        switchEditable.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	table.commit();
                table.setEditable(((Boolean)event.getProperty().getValue()).booleanValue());
            }
        });
        switchEditable.setImmediate(true);
        layout.addComponent(switchEditable);
    }
}
