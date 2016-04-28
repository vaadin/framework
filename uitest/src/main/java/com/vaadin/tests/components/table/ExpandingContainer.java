package com.vaadin.tests.components.table;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ExpandingContainer extends AbstractContainer implements
        Container.Ordered, Container.Indexed, Container.ItemSetChangeNotifier {

    public static final List<String> PROPERTY_IDS = Arrays.asList("id",
            "column1", "column2");

    private final Label sizeLabel;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private int currentSize = 300;

    private boolean loggingEnabled;

    public ExpandingContainer(Label sizeLabel) {
        this.sizeLabel = sizeLabel;
        updateLabel();
    }

    private void log(String message) {
        if (loggingEnabled) {
            log.info(message);
        }
    }

    // Expand container if we scroll past 85%
    public int checkExpand(int index) {
        log("checkExpand(" + index + ")");
        if (index >= currentSize * 0.85) {
            final int oldsize = currentSize;
            currentSize = (int) (oldsize * 1.3333);
            log("*** getSizeWithHint(" + index + "): went past 85% of size="
                    + oldsize + ", new size=" + currentSize);
            updateLabel();
        }
        return currentSize;
    }

    @Override
    public void fireItemSetChange() {
        super.fireItemSetChange();
    }

    private void updateLabel() {
        sizeLabel.setValue("Container size: " + currentSize);
    }

    public void triggerItemSetChange() {
        log("*** triggerItemSetChange(): scheduling item set change event");
        final VaadinSession session = VaadinSession.getCurrent();
        new Thread() {
            @Override
            public void run() {
                ExpandingContainer.this.invoke(session, new Runnable() {
                    @Override
                    public void run() {
                        log("*** Firing item set change event");
                        ExpandingContainer.this.fireItemSetChange();
                    }
                });
            }
        }.start();
    }

    private void invoke(VaadinSession session, Runnable action) {
        session.lock();
        VaadinSession previousSession = VaadinSession.getCurrent();
        VaadinSession.setCurrent(session);
        try {
            action.run();
        } finally {
            session.unlock();
            VaadinSession.setCurrent(previousSession);
        }
    }

    // Container

    @Override
    public BeanItem<MyBean> getItem(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return null;
        }
        final int index = ((Integer) itemId).intValue();
        return new BeanItem<MyBean>(new MyBean(index));
    }

    @Override
    public Collection<Integer> getItemIds() {
        return new IntList(size());
    }

    @Override
    public List<String> getContainerPropertyIds() {
        return PROPERTY_IDS;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Property/* <?> */getContainerProperty(Object itemId,
            Object propertyId) {
        BeanItem<MyBean> item = getItem(itemId);
        return item != null ? item.getItemProperty(propertyId) : null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return Component.class;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public boolean containsId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return false;
        }
        int index = ((Integer) itemId).intValue();
        checkExpand(index);
        return index >= 0 && index < currentSize;
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Item addItem(Object itemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Item addItem() {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public boolean removeItem(Object itemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public boolean removeContainerProperty(Object propertyId) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public boolean removeAllItems() {
        throw new UnsupportedOperationException();
    }

    // Container.Indexed

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Object addItemAt(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Item addItemAt(int index, Object newItemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getIdByIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index < " + index);
        }
        final int size = currentSize;
        if (index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " but size="
                    + size);
        }
        checkExpand(index);
        return index;
    }

    @Override
    public List<Integer> getItemIds(int startIndex, int numberOfItems) {
        if (numberOfItems < 0) {
            throw new IllegalArgumentException("numberOfItems < 0");
        }
        final int size = currentSize;
        checkExpand(startIndex);
        if (startIndex < 0 || startIndex > size) {
            throw new IndexOutOfBoundsException("startIndex=" + startIndex
                    + " but size=" + size);
        }
        if (startIndex + numberOfItems > size) {
            numberOfItems = size - startIndex;
        }
        return new IntList(startIndex, numberOfItems);
    }

    @Override
    public int indexOfId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return -1;
        }
        final int index = ((Integer) itemId).intValue();
        checkExpand(index);
        if (index < 0 || index >= currentSize) {
            return -1;
        }
        return index;
    }

    // Container.Ordered

    @Override
    public Integer nextItemId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return null;
        }
        int index = ((Integer) itemId).intValue();
        checkExpand(index);
        if (index < 0 || index + 1 >= currentSize) {
            return null;
        }
        return index + 1;
    }

    @Override
    public Integer prevItemId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return null;
        }
        int index = ((Integer) itemId).intValue();
        checkExpand(index);
        if (index - 1 < 0 || index >= currentSize) {
            return null;
        }
        return index - 1;
    }

    @Override
    public Integer firstItemId() {
        return currentSize == 0 ? null : 0;
    }

    @Override
    public Integer lastItemId() {
        final int size = currentSize;
        return size == 0 ? null : size - 1;
    }

    @Override
    public boolean isFirstId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return false;
        }
        final int index = ((Integer) itemId).intValue();
        checkExpand(index);
        final int size = currentSize;
        return size > 0 && index == 0;
    }

    @Override
    public boolean isLastId(Object itemId) {
        if (!(itemId instanceof Integer)) {
            return false;
        }
        int index = ((Integer) itemId).intValue();
        checkExpand(index);
        int size = currentSize;
        return size > 0 && index == size - 1;
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Item addItemAfter(Object previousItemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) {
        throw new UnsupportedOperationException();
    }

    // Container.ItemSetChangeNotifier

    @Override
    @SuppressWarnings("deprecation")
    public void addListener(Container.ItemSetChangeListener listener) {
        super.addListener(listener);
    }

    @Override
    public void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        super.addItemSetChangeListener(listener);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeListener(Container.ItemSetChangeListener listener) {
        super.removeListener(listener);
    }

    @Override
    public void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        super.removeItemSetChangeListener(listener);
    }

    // IntList

    private static class IntList extends AbstractList<Integer> {

        private final int min;
        private final int size;

        public IntList(int size) {
            this(0, size);
        }

        public IntList(int min, int size) {
            if (size < 0) {
                throw new IllegalArgumentException("size < 0");
            }
            this.min = min;
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Integer get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException();
            }
            return min + index;
        }
    }

    // MyBean
    public class MyBean {

        private final int index;

        public MyBean(int index) {
            this.index = index;
        }

        public String getId() {
            return "ROW #" + index;
        }

        public String getColumn1() {
            return genText();
        }

        public String getColumn2() {
            return genText();
        }

        private String genText() {
            return "this is a line of text in row #" + index;
        }
    }

    public void logDetails(boolean enabled) {
        loggingEnabled = enabled;
    }
}
