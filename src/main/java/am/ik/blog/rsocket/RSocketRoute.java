package am.ik.blog.rsocket;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import org.springframework.web.util.UriTemplate;

public class RSocketRoute<T extends Publisher> {
	private final UriTemplate uriTemplate;
	private final Function<RSocketRequest, RSocketResponse<T>> handler;

	public static <T extends Publisher> RSocketRoute<T> route(String uriTemplate,
															  Function<RSocketRequest, RSocketResponse<T>> handler) {
		return new RSocketRoute<T>(uriTemplate, handler);
	}

	public RSocketRoute(String uriTemplate,
						Function<RSocketRequest, RSocketResponse<T>> handler) {
		this.uriTemplate = new UriTemplate(
				uriTemplate.startsWith("/") ? uriTemplate : "/" + uriTemplate);
		this.handler = handler;
	}

	public RSocketResponse<T> invoke(String path, RSocketQuery query) {
		RSocketRequest request = new RSocketRequest(this.uriTemplate.match(path), query);
		return this.handler.apply(request);
	}

	public boolean matches(String path) {
		return this.uriTemplate.matches(path);
	}

	@Override
	public String toString() {
		return uriTemplate + " -> " + handler;
	}
}
