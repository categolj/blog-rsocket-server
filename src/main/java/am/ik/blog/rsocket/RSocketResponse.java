package am.ik.blog.rsocket;

import org.reactivestreams.Publisher;

import org.springframework.core.ResolvableType;

public class RSocketResponse<T extends Publisher> {
	private final T body;
	private final ResolvableType resolvableType;

	public RSocketResponse(T body, ResolvableType resolvableType) {
		this.body = body;
		this.resolvableType = resolvableType;
	}

	public static <T extends Publisher> RSocketResponse<T> of(T body,
			ResolvableType resolvableType) {
		return new RSocketResponse<>(body, resolvableType);
	}

	public static <T extends Publisher<S>, S> RSocketResponse<T> of(T body,
			Class<S> clazz) {
		return new RSocketResponse<>(body, ResolvableType.forClass(clazz));
	}

	public T body() {
		return body;
	}

	public ResolvableType type() {
		return resolvableType;
	}
}
