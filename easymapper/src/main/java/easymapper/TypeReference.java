package easymapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;
import lombok.val;

@Getter
@SuppressWarnings("unused")
public abstract class TypeReference<T> {

    private final Type type;

    protected TypeReference() {
        val reference = (ParameterizedType) getClass().getGenericSuperclass();
        this.type = reference.getActualTypeArguments()[0];
    }
}
