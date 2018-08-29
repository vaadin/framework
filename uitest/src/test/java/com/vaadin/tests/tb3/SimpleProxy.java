package com.vaadin.tests.tb3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleProxy extends Thread {
    private final ThreadGroup proxyThreads;
    private final Queue<Socket> sockets = new ConcurrentLinkedQueue<>();
    private final ServerSocket serverSocket;
    private final String remoteHost;
    private final int remotePort;

    public SimpleProxy(int localPort, String remoteHost, int remotePort) throws IOException {
        super(new ThreadGroup("proxy " + localPort), "server");
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        proxyThreads = getThreadGroup();
        serverSocket = new ServerSocket(localPort, 100, InetAddress.getLoopbackAddress());

        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                try {
                    Socket proxySocket = serverSocket.accept();
                    sockets.add(proxySocket);
                    Socket remoteSocket = new Socket(remoteHost, remotePort);
                    sockets.add(remoteSocket);
                    new CopySocket(proxyThreads, proxySocket, remoteSocket).start();
                    new CopySocket(proxyThreads, remoteSocket, proxySocket).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            disconnect();
        }
    }

    public void disconnect() {
        proxyThreads.interrupt();
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    private class CopySocket extends Thread {

        private final InputStream inputStream;
        private final OutputStream outputStream;

        private CopySocket(ThreadGroup proxyThreads, Socket srcSocket, Socket dstSocket) throws IOException {
            super(proxyThreads, "proxy worker");
            setDaemon(true);
            inputStream = srcSocket.getInputStream();
            outputStream = dstSocket.getOutputStream();
        }

        @Override
        public void run() {
            try {
                for (int b; (b = inputStream.read()) >= 0; ) {
                    outputStream.write(b);
                }
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ignored) {

                }
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

}
