package easymapper;

import java.lang.reflect.Type;

public class ConversionContext {

    private final Mapper mapper;
    private final Type sourceType;
    private final Type destinationType;

    ConversionContext(Mapper mapper, Type sourceType, Type destinationType) {
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

    MappingContext toMappingContext() {
        return new MappingContext(mapper, sourceType, destinationType);
    }

    static ConversionContext fromMappingContext(MappingContext context) {
        return new ConversionContext(
            context.getMapper(),
            context.getSourceType(),
            context.getDestinationType()
        );
    }
}
