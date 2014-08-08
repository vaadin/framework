package com.vaadin.tests.components.table;

import java.util.Date;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Reindeer;

public class TableWithContainerRequiringEqualsForItemId extends AbstractTestUI {

    private MyEntityContainer container = new MyEntityContainer();
    private Log log = new Log(10);

    public static class MyEntityContainer extends BeanContainer<Long, MyEntity> {

        public MyEntityContainer() {
            super(MyEntity.class);
            setBeanIdResolver(new BeanIdResolver<Long, TableWithContainerRequiringEqualsForItemId.MyEntity>() {

                @Override
                public Long getIdForBean(MyEntity bean) {
                    // Return a new instance every time to ensure Table can
                    // handle it
                    return new Long(bean.getId());
                }
            });

        }

        @Override
        public Long getIdByIndex(int index) {
            // Explicitly get the id using the resolver to make sure the
            // instance does not stay the same
            BeanItem<MyEntity> beanItem = getItem(super.getIdByIndex(index));
            return getBeanIdResolver().getIdForBean(beanItem.getBean());
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        Table t = new Table("Table with 1000 item");
        t.addGeneratedColumn("Actions", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(final Table source,
                    final Object itemId, final Object columnId) {
                Button tripFolderLink = new Button("Button" + itemId);
                tripFolderLink.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        log.log("Button " + event.getButton().getCaption()
                                + " clicked");
                    }
                });
                tripFolderLink.setStyleName(Reindeer.BUTTON_SMALL);
                return tripFolderLink;
            }
        });

        for (int i = 0; i < 1000; i++) {
            MyEntity myEntity = new MyEntity(i + "st");
            myEntity.setCreated(new Date(new Date().getTime() - 24 * 60 * 60
                    * 1000L));
            myEntity.setId(i);
            container.addBean(myEntity);
        }

        t.setContainerDataSource(container);
        t.setVisibleColumns(new Object[] { "id", "created", "name", "Actions" });

        addComponent(t);
        addComponent(log);

        t.sort(new Object[] { "id" }, new boolean[] { false });

    }

    @Override
    protected String getTestDescription() {
        return "Test that verifies that Table works correctly with containers which do not return the same instance of the itemId object but instead requires an itemId.equals(otherItemId) check";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8712;
    }

    public static class MyEntity {

        private long id;

        private String name;

        private Date created;

        public MyEntity() {
        }

        public MyEntity(String string) {
            name = string;
        }

        public String getName() {
            return name;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

}
