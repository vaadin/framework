/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Table;

public class TableCellStyle extends CustomComponent {
    public TableCellStyle() {
        Table table = new Table("Table with Cell Styles");
        table.addStyleName("checkerboard");
        
        // Add some columns in the table. In this example, the property IDs
        // of the container are integers so we can determine the column number
        // easily.
        table.addContainerProperty("0", String.class, null, "", null, null); // Row header
        for (int i=0; i<8; i++)
            table.addContainerProperty(""+(i+1), String.class, null,
                                       String.valueOf((char) (65+i)), null, null);
        
        // Add some items in the table.
        table.addItem(new Object[]{"1", "R", "N", "B", "Q", "K", "B", "N", "R"}, new Integer(0));
        table.addItem(new Object[]{"2", "P", "P", "P", "P", "P", "P", "P", "P"}, new Integer(1));
        for (int i=2; i<6; i++)
            table.addItem(new Object[]{String.valueOf(i+1), "", "", "", "", "", "", "", ""}, new Integer(i));
        table.addItem(new Object[]{"7", "P", "P", "P", "P", "P", "P", "P", "P"}, new Integer(6));
        table.addItem(new Object[]{"8", "R", "N", "B", "Q", "K", "B", "N", "R"}, new Integer(7));
        table.setPageLength(8);
        
        // Set cell style generator
        table.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Object itemId, Object propertyId) {
                int row = ((Integer)itemId).intValue();
                int col = Integer.parseInt((String)propertyId);
                
                // The first column.
                if (col == 0)
                    return "rowheader";
                
                // Other cells.
                if ((row+col)%2 == 1)
                    return "black";
                else
                    return "white";
            }
        });
        
        setCompositionRoot(table);
    }
}
