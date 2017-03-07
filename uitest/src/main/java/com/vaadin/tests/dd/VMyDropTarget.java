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
package com.vaadin.tests.dd;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VDropHandler;
import com.vaadin.client.ui.dd.VHasDropHandler;

public class VMyDropTarget extends Composite
        implements VHasDropHandler, VDropHandler {

    ApplicationConnection client;

    @Override
    public void dragEnter(VDragEvent drag) {
    }

    @Override
    public void dragLeave(VDragEvent drag) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dragOver(VDragEvent currentDrag) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean drop(VDragEvent drag) {
        // TODO Auto-generated method stub
        // return true to tell DDManager do server visit
        return false;
    }

    @Override
    public VDropHandler getDropHandler() {
        // Drophandler implemented by widget itself
        return this;
    }

    @Override
    public ComponentConnector getConnector() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ApplicationConnection getApplicationConnection() {
        return client;
    }

}
