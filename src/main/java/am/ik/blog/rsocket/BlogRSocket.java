package am.ik.blog.rsocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.buffer.PooledByteBufAllocator;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.util.ByteBufPayload;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

@Component
public class BlogRSocket extends AbstractRSocket {
	private static final Logger log = LoggerFactory.getLogger(BlogRSocket.class);
	private final List<RSocketRoute<? extends Publisher>> routes;
	private final SerializationFormat serializationFormat;
	private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(
			PooledByteBufAllocator.DEFAULT);

	public BlogRSocket(List<RSocketRouter> routers) {
		ArrayList<RSocketRoute<? extends Publisher>> routes = new ArrayList<>();
		routers.forEach(router -> routes.addAll(router.routes()));
		this.routes = routes;
		this.serializationFormat = SerializationFormat.SMILE;
		this.routes.forEach(route -> log.info("{}", route));
	}

	// Payload#getData -> query
	// Payload#getMetadata -> path

	@Override
	public Flux<Payload> requestStream(Payload payload) {
		String path = this.getPath(payload);
		log.debug("[requestStream] {}", path);
		return Flux.fromIterable(this.routes) //
				.filter(route -> route.matches(path)) //
				.flatMap(route -> {
					RSocketQuery query = RSocketQuery.parse(payload.getDataUtf8());
					RSocketResponse<?> response = route.invoke(path, query);
					return this.toPayload(response.body(), response.type(),
							this.serializationFormat.streamMediaType());
				}) //
				.switchIfEmpty(
						Mono.fromCallable(() -> DefaultPayload.create("Not Found")));
	}

	@Override
	public Mono<Payload> requestResponse(Payload payload) {
		String path = this.getPath(payload);
		log.debug("[requestResponse] {}", path);
		return Flux.fromIterable(this.routes) //
				.filter(route -> route.matches(path)) //
				.flatMap(route -> {
					RSocketQuery query = RSocketQuery.parse(payload.getDataUtf8());
					RSocketResponse<?> response = route.invoke(path, query);
					return this.toPayload(response.body(), response.type(),
							this.serializationFormat.singleMediaType());
				}) //
				.next() //
				.switchIfEmpty(
						Mono.fromCallable(() -> DefaultPayload.create("Not Found")));
	}

	private String getPath(Payload payload) {
		String path = payload.getMetadataUtf8();
		return path.startsWith("/") ? path : "/" + path;
	}

	private Flux<Payload> toPayload(Publisher<?> entries, ResolvableType elementType,
			MimeType mimeType) {
		return this.serializationFormat.encoder()
				.encode(entries, dataBufferFactory, elementType, mimeType,
						Collections.emptyMap())
				.cast(NettyDataBuffer.class)
				.map(dataBuffer -> ByteBufPayload.create(dataBuffer.getNativeBuffer()));
	}
}
