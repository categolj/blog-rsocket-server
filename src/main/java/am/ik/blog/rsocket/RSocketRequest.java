package am.ik.blog.rsocket;

import java.util.Map;

public class RSocketRequest {
	private final Map<String, String> pathVariables;
	private final RSocketQuery query;

	public RSocketRequest(Map<String, String> pathVariables, RSocketQuery query) {
		this.pathVariables = pathVariables;
		this.query = query;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public RSocketQuery getQuery() {
		return query;
	}
}
