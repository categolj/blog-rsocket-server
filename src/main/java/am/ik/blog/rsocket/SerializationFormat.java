package am.ik.blog.rsocket;

import org.springframework.core.codec.Encoder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;

public enum SerializationFormat {
	JSON(new Jackson2JsonEncoder(), MediaType.APPLICATION_JSON,
			new MediaType("application", "stream+json")), SMILE(
					new Jackson2SmileEncoder(),
					new MediaType("application", "x-jackson-smile"),
					new MediaType("application", "stream+x-jackson-smile"));

	private final Encoder<Object> encoder;
	private final MediaType singleMediaType;
	private final MediaType streamMediaType;

	SerializationFormat(Encoder<Object> encoder, MediaType singleMediaType,
			MediaType streamMediaType) {
		this.encoder = encoder;
		this.singleMediaType = singleMediaType;
		this.streamMediaType = streamMediaType;
	}

	public Encoder<Object> encoder() {
		return encoder;
	}

	public MediaType singleMediaType() {
		return singleMediaType;
	}

	public MediaType streamMediaType() {
		return streamMediaType;
	}
}
