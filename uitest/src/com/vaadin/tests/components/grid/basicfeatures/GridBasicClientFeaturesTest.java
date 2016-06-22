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
package com.vaadin.tests.components.grid.basicfeatures;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.element.CustomGridElement;

/**
 * GridBasicClientFeatures.
 * 
 * @author Vaadin Ltd
 */
public abstract class GridBasicClientFeaturesTest extends GridBasicFeaturesTest {

    private boolean composite = false;

    @Override
    protected Class<?> getUIClass() {
        return GridBasicClientFeatures.class;
    }

    @Override
    protected String getDeploymentPath() {
        String path = super.getDeploymentPath();
        if (composite) {
            path += (path.contains("?") ? "&" : "?") + "composite";
        }
        return path;
    }

    protected void setUseComposite(boolean useComposite) {
        composite = useComposite;
    }

    @Override
    protected void selectMenu(String menuCaption) {
        // GWT menu does not need to be clicked.
        selectMenu(menuCaption, false);
    }

    @Override
    protected WebElement getMenuElement(String menuCaption) {
        return getDriver().findElement(
                By.xpath("//td[text() = '" + menuCaption + "']"));
    }

    @Override
    protected CustomGridElement getGridElement() {
        if (composite) {
            // Composite requires the basic client features widget for subparts
            return ((TestBenchElement) findElement(By
                    .vaadin("//TestWidgetComponent")))
                    .wrap(CustomGridElement.class);
        } else {
            return super.getGridElement();
        }
    }

    @Override
    protected void assertColumnHeaderOrder(int... indices) {
        List<TestBenchElement> headers = getGridHeaderRowCells();
        for (int i = 0; i < indices.length; i++) {
            assertColumnHeader("HEADER (0," + indices[i] + ")", headers.get(i));
        }
    }

    protected void toggleColumnReorder() {
        selectMenuPath("Component", "State", "Column Reordering");
    }

    protected boolean isRowSelected(int index) {
        boolean selected = getGridElement().getRow(index).isSelected();
        return selected;
    }

    protected void setSelectionModelMulti() {
        setSelectionModel("multi");
    }

    protected void setSelectionModelSingle(boolean deselectAllowed) {
        String mode = "single";
        if (!deselectAllowed) {
            mode += " (no deselect)";
        }
        setSelectionModel(mode);
    }

    protected void setSelectionModelNone() {
        setSelectionModel("none");
    }

    protected void setSelectionModel(String model) {
        selectMenuPath("Component", "State", "Selection mode", model);
    }
}
