package io.itaiit.vertx;

import io.vertx.core.http.HttpServer;
import org.springframework.boot.web.server.*;

public class VertxWebServer implements WebServer {

    private final HttpServer httpServer;
    private final Boolean gracefulShutdown;

    public VertxWebServer(HttpServer httpServer, Shutdown shutdown) {
        this.httpServer = httpServer;
        this.gracefulShutdown = shutdown == Shutdown.GRACEFUL;
    }

    @Override
    public void start() throws WebServerException {
        httpServer.listen(8085);
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
