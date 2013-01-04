package com.vaadin.tests.applicationservlet;

import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeSelect;

public class SystemMessagesTest extends AbstractTestUI {

    public class MyButton extends Button {
        private boolean fail = false;

        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (fail) {
                throw new RuntimeException("Failed on purpose");
            }
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        final NativeSelect localeSelect = new NativeSelect("UI locale");
        localeSelect.setImmediate(true);
        localeSelect.addItem(new Locale("en", "US"));
        localeSelect.addItem(new Locale("fi", "FI"));
        localeSelect.addItem(Locale.GERMANY);
        localeSelect.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setLocale((Locale) localeSelect.getValue());
                getSession().getService().setSystemMessagesProvider(
                        new SystemMessagesProvider() {

                            @Override
                            public SystemMessages getSystemMessages(
                                    SystemMessagesInfo systemMessagesInfo) {
                                CustomizedSystemMessages csm = new CustomizedSystemMessages();
                                // csm.setInternalErrorCaption("Request query string: "
                                // + ((VaadinServletRequest) systemMessagesInfo
                                // .getRequest()).getQueryString());
                                csm.setInternalErrorMessage("MessagesInfo locale: "
                                        + systemMessagesInfo.getLocale());
                                return csm;

                            }
                        });
            }
        });
        localeSelect.setValue(new Locale("fi", "FI"));
        addComponent(localeSelect);
        final MyButton failButton = new MyButton();
        failButton.setCaption("Generate server side error");
        failButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                failButton.fail = true;
                failButton.markAsDirty();
            }
        });
        addComponent(failButton);

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
