package io.itaiit.vertx;

import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.HttpHeaders;
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
        super(dataBufferFactory, new HttpHeaders(new VertxHeadersAdapter(response.headers())));
        this.response = response;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public <T> T getNativeResponse() {
        return (T) response;
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
        return toBuffer(body).map(response::send).then();
    }

    private Mono<Buffer> toBuffer(Publisher<? extends DataBuffer> dataBuffers) {
        return Mono.from(dataBuffers).map(this::toByteBuf);
    }

    private Buffer toByteBuf(DataBuffer buffer) {
        if (buffer instanceof NettyDataBuffer) {
            return Buffer.buffer(((NettyDataBuffer) buffer).getNativeBuffer());
        } else {
            return Buffer.buffer(Unpooled.wrappedBuffer(buffer.asByteBuffer()));
        }
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        System.out.println("VertxServerHttpResponse.writeAndFlushWithInternal");
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
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return (status != null ? status : this.response.getStatusCode());
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
