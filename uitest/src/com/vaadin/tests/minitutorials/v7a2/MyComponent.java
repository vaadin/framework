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

package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentClientRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentServerRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentState;
import com.vaadin.ui.AbstractComponent;

public class MyComponent extends AbstractComponent {
    private int clickCount = 0;

    private MyComponentServerRpc rpc = new MyComponentServerRpc() {
        @Override
        public void clicked(MouseEventDetails mouseDetails) {
            clickCount++;

            // nag every 5:th click
            if (clickCount % 5 == 0) {
                getRpcProxy(MyComponentClientRpc.class).alert(
                        "Ok, that's enough!");
            }

            setText("You have clicked " + clickCount + " times");
        }
    };

    public MyComponent() {
        registerRpc(rpc);
    }

    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }

    public void setText(String text) {
        getState().text = text;
    }

    public String getText() {
        return getState().text;
    }
}
