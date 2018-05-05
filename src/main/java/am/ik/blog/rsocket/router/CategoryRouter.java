package am.ik.blog.rsocket.router;

import java.util.Collections;
import java.util.List;

import am.ik.blog.entry.Category;
import am.ik.blog.reactive.ReactiveCategoryMapper;
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
public class CategoryRouter implements RSocketRouter {
	private final ReactiveCategoryMapper categoryMapper;

	public CategoryRouter(ReactiveCategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}

	RSocketResponse<Mono<List<List<String>>>> getCategories(RSocketRequest req) {
		return RSocketResponse.of(
				this.categoryMapper.findAll()
						.map(categories -> categories.stream()
								.map(c -> c.getValue().stream().map(Category::getValue)
										.collect(toList()))
								.collect(toList())),
				ResolvableType.forClassWithGenerics(List.class,
						ResolvableType.forClassWithGenerics(List.class, String.class)));
	}

	@Override
	public List<RSocketRoute<? extends Publisher>> routes() {
		return Collections.singletonList(
				new RSocketRoute<>("/categories", this::getCategories));
	}
}
