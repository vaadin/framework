package com.vaadin.tests.server;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.Application;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.gwt.server.CommunicationManager;
import com.vaadin.ui.Upload;

public class TestStreamVariableMapping extends TestCase {
    private static final String variableName = "myName";

    private Upload owner;
    private StreamVariable streamVariable;

    private CommunicationManager cm;

    @Override
    protected void setUp() throws Exception {
        owner = new Upload();
        streamVariable = EasyMock.createMock(StreamVariable.class);
        cm = createCommunicationManager();

        super.setUp();
    }

    public void testAddStreamVariable() {
        String targetUrl = cm.getStreamVariableTargetUrl(owner, variableName,
                streamVariable);
        assertTrue(targetUrl.startsWith("app://APP/UPLOAD/PID0/myName/"));

        StreamVariable streamVariable2 = cm.getStreamVariable(
                cm.getPaintableId(owner), variableName);
        assertSame(streamVariable, streamVariable2);
    }

    public void testRemoverVariable() {
        cm.getStreamVariableTargetUrl(owner, variableName, streamVariable);
        assertNotNull(cm.getStreamVariable(cm.getPaintableId(owner),
                variableName));

        cm.cleanStreamVariable(owner, variableName);
        assertNull(cm.getStreamVariable(cm.getPaintableId(owner), variableName));
    }

    private CommunicationManager createCommunicationManager() {
        return new CommunicationManager(new Application() {
            @Override
            public void init() {
                // TODO Auto-generated method stub
            }
        });
    }

}
