package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.Rating;

import java.io.IOException;

public class CustomRatingSerializer extends StdSerializer<Rating> {
    public CustomRatingSerializer() {
        this(null);
    }

    protected CustomRatingSerializer(Class<Rating> t) {
        super(t);
    }

    @Override
    public void serialize(Rating rating, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(Rating.getStringRating(rating));
    }
}







