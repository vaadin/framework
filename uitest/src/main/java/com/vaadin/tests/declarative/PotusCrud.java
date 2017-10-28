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
package com.vaadin.tests.declarative;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

@DesignRoot
public class PotusCrud extends VerticalLayout {

    public Table potusList;
    public PotusForm potusForm;
    public Button addNew;

    private FieldGroup fg;

    private BeanItemContainer<Potus> potusContainer = new BeanItemContainer<>(
            Potus.class);

    public PotusCrud() {
        Design.read(this);
        init();
    }

    private void init() {
        initTable();
        initForm();
        addNew.addClickListener(event -> doAdd());
    }

    private void initTable() {
        potusList.setContainerDataSource(potusContainer);
        potusList.addValueChangeListener(event -> doEdit());
    }

    private void initForm() {
        potusForm.save.addClickListener(evenet -> doSave());
        potusForm.delete.addClickListener(event -> doDelete());
        potusForm.revert.addClickListener(event -> doRevert());
        fg = new FieldGroup();
    }

    protected void doRevert() {
        fg.discard();
    }

    protected void doDelete() {
        potusContainer.removeItem(potusList.getValue());
        fg.setItemDataSource(null);
    }

    protected void doSave() {
        try {
            fg.commit();
        } catch (CommitException e) {
            e.printStackTrace();
        }
    }

    protected void doAdd() {
        potusContainer.addBean(new Potus());
    }

    protected void doEdit() {
        if (potusList.getValue() != null) {
            fg.setItemDataSource(potusList.getItem(potusList.getValue()));
            fg.bindMemberFields(potusForm);
        } else {
            fg.setItemDataSource(null);
        }
    }

}
