package com.vaadin.server;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.FieldRpc.BlurServerRpc;
import com.vaadin.shared.ui.ClickRpc;

/**
 * We test that AbstractClientConnector has a suitable isThis method which is
 * needed to correctly perform an equals check between a proxy and it's
 * underlying instance.
 *
 * @author Vaadin Ltd
 */
public class AbstractClientConnectorTest {

    @Test
    public void registerRPCMultiInterfaceTest() {
        AbstractClientConnector mock = mock(AbstractClientConnector.class);
        MultiServerRpcMock implementation = new MultiServerRpcMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        try {
            mock.registerRpc(implementation);
            Assert.fail("expected exception");
        } catch (Exception expected) {
            Assert.assertEquals(expected.getMessage(),
                    "Use registerRpc(T implementation, Class<T> rpcInterfaceType) if the Rpc implementation implements more than one interface");
        }
    }

    @Test
    public void registerRPCInterfaceTest() {
        AbstractClientConnector mock = mock(AbstractClientConnector.class);
        ServerRpcMock implementation = new ServerRpcMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        mock.registerRpc(implementation);
        verify(mock, times(1)).registerRpc(implementation, ClickRpc.class);
    }

    @Test
    public void registerRPCInterfaceLastTest() {
        AbstractClientConnector mock = mock(AbstractClientConnector.class);
        ServerRpcLastMock implementation = new ServerRpcLastMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        mock.registerRpc(implementation);
        verify(mock, times(1)).registerRpc(implementation, ClickRpc.class);
    }

    private class ServerRpcLastMock
            implements Comparable<ServerRpcLastMock>, ClickRpc {
        private static final long serialVersionUID = -2822356895755286180L;

        @Override
        public void click(MouseEventDetails mouseDetails) {
        }

        @Override
        public int compareTo(ServerRpcLastMock o) {
            return 0;
        }

    }

    private class ServerRpcMock implements ClickRpc {
        private static final long serialVersionUID = 2822356895755286180L;

        @Override
        public void click(MouseEventDetails mouseDetails) {
        }

    }

    private class MultiServerRpcMock implements ClickRpc, BlurServerRpc {

        private static final long serialVersionUID = -7611999715560330373L;

        @Override
        public void blur() {

        }

        @Override
        public void click(MouseEventDetails mouseDetails) {

        }

    }

}
