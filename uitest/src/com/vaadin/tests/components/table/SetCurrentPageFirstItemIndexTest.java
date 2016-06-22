/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
@Ignore
// Enable after #15286 is fixed.
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
            Assert.fail(String.format("Can't locate row for index: %s", index));
        }
    }

}
