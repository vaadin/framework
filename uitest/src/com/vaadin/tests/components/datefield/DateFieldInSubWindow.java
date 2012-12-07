package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DateFieldInSubWindow extends AbstractTestCase {

    @SuppressWarnings("serial")
    public class TestCaseWindow extends Window {

        public class MyBean {
            private Date myDate;
            private String myString;

            public Date getMyDate() {
                return myDate;
            }

            public void setMyDate(Date myDate) {
                this.myDate = myDate;
            }

            public String getMyString() {
                return myString;
            }

            public void setMyString(String myString) {
                this.myString = myString;
            }

        }

        private MyBean myBean;

        public TestCaseWindow() {
            super("Test Case Window");
            setModal(true);
            setWidth("400px");
            myBean = new MyBean();

            initWindow();
        }

        protected class CustomerFieldFactory extends DefaultFieldFactory {

            public static final String COMMON_FIELD_WIDTH = "12em";

            @Override
            public Field<?> createField(Item item, Object propertyId,
                    Component uiContext) {
                Field<?> f = super.createField(item, propertyId, uiContext);

                if ("myDate".equals(propertyId)) {
                    ((DateField) f).setResolution(DateField.RESOLUTION_MIN);
                    ((DateField) f).setCaption("This is my date");

                }

                return f;
            }
        }

        protected void initWindow() {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            layout.setSpacing(true);
            setContent(layout);

            /**
             * This causes the window to add the .v-readonly style!
             */
            setClosable(false);

            CustomerFieldFactory fieldFactory = new CustomerFieldFactory();
            final Form generalForm = new Form();
            {
                generalForm.setFooter(null);
                generalForm.setCaption("My form");
                generalForm.setBuffered(false);
                generalForm.setFormFieldFactory(fieldFactory);

                BeanItem<MyBean> myBeanItem = new BeanItem<MyBean>(myBean);
                generalForm.setItemDataSource(myBeanItem);

                generalForm.setVisibleItemProperties(new String[] { "myDate",
                        "myString" });
                generalForm.setValidationVisible(true);
                layout.addComponent(generalForm);
            }

            HorizontalLayout buttons = new HorizontalLayout();
            {
                buttons.setSpacing(true);

                Button b = new Button("Close", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        TestCaseWindow.this.close();
                    }
                });
                buttons.addComponent(b);
                layout.addComponent(buttons);

            }
        }
    }

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow();
        setMainWindow(mainWindow);
        Button open = new Button("Open window", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().addWindow(new TestCaseWindow());
            }
        });

        mainWindow.addComponent(open);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 4582;
    }

}
