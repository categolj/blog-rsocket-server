package am.ik.blog.rsocket.router;

import java.util.Collections;
import java.util.List;

import am.ik.blog.entry.Tag;
import am.ik.blog.reactive.ReactiveTagMapper;
import am.ik.blog.rsocket.RSocketRouter;
import am.ik.blog.rsocket.RSocketRoute;
import am.ik.blog.rsocket.RSocketRequest;
import am.ik.blog.rsocket.RSocketResponse;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class TagRouter implements RSocketRouter {
	private final ReactiveTagMapper tagMapper;

	public TagRouter(ReactiveTagMapper tagMapper) {
		this.tagMapper = tagMapper;
	}

	RSocketResponse<Mono<List<String>>> getTags(RSocketRequest req) {
		return RSocketResponse.of(
				this.tagMapper.findOrderByTagNameAsc()
						.map(tags -> tags.stream().map(Tag::getValue).collect(toList())),
				ResolvableType.forClassWithGenerics(List.class, String.class));
	}

	@Override
	public List<RSocketRoute<? extends Publisher>> routes() {
		return Collections
				.singletonList(new RSocketRoute<>("/tags", this::getTags));
	}
}
