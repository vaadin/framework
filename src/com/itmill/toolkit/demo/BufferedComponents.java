/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.Notification;

public class BufferedComponents extends Application {

    private ObjectProperty property;

    private TextField text;

    public void init() {

        final Window w = new Window("Buffered UI components demo");
        addWindow(w);

        // Create property
        final Float floatValue = new Float(1.0f);
        property = new ObjectProperty(floatValue);

        // Textfield
        text = new TextField("TextField (Buffered, using ObjectProperty)",
                property);
        text.setImmediate(true);
        text.setWriteThrough(false);
        w.addComponent(text);

        // Property state
        final Label propertyState = new Label(property);
        propertyState.setCaption("Property (data source) state");
        w.addComponent(propertyState);

        // Button state
        final Label textState = new Label(text);
        textState.setCaption("TextField state");
        w.addComponent(textState);

        // Button to change the property
        w.addComponent(new Button("increase property value",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        final Float currentValue = (Float) property.getValue();
                        property.setValue(new Float(
                                currentValue.floatValue() + 1.0));
                    }
                }));

        // Buffering
        w.addComponent(new Button("Write through enabled", new MethodProperty(
                text, "writeThrough")));
        w.addComponent(new Button("discard", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                text.discard();
            }
        }));
        Button commit = new Button("commit", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                    text.commit();
                    w.showNotification("Committed " + property
                            + " to datasource.");
                } catch (Throwable e) {
                    w.showNotification("Error committing an invalid value: "
                            + text, Notification.TYPE_WARNING_MESSAGE);
                }
            }
        });
        w.addComponent(commit);

        // Restart button for application
        // (easier debugging when you dont have to restart the server to
        // make
        // code changes)
        final Button restart = new Button("restart", this, "close");
        restart.addStyleName(Button.STYLE_LINK);
        w.addComponent(restart);
    }
}
