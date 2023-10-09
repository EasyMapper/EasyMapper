package easymapper;

import java.lang.reflect.Type;

public final class MappingContext {

    private final Mapper mapper;
    private final Type sourceType;
    private final Type destinationType;

    MappingContext(Mapper mapper, Type sourceType, Type destinationType) {
        this.mapper = mapper;
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    Mapper getMapper() {
        return mapper;
    }

    Type getSourceType() {
        return sourceType;
    }

    Type getDestinationType() {
        return destinationType;
    }
}
