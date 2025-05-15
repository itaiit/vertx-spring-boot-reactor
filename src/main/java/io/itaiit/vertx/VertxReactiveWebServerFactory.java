package io.itaiit.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class VertxReactiveWebServerFactory extends AbstractReactiveWebServerFactory {

    public VertxReactiveWebServerFactory() {
    }

    public VertxReactiveWebServerFactory(int port) {
        super(port);
    }

    @Override
    public WebServer getWebServer(HttpHandler httpHandler) {
        // Create and return a new instance of your Vert.x web server
        VertxHttpHandlerAdapter vertxHttpHandlerAdapter = new VertxHttpHandlerAdapter(httpHandler);
        HttpServer httpServer = createHttpServer(vertxHttpHandlerAdapter);
        VertxWebServer vertxWebServer = new VertxWebServer(httpServer, getListenAddress(), getShutdown());
        return vertxWebServer;
    }

    private HttpServer createHttpServer(VertxHttpHandlerAdapter vertxHttpHandlerAdapter) {
        VertxOptions options = new VertxOptions()
                .setWorkerPoolSize(20); // 设置 Worker 线程数为 20
        Vertx vertx = Vertx.vertx(options);
        Router router = Router.router(vertx);
        router.get("/").handler(vertxHttpHandlerAdapter);
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router);
        return httpServer;
    }

    private SocketAddress getListenAddress() {
        if (getAddress() != null) {
            return SocketAddress.inetSocketAddress(
                    new InetSocketAddress(getAddress().getHostAddress(), getPort())
            );
        }
        return SocketAddress.inetSocketAddress(
                new InetSocketAddress(getPort())
        );
    }
}
