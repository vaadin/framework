package com.vaadin.ui;

import static com.vaadin.ui.ComponentTest.isDirty;
import static com.vaadin.ui.ComponentTest.syncToClient;
import static com.vaadin.ui.ComponentTest.updateDiffState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.shared.ui.richtextarea.RichTextAreaServerRpc;
import com.vaadin.tests.util.MockUI;

public class RichTextAreaTest {

    @Test
    public void initiallyEmpty() {
        RichTextArea tf = new RichTextArea();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void setValueServerWhenReadOnly() {
        RichTextArea tf = new RichTextArea();
        tf.setReadOnly(true);
        tf.setValue("foo");
        assertEquals("foo", tf.getValue());
    }

    @Test
    public void diffStateAfterClientSetValueWhenReadOnly() {
        UI ui = new MockUI();

        // If the client has a non-readonly text field which is set to read-only
        // on the server, then any update from the client must cause both the
        // readonly state and the old value to be sent
        RichTextArea rta = new RichTextArea();
        ui.setContent(rta);
        rta.setValue("bar");
        rta.setReadOnly(true);
        syncToClient(rta);

        // Client thinks the field says "foo" but it won't be updated because
        // the field is readonly
        ServerRpcManager.getRpcProxy(rta, RichTextAreaServerRpc.class)
                .setText("foo");

        // The real value will be sent back as long as the field is marked as
        // dirty and diffstate contains what the client has
        assertEquals("foo", getDiffStateString(rta, "value"));
        assertTrue("Component should be marked dirty", isDirty(rta));
    }

    @Test
    public void setValueClientNotSentBack() throws RpcInvocationException {
        UI ui = new MockUI();
        RichTextArea rta = new RichTextArea();
        ui.setContent(rta);
        rta.setValue("bar");

        updateDiffState(rta);
        ServerRpcManager.getRpcProxy(rta, RichTextAreaServerRpc.class)
                .setText("foo");
        assertEquals("foo", getDiffStateString(rta, "value"));
    }

    private String getDiffStateString(ClientConnector connector, String key) {
        return connector.getUI().getConnectorTracker().getDiffState(connector)
                .get(key).asString();
    }

    @Test
    public void setValueClientRefusedWhenReadOnly() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("bar");
        tf.setReadOnly(true);
        tf.setValue("foo", true);
        assertEquals("bar", tf.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        RichTextArea tf = new RichTextArea();
        tf.setValue(null);
    }

    @Test
    public void emptyAfterClear() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("foobar");
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

}
