package easymapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public abstract class TypeReference<T> {

    private final Type type;

    protected TypeReference() {
        ParameterizedType reference = (ParameterizedType) getClass().getGenericSuperclass();
        this.type = reference.getActualTypeArguments()[0];
    }
}
