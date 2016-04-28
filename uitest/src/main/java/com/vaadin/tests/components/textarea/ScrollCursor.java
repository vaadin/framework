package com.vaadin.tests.components.textarea;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;

/**
 * @author denis
 * 
 */
public class ScrollCursor extends TestBase {

    private TextArea textArea;
    private int position;

    @Override
    protected void setup() {
        textArea = new TextArea();
        textArea.setValue("saddddddddddd     fdgdfgfdgfd\n"
                + "aasddddddddddd\n" + "dsaffffffdsf\n" + "sdf\n"
                + "dsfsdfsdfsdfsd\n\n" + "ffffffffffffffffffff\n"
                + "sdfdsfdsfsdfsdfsd  xxxxxxxxxxxxxxxx\n" + "sdgfsd\n"
                + "dsf\n" + "ds\n" + "fds\n" + "fds\nfs");
        addComponent(textArea);
        Button button = new Button("Scroll");
        button.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                textArea.setCursorPosition(getPosition());
            }
        });
        Button wrap = new Button("Set wrap");
        wrap.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                textArea.setWordwrap(false);
            }
        });

        Button toBegin = new Button("To begin");
        toBegin.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                position = 3;
            }
        });

        Button toMiddle = new Button("To middle");
        toMiddle.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                position = 130;
            }
        });

        Button toEnd = new Button("To end");
        toEnd.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                position = textArea.getValue().toString().length();
            }
        });

        addComponent(button);
        addComponent(wrap);
        addComponent(toBegin);
        addComponent(toMiddle);
        addComponent(toEnd);
    }

    @Override
    protected String getDescription() {
        return "Tests scrolling for TextArea with different word wrapping settings. "
                + "Sets cursor position at the beginning, middle and the end "
                + "of text and checks textarea is scrolled.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8769;
    }

    private int getPosition() {
        return position;
    }

}
