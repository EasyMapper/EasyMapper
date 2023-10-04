package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImmutableBag<T> {
    private final T value;
}
