package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Objects;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxPageLengthZeroRefreshItemAfterDataProviderUpdate
        extends AbstractTestUI {

    private static final class Bean {
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Bean bean = (Bean) o;
            return Objects.equals(name, bean.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        private String name;

        public Bean(String name) {
            setName(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private ArrayList<Bean> list = new ArrayList<>();
    private ArrayList<Bean> list2 = new ArrayList<>();

    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull();
        VerticalLayout leftLayout = new VerticalLayout();
        VerticalLayout rightLayout = new VerticalLayout();
        root.addComponent(leftLayout);
        root.addComponent(rightLayout);
        addComponent(root);
        final ComboBox<Bean> comboBoxPageLengthZero = new ComboBox<>();
        comboBoxPageLengthZero.setId("combo-0");
        comboBoxPageLengthZero.setPageLength(0);
        final ComboBox<Bean> comboBoxRegular = new ComboBox<>();
        comboBoxRegular.setId("combo-n");
        comboBoxPageLengthZero.setItemCaptionGenerator(bean -> bean.getName());
        comboBoxRegular.setItemCaptionGenerator(bean -> bean.getName());
        ListDataProvider<Bean> dataProvider = new ListDataProvider<>(list);
        ListDataProvider<Bean> dataProvider2 = new ListDataProvider<>(list2);
        comboBoxPageLengthZero.setDataProvider(dataProvider);
        comboBoxRegular.setDataProvider(dataProvider2);
        createComponents(leftLayout, comboBoxPageLengthZero, dataProvider,
                true);
        createComponents(rightLayout, comboBoxRegular, dataProvider2, false);
    }

    private void createComponents(VerticalLayout layout,
            ComboBox<Bean> comboBox, ListDataProvider<Bean> dataProvider,
            boolean pageLengthZero) {
        Button updateValues = new Button(
                "1. Refresh backing list and dataprovider, select first item",
                e -> {
                    Bean selected = null;
                    if (pageLengthZero) {
                        list.clear();
                        list.add(new Bean("foo" + (++counter)));
                        list.add(new Bean("foo" + (++counter)));
                        selected = list.get(0);
                    } else {
                        list2.clear();
                        list2.add(new Bean("bar" + (++counter)));
                        list2.add(new Bean("bar" + (++counter)));
                        selected = list2.get(0);
                    }
                    dataProvider.refreshAll();
                    comboBox.setValue(selected);
                });

        updateValues.setId(pageLengthZero ? "update-0" : "update-n");

        Button refresh = new Button(
                "2. Update item returned by combobox's getValue and call refreshItem on data provider",
                e -> {
                    Bean currentValue = comboBox.getValue();
                    currentValue.setName(currentValue.getName() + "(updated)");
                    dataProvider.refreshItem(currentValue);
                });
        refresh.setId(pageLengthZero ? "refresh-0" : "refresh-n");

        layout.addComponents(updateValues, refresh, comboBox);
    }

}
