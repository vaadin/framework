/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.exceptions.NoSuchColumnException;

@Deprecated
public class TableRowElement extends AbstractComponentElement {

    /**
     * Returns cell from current row by index. Returns the same element as
     * $(TableElement.class).first().getCell(row, col).
     *
     * @see com.vaadin.testbench.elements.TableElement#getCell(int, int)
     * @param col
     *            column index
     * @return cell from current row by index.
     */
    public TestBenchElement getCell(int col) {
        List<WebElement> cells = getWrappedElement()
                .findElements(By.tagName("td"));
        if (col >= cells.size()) {
            throw new NoSuchColumnException();
        }

        WebElement cellContent = cells.get(col);
        return wrapElement(cellContent.findElement(By.xpath("./*")),
                getCommandExecutor());
    }
}
