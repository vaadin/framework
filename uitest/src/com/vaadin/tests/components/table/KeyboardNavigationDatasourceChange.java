package com.vaadin.tests.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class KeyboardNavigationDatasourceChange extends TestBase {

    @Override
    protected void setup() {
        List<MyItem> items = new ArrayList<MyItem>();
        for (int i = 0; i < 110; i++) {
            items.add(new MyItem("item" + i, i, null));
        }
        BeanItemContainer<MyItem> c = new BeanItemContainer<MyItem>(
                MyItem.class, items);
        Table t = new Table("Test", c);
        t.setVisibleColumns(new String[] { "nome", "index", "parent" });
        t.setSelectable(true);
        t.setSizeFull();
        t.setImmediate(true);
        TextField f = new TextField("Name");
        final Form form = new Form();
        // Property p = new ObjectProperty<String>("", String.class);
        // t.setPropertyDataSource(p); // UNCOMMENT THIS LINE TO SEE BUG
        // HAPPENING
        // f.setPropertyDataSource(p);
        // f.setImmediate(true);
        t.setPropertyDataSource(f);
        form.addField("table", t);
        form.addField("name", f);
        addComponent(form);

    }

    @Override
    protected String getDescription() {
        return "When calling setPropertyDataSource on a regular table the keyboard navigation becomes unstable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7446;
    }

    public class MyItem implements Serializable {

        private String nome;
        private Integer index;
        protected List<MyItem> children = new ArrayList<MyItem>();
        private MyItem parent;

        public MyItem(String nome, Integer index, List<MyItem> children) {
            this.nome = nome;
            this.index = index;
            if (children != null) {
                this.children = children;
                if (children != null) {
                    for (MyItem child : children) {
                        child.setParent(this);
                    }
                }
            }
        }

        /**
         * @return the nome
         */
        public String getNome() {
            return nome;
        }

        /**
         * @param nome
         *            the nome to set
         */
        public void setNome(String nome) {
            this.nome = nome;
        }

        /**
         * @return the idade
         */
        public Integer getIndex() {
            return index;
        }

        /**
         * @param idade
         *            the idade to set
         */
        public void setIndex(Integer idade) {
            index = idade;
        }

        /**
         * @return the children
         */
        public List<MyItem> getChildren() {
            return children;
        }

        /**
         * @param children
         *            the children to set
         */
        public void setChildren(List<MyItem> children) {
            this.children = children;
        }

        /**
         * @return the parent
         */
        public MyItem getParent() {
            return parent;
        }

        /**
         * @param parent
         *            the parent to set
         */
        public void setParent(MyItem parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
}
