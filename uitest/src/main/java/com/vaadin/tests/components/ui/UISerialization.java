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
package com.vaadin.tests.components.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class UISerialization extends AbstractTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        addComponent(new Button("Serialize UI", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Date d = new Date();
                try {
                    byte[] result = serialize(UISerialization.this);
                    long elapsed = new Date().getTime() - d.getTime();
                    log.log("Serialized UI in " + elapsed + "ms into "
                            + result.length + " bytes");
                    Object diffStateBefore = getConnectorTracker()
                            .getDiffState(UISerialization.this);
                    UISerialization app = (UISerialization) deserialize(result);
                    log.log("Deserialized UI in " + elapsed + "ms");
                    Object diffStateAfter = getConnectorTracker().getDiffState(
                            UISerialization.this);
                    if (diffStateBefore.equals(diffStateAfter)) {
                        log.log("Diff states match, size: "
                                + diffStateBefore.toString().length());
                    } else {
                        log.log("Diff states do not match");
                    }
                } catch (Exception e) {
                    log.log("Exception caught: " + e.getMessage());
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    addComponent(new Label(sw.toString(),
                            ContentMode.PREFORMATTED));
                }

            }
        }));
    }

    protected void serializeInstance(Class<?> cls)
            throws InstantiationException, IllegalAccessException, IOException {
        serialize((Serializable) cls.newInstance());
    }

    protected byte[] serialize(Serializable serializable) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        oos = new ObjectOutputStream(os);
        oos.writeObject(serializable);
        return os.toByteArray();
    }

    protected Object deserialize(byte[] result) {
        ByteArrayInputStream is = new ByteArrayInputStream(result);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(is);
            return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
