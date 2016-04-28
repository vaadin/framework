package com.vaadin.tests.components.richtextarea;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RichTextAreaWithKeyboardShortcuts extends TestBase {

    private Handler actionHandler = new Handler() {

        ShortcutAction save = new ShortcutAction("^Save");
        private Action[] actions = new Action[] { save };

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            String msg = "Action: " + action.getCaption();
            msg += " From : " + sender.getClass().getSimpleName() + " '"
                    + ((Component) sender).getCaption() + "'";

            AbstractField<String> f = (AbstractField<String>) target;
            msg += " Target:" + target.getClass().getSimpleName() + " '"
                    + f.getCaption() + "'";

            String string = f.getValue().toString();

            msg += " Value: " + string;
            Notification notification = new Notification(msg);
            notification.setHtmlContentAllowed(true);
            notification.show(Page.getCurrent());

        }

        @Override
        public Action[] getActions(Object target, Object sender) {
            return actions;
        }
    };

    @Override
    protected void setup() {

        getLayout().getUI().addActionHandler(actionHandler);
        getLayout().addComponent(createRichTextArea("InMainLayout"));

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel("RTA Panel", panelLayout);
        panel.addActionHandler(actionHandler);
        panelLayout.addComponent(createRichTextArea("InPanel"));
        getLayout().addComponent(panel);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("SubWindow", layout);
        w.addActionHandler(actionHandler);
        layout.addComponent(createRichTextArea("InSubWindow"));
        layout.setSizeUndefined();

        getLayout().getUI().addWindow(w);

    }

    private RichTextArea createRichTextArea(String caption) {
        RichTextArea rta = new RichTextArea(caption);
        return rta;
    }

    @Override
    protected String getDescription() {
        return "RichTextArea shouls support shortcut actions just like other components do.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4175;
    }

}
