package com.zpf.avc.proxy;

import com.zpf.avc.util.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

//public class ProxyServer {
//    private final ServerSocket serverSocket;
//    private final int port;
//
//    public ProxyServer() {
//            try {
//                InetAddress inetAddress = InetAddress.getByName(Constants.LOCAL_HOST);
//                this.serverSocket = new ServerSocket(0, 8, inetAddress);
//                this.port = serverSocket.getLocalPort();
//
//                IgnoreHostProxySelector.install(PROXY_HOST, port);
//                CountDownLatch startSignal = new CountDownLatch(1);
//                this.waitConnectionThread = new Thread(new WaitRequestsRunnable(startSignal));
//                this.waitConnectionThread.start();
//                startSignal.await(); // freeze thread, wait for server starts
//                this.pinger = new Pinger(PROXY_HOST, port);
//                LOG.info("Proxy cache server started. Is it alive? " + isAlive());
//            } catch (IOException | InterruptedException e) {
//                socketProcessor.shutdown();
//                throw new IllegalStateException("Error starting local proxy server", e);
//            }
//    }
//
//    private void waitForRequest() {
//        try {
//            while (!Thread.currentThread().isInterrupted()) {
//                Socket socket = serverSocket.accept();
//                LOG.debug("Accept new socket " + socket);
//                socketProcessor.submit(new SocketProcessorRunnable(socket));
//            }
//        } catch (IOException e) {
//            onError(new ProxyCacheException("Error during waiting connection", e));
//        }
//    }
//}
