package io.itaiit.vertx;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;

public class VertxServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {

    private final HttpServerResponse response;
    private final DataBufferFactory dataBufferFactory;

    public VertxServerHttpResponse(HttpServerResponse response, DataBufferFactory dataBufferFactory) {
        super(dataBufferFactory);
        this.response = response;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public <T> T getNativeResponse() {
        return (T) response;
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
        return null;
    }


    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return null;
    }

    @Override
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.response.setStatusCode(status);
        }
    }

    @Override
    protected void applyHeaders() {

    }

    @Override
    protected void applyCookies() {
        for (List<ResponseCookie> cookies : getCookies().values()) {
            for (ResponseCookie cookie : cookies) {
                this.response.putHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
        }
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() ->
                Mono.just(this.response.sendFile(file.toString(), position, count).result())
        );
    }
}
