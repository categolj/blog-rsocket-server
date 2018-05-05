package am.ik.blog.rsocket.router;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import am.ik.blog.entry.Category;
import am.ik.blog.entry.Entry;
import am.ik.blog.entry.EntryId;
import am.ik.blog.entry.Tag;
import am.ik.blog.entry.criteria.CategoryOrders;
import am.ik.blog.entry.criteria.SearchCriteria;
import am.ik.blog.reactive.ReactiveEntryMapper;
import am.ik.blog.rsocket.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

@Component
public class EntryRouter implements RSocketRouter {
	private final ReactiveEntryMapper entryMapper;

	public EntryRouter(ReactiveEntryMapper entryMapper) {
		this.entryMapper = entryMapper;
	}

	RSocketResponse getEntry(RSocketRequest req) {
		EntryId entryId = new EntryId(req.getPathVariables().get("entryId"));
		boolean excludeContent = req.getQuery().getBoolean("excludeContent")
				.orElse(false);
		Mono<Entry> entry = this.entryMapper.findOne(entryId, excludeContent);
		return RSocketResponse.of(entry, Entry.class);
	}

	RSocketResponse getEntries(RSocketRequest req) {
		RSocketQuery query = req.getQuery();
		boolean excludeContent = req.getQuery().getBoolean("excludeContent").orElse(true);
		SearchCriteria searchCriteria = query.getString("q")
				.map(keyword -> SearchCriteria.defaults().keyword(keyword))
				.orElseGet(SearchCriteria::defaults) //
				.excludeContent(excludeContent) //
				.build();
		Flux<Entry> entries = this.entryMapper.collectAll(searchCriteria,
				query.pageRequest());
		return RSocketResponse.of(entries, Entry.class);
	}

	RSocketResponse getEntriesByTag(RSocketRequest req) {
		RSocketQuery query = req.getQuery();
		boolean excludeContent = req.getQuery().getBoolean("excludeContent").orElse(true);
		Tag tag = new Tag(req.getPathVariables().get("tag"));
		SearchCriteria searchCriteria = SearchCriteria.builder().tag(tag)
				.excludeContent(excludeContent).build();
		Flux<Entry> entries = this.entryMapper.collectAll(searchCriteria,
				query.pageRequest());
		return RSocketResponse.of(entries, Entry.class);
	}

	RSocketResponse getEntriesByCategories(RSocketRequest req) {
		RSocketQuery query = req.getQuery();
		List<Category> categories = Arrays
				.stream(req.getPathVariables().get("categories").split(",")) //
				.map(Category::new) //
				.collect(Collectors.toList());
		int order = categories.size() - 1;
		boolean excludeContent = req.getQuery().getBoolean("excludeContent").orElse(true);
		Category category = categories.get(order);
		SearchCriteria searchCriteria = SearchCriteria.builder()
				.categoryOrders(new CategoryOrders().add(category, order) /* TODO */)
				.excludeContent(excludeContent).build();
		Flux<Entry> entries = this.entryMapper.collectAll(searchCriteria,
				query.pageRequest());
		return RSocketResponse.of(entries, Entry.class);
	}

	@Override
	public List<RSocketRoute<? extends Publisher>> routes() {
		return Arrays.asList(
				new RSocketRoute<Mono<Entry>>("/entries/{entryId}", this::getEntry),
				new RSocketRoute<Flux<Entry>>("/entries", this::getEntries),
				new RSocketRoute<Flux<Entry>>("/tags/{tag}/entries",
						this::getEntriesByTag),
				new RSocketRoute<Flux<Entry>>("/categories/{categories}/entries",
						this::getEntriesByCategories));
	}
}
