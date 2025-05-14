package io.itaiit.vertx;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

public class VertxHttpServerRequest extends AbstractServerHttpRequest {

    private final HttpServerRequest request;
    private final DataBufferFactory bufferFactory;

    public VertxHttpServerRequest(HttpServerRequest request, DataBufferFactory bufferFactory) throws URISyntaxException {
        super(initUri(request), "", (MultiValueMap<String, String>) request.headers());
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        return new URI(resolveBaseUrl(request) + resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        String scheme = request.scheme();
        String header = request.headers().get(HttpHeaderNames.HOST);
        if (header != null) {
            final int portIndex;
            if (header.startsWith("[")) {
                portIndex = header.indexOf(':', header.indexOf(']'));
            } else {
                portIndex = header.indexOf(':');
            }
            if (portIndex != -1) {
                try {
                    return new URI(scheme, null, header.substring(0, portIndex),
                            Integer.parseInt(header.substring(portIndex + 1)), null, null, null);
                } catch (NumberFormatException ex) {
                    throw new URISyntaxException(header, "Unable to parse port", portIndex);
                }
            } else {
                return new URI(scheme, header, null, null);
            }
        } else {
            Assert.state(request.localAddress() != null, "No host address available");
            return new URI(scheme, null, request.localAddress().host(),
                    request.localAddress().port(), null, null, null);
        }
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        String uri = request.uri();
        for (int i = 0; i < uri.length(); i++) {
            char c = uri.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                break;
            }
            if (c == ':' && (i + 2 < uri.length())) {
                if (uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
                    for (int j = i + 3; j < uri.length(); j++) {
                        c = uri.charAt(j);
                        if (c == '/' || c == '?' || c == '#') {
                            return uri.substring(j);
                        }
                    }
                    return "";
                }
            }
        }
        return uri;
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (Cookie cookie : this.request.cookies()) {
            HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
            cookies.add(cookie.getName(), httpCookie);
        }
        return cookies;
    }

    @Override
    protected SslInfo initSslInfo() {
        return null;
    }

    @Override
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override
    public String getMethodValue() {
        return this.request.method().name();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        AtomicReference<DataBuffer> dataBuffer = new AtomicReference<>();
        request.bodyHandler(event -> {
            dataBuffer.set(bufferFactory.wrap(event.getBytes()));
        });

        return Flux.just(dataBuffer.get());
    }
}
