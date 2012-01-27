package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.junit.Test;

public class AttachDetachWindow {

    private Application testApp = new Application() {
        @Override
        public void init() {
        }
    };

    private class TestWindow extends Window {
        boolean windowAttachCalled = false;
        boolean contentAttachCalled = false;
        boolean childAttachCalled = false;
        boolean windowDetachCalled = false;
        boolean contentDetachCalled = false;
        boolean childDetachCalled = false;

        TestWindow() {
            setContent(new VerticalLayout() {
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
            });
            addComponent(new Label() {
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
            });
        }

        Component getChild() {
            return getComponentIterator().next();
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
    }

    TestWindow main = new TestWindow();
    TestWindow sub = new TestWindow();

    @Test
    public void addSubWindowBeforeAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        main.addWindow(sub);
        assertUnattached(main);
        assertUnattached(sub);

        // attaching main should recurse to sub
        testApp.setMainWindow(main);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void addSubWindowAfterAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        testApp.setMainWindow(main);
        assertAttached(main);
        assertUnattached(sub);

        // main is already attached, so attach should be called for sub
        main.addWindow(sub);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void removeSubWindowBeforeDetachingMainWindow() {
        testApp.addWindow(main);
        main.addWindow(sub);

        // sub should be detached when removing from attached main
        main.removeWindow(sub);
        assertAttached(main);
        assertDetached(sub);

        // main detach should recurse to sub
        testApp.removeWindow(main);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void removeSubWindowAfterDetachingMainWindow() {
        testApp.addWindow(main);
        main.addWindow(sub);

        // main detach should recurse to sub
        testApp.removeWindow(main);
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
    private void assertAttached(TestWindow win) {
        assertTrue("window attach not called", win.windowAttachCalled);
        assertTrue("window content attach not called", win.contentAttachCalled);
        assertTrue("window child attach not called", win.childAttachCalled);

        assertSame("window not attached", win.getApplication(), testApp);
        assertSame("window content not attached", win.getContent()
                .getApplication(), testApp);
        assertSame("window children not attached", win.getChild()
                .getApplication(), testApp);
    }

    /**
     * Asserts that win and its children are not attached.
     */
    private void assertUnattached(TestWindow win) {
        assertSame("window not detached", win.getApplication(), null);
        assertSame("window content not detached", win.getContent()
                .getApplication(), null);
        assertSame("window children not detached", win.getChild()
                .getApplication(), null);
    }

    /**
     * Asserts that win and its children are unattached and their detach()
     * methods have been been called.
     * 
     * @param win
     */
    private void assertDetached(TestWindow win) {
        assertUnattached(win);
        assertTrue("window detach not called", win.windowDetachCalled);
        assertTrue("window content detach not called", win.contentDetachCalled);
        assertTrue("window child detach not called", win.childDetachCalled);
    }
}
