package com.vaadin.tests.server.component.abstractcomponentcontainer;

import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

/**
 * Tests for avoiding add parent as child for
 * {@link AbstractComponentContainer#addComponent(Component)}
 *
 * @author Vaadin Ltd
 */
public class AddParentAsChildTest {

    @Test(expected = IllegalArgumentException.class)
    public void testAddComponent() {
        AbstractComponentContainer container = new ComponentContainer();
        HasComponents hasComponentsMock = EasyMock
                .createMock(HasComponents.class);
        container.setParent(hasComponentsMock);

        container.addComponent(hasComponentsMock);
    }

    class ComponentContainer extends AbstractComponentContainer {

        @Override
        public void replaceComponent(Component oldComponent,
                Component newComponent) {
        }

        @Override
        public int getComponentCount() {
            return 0;
        }

        @Override
        public Iterator<Component> iterator() {
            return null;
        }

    }

}
