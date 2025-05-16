package io.itaiit.vertx;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;

import java.net.URISyntaxException;

public class VertxHttpHandlerAdapter implements Handler<RoutingContext> {
    Logger logger = LoggerFactory.getLogger(VertxHttpHandlerAdapter.class);

    private final HttpHandler httpHandler;

    public VertxHttpHandlerAdapter(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public void handle(RoutingContext event) {
        DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        VertxHttpServerRequest vertxHttpServerRequest = null;
        try {
            vertxHttpServerRequest = new VertxHttpServerRequest(event.request(), dataBufferFactory);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        VertxServerHttpResponse vertxServerHttpResponse = new VertxServerHttpResponse(event.response(), dataBufferFactory);
        this.httpHandler.handle(vertxHttpServerRequest, vertxServerHttpResponse)
                .doOnError(e -> {
                    logger.error("Vertx HTTP handler error", e);
                    event.fail(e);
                })
                .doOnSuccess(aVoid -> {
                    logger.info("Vertx HTTP handler success");
                    if (!event.response().ended()) {
                        event.response().end();
                    }
                })
                .subscribe();
    }
}
