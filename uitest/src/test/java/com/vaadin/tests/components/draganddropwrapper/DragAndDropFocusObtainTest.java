package com.vaadin.tests.components.draganddropwrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.DragAndDropWrapper;

/**
 * Test for text area inside {@link DragAndDropWrapper}: text area should obtain
 * focus on click.
 *
 * @since
 * @author Vaadin Ltd
 */
public class DragAndDropFocusObtainTest extends MultiBrowserTest {

    @Test
    public void testTextAreaDndImage() {
        openTestURL();
        int index = 1;
        for (AbstractTextFieldElement ta : $(AbstractTextFieldElement.class)
                .all()) {
            String caption = ta.getCaption();
            ta.click();
            assertEquals(index + ". Field '" + caption + "' focused",
                    getLogRow(0));
            index++;
        }

        // Make sure we checked all fields
        assertEquals(8 + 1, index);

    }

}
