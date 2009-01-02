package com.itmill.toolkit.tests.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ChatApplication extends Application implements
        Button.ClickListener {
    /*
     * ChatApplication instances of different users. Warning: a hack, not safe,
     * because sessions can expire.
     */
    static List users = new ArrayList();

    /* Messages as a shared list. */
    static List messages = new ArrayList();
    int localSize = 0;

    /* User interface. */
    Table messageTable = new Table();
    TextField username = new TextField("Username:");
    TextField message = new TextField("Message:");

    @Override
    public void init() {
        final Window main = new Window("Chat");
        setMainWindow(main);
        setTheme("tests-magi");
        users.add(this);

        main.addComponent(username);

        main.addComponent(messageTable);
        messageTable.addContainerProperty("Sender", String.class, "");
        messageTable.addContainerProperty("Message", String.class, "");
        updateTable();

        main.addComponent(message);

        Button send = new Button("Send");
        send.addListener(this);
        main.addComponent(send);

        // Poll for new messages once a second.
        ProgressIndicator poller = new ProgressIndicator();
        poller.addStyleName("invisible");
        main.addComponent(poller);
    }

    public void buttonClick(ClickEvent event) {
        synchronized (users) {
            // Create the new message in the shared list.
            messages.add(new String[] {
                    new String((String) username.getValue()),
                    new String((String) message.getValue()) });

            // Update the message tables for all users.
            for (Iterator i = users.iterator(); i.hasNext();) {
                ((ChatApplication) i.next()).updateTable();
            }
        }
    }

    void updateTable() {
        if (localSize == messages.size()) {
            return; // No updating needed
        }

        // Add new messages to the table
        while (localSize < messages.size()) {
            messageTable.addItem((Object[]) messages.get(localSize++),
                    new Integer(localSize - 1));
        }
    }
}
