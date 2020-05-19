/*
* Copyright 2000-2020 Vaadin Ltd.
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
package com.vaadin.v7.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;

/**
 * Test for issue #11246 where ComboBox set to render from Property does not
 * filter correctly when page size is 0
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("deprecation")
public class ComboboxPageLengthZeroFilter extends AbstractTestUI {

    public static class Topping {
        private int id;
        private String name;

        public Topping(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        BeanContainer<Integer, Topping> container = new BeanContainer<Integer, Topping>(
                Topping.class);
        container.setBeanIdProperty("id");
        for (int i = 0; i < 12; i++) {
            container.addBean(new Topping(i, "Topping " + i));
        }

        final ComboBox comboBox = new ComboBox();
        comboBox.setPageLength(0);
        comboBox.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        comboBox.setItemCaptionPropertyId("name");
        comboBox.setContainerDataSource(container);
        comboBox.setInvalidAllowed(false);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setTextInputAllowed(true);

        getLayout().addComponent(comboBox);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11246;
    }

    @Override
    protected String getTestDescription() {
        return "Filtering should work even when ComboBox page length is zero.";
    }
}
