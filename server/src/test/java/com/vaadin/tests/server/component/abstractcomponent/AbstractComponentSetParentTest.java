package com.vaadin.tests.server.component.abstractcomponent;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HasComponents;

public class AbstractComponentSetParentTest {

    private static class TestComponent extends AbstractComponent {
    }

    @Test
    public void setParent_marks_old_parent_as_dirty() {
        HasComponents hasComponents = Mockito.mock(HasComponents.class);
        TestComponent testComponent = new TestComponent();
        testComponent.setParent(hasComponents);
        testComponent.setParent(null);
        Mockito.verify(hasComponents, Mockito.times(1)).markAsDirty();
    }
}
