package com.vaadin.data.util;

import com.vaadin.data.Item;

/**
 * An in-memory container for JavaBeans.
 * 
 * <p>
 * The properties of the container are determined automatically by introspecting
 * the used JavaBean class. Only beans of the same type can be added to the
 * container.
 * </p>
 * 
 * <p>
 * In BeanContainer (unlike {@link BeanItemContainer}), the item IDs do not have
 * to be the beans themselves.
 * </p>
 * 
 * <p>
 * It is not possible to add additional properties to the container and nested
 * bean properties are not supported.
 * </p>
 * 
 * @param <IDTYPE>
 *            The type of the item identifier
 * @param <BT>
 *            The type of the Bean
 * 
 * @see AbstractBeanContainer
 * @see BeanItemContainer
 * 
 * @since 6.5
 */
public class BeanContainer<IDTYPE, BT> extends
        AbstractBeanContainer<IDTYPE, BT> {

    public BeanContainer(Class<? extends BT> type) {
        super(type);
    }

    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the bean to the Container.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    @Override
    public BeanItem<BT> addItem(IDTYPE itemId, BT bean) {
        return super.addItem(itemId, bean);
    }

    /**
     * Adds the bean after the given bean.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    @Override
    public BeanItem<BT> addItemAfter(IDTYPE previousItemId, IDTYPE newItemId,
            BT bean) {
        return super.addItemAfter(previousItemId, newItemId, bean);
    }

    /**
     * Adds a new bean at the given index.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param index
     *            Index at which the bean should be added.
     * @param newItemId
     *            The item id for the bean to add to the container.
     * @param bean
     *            The bean to add to the container.
     * 
     * @return Returns the new BeanItem or null if the operation fails.
     */
    @Override
    public BeanItem<BT> addItemAt(int index, IDTYPE newItemId, BT bean) {
        return super.addItemAt(index, newItemId, bean);
    }

}
