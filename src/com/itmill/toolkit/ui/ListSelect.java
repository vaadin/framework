package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * This is a simple list select without, for instance, support for new items,
 * lazyloading, and other advanced features.
 */
public class ListSelect extends AbstractSelect {

    private int columns = 0;
    private int rows = 0;

    public ListSelect() {
        super();
    }

    public ListSelect(String caption, Collection options) {
        super(caption, options);
    }

    public ListSelect(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public ListSelect(String caption) {
        super(caption);
    }

    /**
     * Sets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     * 
     * @param columns
     *                the number of columns to set.
     */
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        if (this.columns != columns) {
            this.columns = columns;
            requestRepaint();
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor. If the number of rows is set 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @param rows
     *                the number of rows to set.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            requestRepaint();
        }
    }

    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "list");
        // Adds the number of columns
        if (columns != 0) {
            target.addAttribute("cols", columns);
        }
        // Adds the number of rows
        if (rows != 0) {
            target.addAttribute("rows", rows);
        }
        super.paintContent(target);
    }
}
