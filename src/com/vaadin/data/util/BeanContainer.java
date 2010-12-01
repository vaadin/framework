package com.vaadin.data.util;

import java.util.Collection;

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
 * <p>
 * If a bean id resolver is set, the {@link #addBean(Object)},
 * {@link #addBeanAfter(Object, Object)}, {@link #addBeanAt(int, Object)} and
 * {@link #addAll(java.util.Collection)} methods can be used to add items to the
 * container. If one of these methods is called, the resolver is used to
 * generate an identifier for the item (must not return null). Explicit item
 * identifiers can be used also when a resolver has been set.
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

    public BeanContainer(Class<? super BT> type) {
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
        if (itemId != null && bean != null) {
            return super.addItem(itemId, bean);
        } else {
            return null;
        }
    }

    /**
     * Adds the bean after the given bean.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    @Override
    public BeanItem<BT> addItemAfter(IDTYPE previousItemId, IDTYPE newItemId,
            BT bean) {
        if (newItemId != null && bean != null) {
            return super.addItemAfter(previousItemId, newItemId, bean);
        } else {
            return null;
        }
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
        if (newItemId != null && bean != null) {
            return super.addItemAt(index, newItemId, bean);
        } else {
            return null;
        }
    }

    // automatic item id resolution

    @Override
    public void setIdResolver(
            com.vaadin.data.util.AbstractBeanContainer.BeanIdResolver<IDTYPE, BT> beanIdResolver) {
        super.setIdResolver(beanIdResolver);
    }

    @Override
    public BeanItem<BT> addBean(BT bean) throws IllegalStateException,
            IllegalArgumentException {
        return super.addBean(bean);
    }

    @Override
    public BeanItem<BT> addBeanAfter(IDTYPE previousItemId, BT bean)
            throws IllegalStateException, IllegalArgumentException {
        return super.addBeanAfter(previousItemId, bean);
    }

    @Override
    public BeanItem<BT> addBeanAt(int index, BT bean)
            throws IllegalStateException, IllegalArgumentException {
        return super.addBeanAt(index, bean);
    }

    @Override
    public void addAll(Collection<? extends BT> collection)
            throws IllegalStateException {
        super.addAll(collection);
    }

}
