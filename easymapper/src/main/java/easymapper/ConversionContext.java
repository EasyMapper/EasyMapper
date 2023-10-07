package easymapper;

import java.lang.reflect.Type;

public class ConversionContext {

    private final Mapper mapper;
    private final Type sourceType;
    private final Type destinationType;

    public ConversionContext(
        Mapper mapper,
        Type sourceType,
        Type destinationType
    ) {
        this.mapper = mapper;
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public Type getSourceType() {
        return sourceType;
    }

    public Type getDestinationType() {
        return destinationType;
    }
}