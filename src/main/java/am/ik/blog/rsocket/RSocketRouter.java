package am.ik.blog.rsocket;

import java.util.List;

import org.reactivestreams.Publisher;

public interface RSocketRouter {
	List<RSocketRoute<? extends Publisher>> routes();
}
