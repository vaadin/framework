package com.itmill.toolkit.demo.sampler;

import java.util.HashMap;
import java.util.Iterator;

import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ModeSwitch extends CustomComponent {

    GridLayout layout = new GridLayout(3, 1);

    HashMap<Object, Button> idToButton = new HashMap<Object, Button>();
    Object mode = null;

    public ModeSwitch() {
        setSizeUndefined();
        layout.setSizeUndefined();
        setCompositionRoot(layout);
        setStyleName("ModeSwitch");
    }

    public Object getMode() {
        return mode;
    }

    public void setMode(Object mode) {
        if (idToButton.containsKey(mode)) {
            this.mode = mode;
            updateStyles();
            fireEvent(new ModeSwitchEvent());
        }
    }

    public void addListener(ModeSwitchListener listener) {
        super.addListener(listener);
    }

    public void addMode(Object id, String caption, String description,
            Resource icon) {
        if (idToButton.containsKey(id)) {
            removeMode(id);
        }
        Button b = new Button();
        if (caption != null) {
            b.setCaption(caption);
        }
        if (description != null) {
            b.setDescription(description);
        }
        if (icon != null) {
            b.setIcon(icon);
        }
        b.setData(id);
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setMode(event.getButton().getData());
            }
        });
        idToButton.put(id, b);
        layout.addComponent(b);
        updateStyles();
    }

    public void removeMode(Object id) {
        Button b = idToButton.remove(id);
        layout.removeComponent(b);
        updateStyles();
    }

    private void updateStyles() {
        boolean first = true;
        for (Iterator it = layout.getComponentIterator(); it.hasNext();) {
            Button b = (Button) it.next();
            String isOn = (b.getData() == mode ? "-on" : "");
            if (first) {
                first = false;
                b.setStyleName("first" + isOn);
            } else if (!it.hasNext()) {
                b.setStyleName("last" + isOn);
            } else {
                b.setStyleName("mid" + isOn);
            }
        }
    }

    public interface ModeSwitchListener extends Listener {

    }

    public class ModeSwitchEvent extends Event {

        public ModeSwitchEvent() {
            super(ModeSwitch.this);
        }

        public Object getMode() {
            return ModeSwitch.this.getMode();
        }
    }

}
