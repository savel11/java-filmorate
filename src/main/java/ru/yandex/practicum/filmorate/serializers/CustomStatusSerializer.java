package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.Status;

import java.io.IOException;

public class CustomStatusSerializer extends StdSerializer<Status> {
    public CustomStatusSerializer() {
        this(null);
    }

    protected CustomStatusSerializer(Class<Status> t) {
        super(t);
    }

    @Override
    public void serialize(Status status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(Status.getStatusString(status));
    }
}
