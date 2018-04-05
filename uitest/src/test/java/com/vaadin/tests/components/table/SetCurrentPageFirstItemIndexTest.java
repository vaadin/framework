package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SetCurrentPageFirstItemIndexTest extends MultiBrowserTest {

    @Test
    public void currentPageIndexChangesTwice() {
        openTestURL();

        ButtonElement button = $(ButtonElement.class).first();
        button.click(); // change to 20
        button.click(); // change to 5

        // When failing, the index stays on 20.
        assertThatRowIsVisible(5);
    }

    private void assertThatRowIsVisible(int index) {
        try {
            TableElement table = $(TableElement.class).first();
            TestBenchElement cell = table.getCell(index, 0);

            assertThat(cell.getText(), is(Integer.toString(index + 1)));
        } catch (NoSuchElementException e) {
            fail(String.format("Can't locate row for index: %s", index));
        }
    }

}
