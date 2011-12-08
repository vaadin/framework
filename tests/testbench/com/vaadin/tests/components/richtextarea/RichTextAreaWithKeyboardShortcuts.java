package com.vaadin.tests.components.richtextarea;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RichTextAreaWithKeyboardShortcuts extends TestBase {

    private Handler actionHandler = new Handler() {

        ShortcutAction save = new ShortcutAction("^Save");
        private Action[] actions = new Action[] { save };

        public void handleAction(Action action, Object sender, Object target) {
            String msg = "Action: " + action.getCaption();
            msg += " From : " + sender.getClass().getSimpleName() + " '"
                    + ((Component) sender).getCaption() + "'";

            AbstractField<String> f = (AbstractField<String>) target;
            msg += " Target:" + target.getClass().getSimpleName() + " '"
                    + f.getCaption() + "'";

            String string = f.getValue().toString();

            msg += " Value: " + string;
            f.getRoot().showNotification(msg);

        }

        public Action[] getActions(Object target, Object sender) {
            return actions;
        }
    };

    @Override
    protected void setup() {

        getLayout().getRoot().addActionHandler(actionHandler);
        getLayout().addComponent(createRichTextArea("InMainLayout"));

        Panel panel = new Panel("RTA Panel");
        panel.addActionHandler(actionHandler);
        panel.getContent().addComponent(createRichTextArea("InPanel"));
        getLayout().addComponent(panel);

        Window w = new Window("SubWindow");
        w.addActionHandler(actionHandler);
        w.addComponent(createRichTextArea("InSubWindow"));
        w.getContent().setSizeUndefined();

        getLayout().getRoot().addWindow(w);

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
