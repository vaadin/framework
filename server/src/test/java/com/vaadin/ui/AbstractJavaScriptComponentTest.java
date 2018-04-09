package com.vaadin.ui;

import org.junit.Test;

import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.util.MockUI;

public class AbstractJavaScriptComponentTest {
    public static class TestJsComponentState extends JavaScriptComponentState {
        public String ownField = "foo";
    }

    public static class TestJsComponent extends AbstractJavaScriptComponent {
        @Override
        protected TestJsComponentState getState() {
            return (TestJsComponentState) super.getState();
        }
    }

    @Test
    public void testComponentStateEncoding() {
        MockUI ui = new MockUI();
        TestJsComponent component = new TestJsComponent();
        ui.setContent(component);

        ComponentTest.assertEncodedStateProperties(component,
                "Only defaults not known by the client should be sent",
                "ownField");

        component.setCaption("My caption");
        ComponentTest.assertEncodedStateProperties(component,
                "Caption should be the only changed state property", "caption");
    }
}
