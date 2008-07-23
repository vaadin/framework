/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

public class TableEditableBean extends CustomComponent {
	public class MyBean {
		boolean selected;
		String  text;
		
		public MyBean() {
			selected = false;
			text     = "Hello";
		}

		public boolean isSelected() {
		    System.out.println("isSelected() called: " + selected);
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            System.out.println("setSelected1("+selected+") called.");
        }

        public String getText() {
            System.out.println("getText() called: " + text);
            return text;
        }

        public void setText(String text) {
            this.text = text;
            System.out.println("setText("+text+") called.");
        }
	};
	
	public class MyFieldFactory extends BaseFieldFactory {
	    public Field createField(Class type, Component uiContext) {
	        // Boolean field
            if (Boolean.class.isAssignableFrom(type)) {
                final CheckBox checkbox = new CheckBox();
                checkbox.setSwitchMode(true);
                checkbox.setImmediate(true);
                return checkbox;
            }
            return super.createField(type, uiContext);
	    }
	}
	
	public class MyTable extends Table {
	    /** Really adds an item and not just an item id. */
	    public void addItem(Item item, Object itemId) {
	        
	    }
	}

    TableEditableBean() {
        /* A layout needed for the example. */
        OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        setCompositionRoot(layout);

        // Create a table. It is by default not editable.
        final Table table = new Table();
        layout.addComponent(table);
        table.setPageLength(8);
        
        // Define the names and data types of columns.
        table.addContainerProperty("selected", Boolean.class, null);
        table.addContainerProperty("text",      String.class,  null);
        
        // Add a few items in the table.
        for (int i=0; i<5; i++) {
        	MyBean item = new MyBean();
        	BeanItem bitem = new BeanItem(item);
        	//table.addItem(bitem);
        	table.addItem(new Object[]{bitem,bitem}, new Integer(i));
        }
        
        // Use custom field factory that sets the checkboxes in immediate mode.
        table.setFieldFactory(new MyFieldFactory());

        // Have a check box to switch the table between normal and editable mode.
        final CheckBox switchEditable = new CheckBox("Editable");
        switchEditable.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                table.setEditable(((Boolean)event.getProperty().getValue()).booleanValue());
            }
        });
        switchEditable.setImmediate(true);
        layout.addComponent(switchEditable);
    }
}
