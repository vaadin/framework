package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AttachDetachWindow {

    private Application testApp = new Application();

    private interface TestContainer {
        public boolean attachCalled();

        public boolean detachCalled();

        public TestContent getTestContent();

        public Application getApplication();
    }

    private class TestWindow extends Window implements TestContainer {
        boolean windowAttachCalled = false;
        boolean windowDetachCalled = false;
        private TestContent testContent = new TestContent();;

        TestWindow() {
            setContent(testContent);
        }

        @Override
        public void attach() {
            super.attach();
            windowAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            windowDetachCalled = true;
        }

        public boolean attachCalled() {
            return windowAttachCalled;
        }

        public boolean detachCalled() {
            return windowDetachCalled;
        }

        public TestContent getTestContent() {
            return testContent;
        }
    }

    private class TestContent extends VerticalLayout {
        boolean contentDetachCalled = false;
        boolean childDetachCalled = false;
        boolean contentAttachCalled = false;
        boolean childAttachCalled = false;

        private Label child = new Label() {
            @Override
            public void attach() {
                super.attach();
                childAttachCalled = true;
            }

            @Override
            public void detach() {
                super.detach();
                childDetachCalled = true;
            }
        };

        public TestContent() {
            addComponent(child);
        }

        @Override
        public void attach() {
            super.attach();
            contentAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            contentDetachCalled = true;
        }
    }

    private class TestRoot extends Root implements TestContainer {
        boolean rootAttachCalled = false;
        boolean rootDetachCalled = false;
        private TestContent testContent = new TestContent();;

        public TestRoot() {
            setContent(testContent);
        }

        @Override
        protected void init(WrappedRequest request) {
            // Do nothing
        }

        public boolean attachCalled() {
            return rootAttachCalled;
        }

        public boolean detachCalled() {
            return rootDetachCalled;
        }

        public TestContent getTestContent() {
            return testContent;
        }

        @Override
        public void attach() {
            super.attach();
            rootAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            rootDetachCalled = true;
        }
    }

    TestRoot main = new TestRoot();
    TestWindow sub = new TestWindow();

    @Test
    public void addSubWindowBeforeAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        main.addWindow(sub);
        assertUnattached(main);
        assertUnattached(sub);

        // attaching main should recurse to sub
        main.setApplication(testApp);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void addSubWindowAfterAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        main.setApplication(testApp);
        assertAttached(main);
        assertUnattached(sub);

        // main is already attached, so attach should be called for sub
        main.addWindow(sub);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void removeSubWindowBeforeDetachingMainWindow() {
        main.setApplication(testApp);
        main.addWindow(sub);

        // sub should be detached when removing from attached main
        main.removeWindow(sub);
        assertAttached(main);
        assertDetached(sub);

        // main detach should recurse to sub
        main.setApplication(null);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void removeSubWindowAfterDetachingMainWindow() {
        main.setApplication(testApp);
        main.addWindow(sub);

        // main detach should recurse to sub
        main.setApplication(null);
        assertDetached(main);
        assertDetached(sub);

        main.removeWindow(sub);
        assertDetached(main);
        assertDetached(sub);
    }

    /**
     * Asserts that win and its children are attached to testApp and their
     * attach() methods have been called.
     */
    private void assertAttached(TestContainer win) {
        TestContent testContent = win.getTestContent();
        
        assertTrue("window attach not called", win.attachCalled());
        assertTrue("window content attach not called",
                testContent.contentAttachCalled);
        assertTrue("window child attach not called",
                testContent.childAttachCalled);

        assertSame("window not attached", win.getApplication(), testApp);
        assertSame("window content not attached", testContent.getApplication(),
                testApp);
        assertSame("window children not attached",
                testContent.child.getApplication(), testApp);
    }

    /**
     * Asserts that win and its children are not attached.
     */
    private void assertUnattached(TestContainer win) {
        assertSame("window not detached", win.getApplication(), null);
        assertSame("window content not detached", win.getTestContent()
                .getApplication(), null);
        assertSame("window children not detached",
                win.getTestContent().child.getApplication(), null);
    }

    /**
     * Asserts that win and its children are unattached and their detach()
     * methods have been been called.
     * 
     * @param win
     */
    private void assertDetached(TestContainer win) {
        assertUnattached(win);
        assertTrue("window detach not called", win.detachCalled());
        assertTrue("window content detach not called",
                win.getTestContent().contentDetachCalled);
        assertTrue("window child detach not called",
                win.getTestContent().childDetachCalled);
    }
}
