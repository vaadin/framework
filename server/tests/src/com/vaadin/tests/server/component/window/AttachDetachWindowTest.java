package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AttachDetachWindowTest {

    private VaadinSession testApp = new AlwaysLockedVaadinSession(null);

    private interface TestContainer {
        public boolean attachCalled();

        public boolean detachCalled();

        public TestContent getTestContent();

        public VaadinSession getSession();
    }

    private class TestWindow extends Window implements TestContainer {
        boolean windowAttachCalled = false;
        boolean windowDetachCalled = false;
        private TestContent testContent = new TestContent();

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

        @Override
        public boolean attachCalled() {
            return windowAttachCalled;
        }

        @Override
        public boolean detachCalled() {
            return windowDetachCalled;
        }

        @Override
        public TestContent getTestContent() {
            return testContent;
        }

        @Override
        public VaadinSession getSession() {
            return super.getSession();
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

    private class TestUI extends UI implements TestContainer {
        boolean rootAttachCalled = false;
        boolean rootDetachCalled = false;
        private TestContent testContent = new TestContent();

        public TestUI() {
            setContent(testContent);
        }

        @Override
        protected void init(VaadinRequest request) {
            // Do nothing
        }

        @Override
        public boolean attachCalled() {
            return rootAttachCalled;
        }

        @Override
        public boolean detachCalled() {
            return rootDetachCalled;
        }

        @Override
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

    TestUI main = new TestUI();
    TestWindow sub = new TestWindow();

    @Test
    public void addSubWindowBeforeAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        main.addWindow(sub);
        assertUnattached(main);
        assertUnattached(sub);

        // attaching main should recurse to sub
        main.setSession(testApp);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void addSubWindowAfterAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);

        main.setSession(testApp);
        assertAttached(main);
        assertUnattached(sub);

        // main is already attached, so attach should be called for sub
        main.addWindow(sub);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void removeSubWindowBeforeDetachingMainWindow() {
        main.setSession(testApp);
        main.addWindow(sub);

        // sub should be detached when removing from attached main
        main.removeWindow(sub);
        assertAttached(main);
        assertDetached(sub);

        // main detach should recurse to sub
        main.setSession(null);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void removeSubWindowAfterDetachingMainWindow() {
        main.setSession(testApp);
        main.addWindow(sub);

        // main detach should recurse to sub
        main.setSession(null);
        assertDetached(main);
        assertDetached(sub);

        main.removeWindow(sub);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void addWindow_attachEventIsFired() {
        TestUI ui = new TestUI();
        final Window window = new Window();

        final boolean[] eventFired = new boolean[1];
        ui.addComponentAttachListener(new ComponentAttachListener() {

            @Override
            public void componentAttachedToContainer(ComponentAttachEvent event) {
                eventFired[0] = event.getAttachedComponent().equals(window);
            }
        });
        ui.addWindow(window);
        Assert.assertTrue("Attach event is not fired for added window",
                eventFired[0]);
    }

    @Test
    public void removeWindow_detachEventIsFired() {
        TestUI ui = new TestUI();
        final Window window = new Window();

        final boolean[] eventFired = new boolean[1];
        ui.addComponentDetachListener(new ComponentDetachListener() {

            @Override
            public void componentDetachedFromContainer(
                    ComponentDetachEvent event) {
                eventFired[0] = event.getDetachedComponent().equals(window);
            }
        });
        ui.addWindow(window);
        ui.removeWindow(window);

        Assert.assertTrue("Detach event is not fired for removed window",
                eventFired[0]);
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

        assertSame("window not attached", win.getSession(), testApp);
        assertSame("window content not attached", testContent.getUI()
                .getSession(), testApp);
        assertSame("window children not attached", testContent.child.getUI()
                .getSession(), testApp);
    }

    /**
     * Asserts that win and its children are not attached.
     */
    private void assertUnattached(TestContainer win) {
        assertSame("window not detached", win.getSession(), null);
        assertSame("window content not detached",
                getSession(win.getTestContent()), null);
        assertSame("window children not detached",
                getSession(win.getTestContent().child), null);
    }

    private VaadinSession getSession(ClientConnector testContainer) {
        UI ui = testContainer.getUI();
        if (ui != null) {
            return ui.getSession();
        } else {
            return null;
        }
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
