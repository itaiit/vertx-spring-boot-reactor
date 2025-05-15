package io.itaiit.vertx;

import io.vertx.core.http.HttpServer;
import io.vertx.core.net.SocketAddress;
import org.springframework.boot.web.server.*;

public class VertxWebServer implements WebServer {

    private final HttpServer httpServer;
    private final Boolean gracefulShutdown;
    private final SocketAddress listenAddress;

    public VertxWebServer(HttpServer httpServer, SocketAddress listenAddress, Shutdown shutdown) {
        this.httpServer = httpServer;
        this.gracefulShutdown = shutdown == Shutdown.GRACEFUL;
        this.listenAddress = listenAddress;
    }

    @Override
    public void start() throws WebServerException {
        httpServer.listen(listenAddress);
        System.out.println("VertxWebServer started on " + listenAddress.host() + ":" + listenAddress.port());
    }

    @Override
    public void stop() throws WebServerException {
        httpServer.close(event -> {

        });
    }

    @Override
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (!gracefulShutdown) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
            return;
        }
        httpServer.close(event -> {
            if (event.succeeded()) {
                callback.shutdownComplete(GracefulShutdownResult.IDLE);
            } else {
                // Handle the error
            }
        });

    }

    @Override
    public int getPort() {
        if (this.httpServer != null) {
            try {
                return httpServer.actualPort();
            } catch (UnsupportedOperationException ex) {
                return -1;
            }
        }
        return -1;
    }


}
