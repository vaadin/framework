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
package com.vaadin.tests.components.abstractcomponent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DesignSynchronizable;
import com.vaadin.ui.declarative.LayoutHandler;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ParseComponentHierarchy extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(
                    "C:\\Users\\mika\\Projects\\Vaadin\\vaadin\\server\\src\\com\\vaadin\\ui\\declarative\\testFile.html");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DesignSynchronizable root = LayoutHandler.parse(fis).getComponentRoot();
        addComponent(root);
    }
}
