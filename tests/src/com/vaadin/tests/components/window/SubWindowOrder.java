package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class SubWindowOrder extends TestBase {

    @Override
    protected void setup() {
        Window mainWindow = getMainWindow();
        HorizontalLayout controlpanels = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            Window dialog = new Window("Dialog " + i);
            dialog.addComponent(new Label("this is dialog number " + i));
            mainWindow.addWindow(dialog);
            controlpanels.addComponent(new ControlPanel(dialog));
        }
        getLayout().setSizeFull();
        getLayout().addComponent(controlpanels);
        getLayout().setComponentAlignment(controlpanels, Alignment.BOTTOM_LEFT);
    }

    @Override
    protected String getDescription() {
        return "Subwindows should be rendered in the same order as they are added.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3363;
    }

    class ControlPanel extends CssLayout implements ClickListener {
        private Window w;
        private Button bf = new Button("Bring to front");
        private Button toggleModality = new Button("Toggle modality");

        public ControlPanel(Window w) {
            this.w = w;
            setCaption("Control window " + w.getCaption() + ":");
            addComponent(bf);
            addComponent(toggleModality);
            bf.addListener(this);
            toggleModality.addListener(this);
        }

        public void buttonClick(ClickEvent event) {
            if (event.getButton() == bf) {
                w.bringToFront();
            } else if (event.getButton() == toggleModality) {
                w.setModal(!w.isModal());
            }

        }
    }

}
