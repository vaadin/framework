package com.vaadin.tests.server.component.listselect;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.ListSelect;

/**
 * List select declarative test.
 * <p>
 * There is only {@link ListSelect#setRows(int)}/{@link ListSelect#getRows()}
 * explicit test. All other tests are in the super class (
 * {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
@SuppressWarnings("rawtypes")
public class ListSelectDeclarativeTest
        extends AbstractMultiSelectDeclarativeTest<ListSelect> {

    @Test
    public void rowsPropertySerialization() {
        int rows = 7;
        String design = String.format("<%s rows='%s'/>", getComponentTag(),
                rows);

        ListSelect<String> select = new ListSelect<>();
        select.setRows(rows);

        testRead(design, select);
        testWrite(design, select);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-list-select";
    }

    @Override
    protected Class<? extends ListSelect> getComponentClass() {
        return ListSelect.class;
    }

}
