package am.ik.blog.rsocket;

import java.util.Optional;
import java.util.OptionalInt;

import org.springframework.data.domain.PageRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

public class RSocketQuery {
	private final MultiValueMap<String, String> params;

	public static RSocketQuery parse(String query) {
		return new RSocketQuery(query);
	}

	private RSocketQuery(String query) {
		this.params = UriComponentsBuilder.newInstance().query(query).build()
				.getQueryParams();
	}

	public PageRequest pageRequest() {
		return PageRequest.of(this.page(), this.size());
	}

	public int page() {
		return this.getInt("page").orElse(0);
	}

	public int size() {
		return this.getInt("size").orElse(20);
	}

	public Optional<String> getString(String name) {
		String s = this.params.getFirst(name);
		if (StringUtils.isEmpty(s)) {
			return Optional.empty();
		}
		return Optional.of(s);

	}

	public Optional<Boolean> getBoolean(String name) {
		return this.getString(name).map(Boolean::parseBoolean);
	}

	public OptionalInt getInt(String name) {
		String s = this.params.getFirst(name);
		if (StringUtils.isEmpty(s)) {
			return OptionalInt.empty();
		}
		try {
			return OptionalInt.of(Integer.parseInt(s));
		}
		catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}
}
