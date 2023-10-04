package test.easymapper.fixture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutableBag<T> {
    private T value;
}
