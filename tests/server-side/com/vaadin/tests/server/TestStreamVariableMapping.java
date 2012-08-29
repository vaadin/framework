package com.vaadin.tests.server;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.Application;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.WrappedRequest;
import com.vaadin.terminal.gwt.server.CommunicationManager;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

public class TestStreamVariableMapping extends TestCase {
    private static final String variableName = "myName";

    private Upload owner;
    private StreamVariable streamVariable;

    private CommunicationManager cm;

    @Override
    protected void setUp() throws Exception {
        final Application application = new Application();
        final UI uI = new UI() {
            @Override
            protected void init(WrappedRequest request) {
                // TODO Auto-generated method stub

            }

            @Override
            public Application getApplication() {
                return application;
            }
        };
        owner = new Upload() {
            @Override
            public UI getUI() {
                return uI;
            }
        };
        streamVariable = EasyMock.createMock(StreamVariable.class);
        cm = createCommunicationManager();

        super.setUp();
    }

    public void testAddStreamVariable() {
        String targetUrl = cm.getStreamVariableTargetUrl(owner, variableName,
                streamVariable);
        assertTrue(targetUrl.startsWith("app://APP/UPLOAD/-1/1/myName/"));

        StreamVariable streamVariable2 = cm.getStreamVariable(
                owner.getConnectorId(), variableName);
        assertSame(streamVariable, streamVariable2);
    }

    public void testRemoverVariable() {
        cm.getStreamVariableTargetUrl(owner, variableName, streamVariable);
        assertNotNull(cm
                .getStreamVariable(owner.getConnectorId(), variableName));

        cm.cleanStreamVariable(owner, variableName);
        assertNull(cm.getStreamVariable(owner.getConnectorId(), variableName));
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
