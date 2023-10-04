package test.easymapper.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Post {
    private final UUID id;
    private final UUID authorId;
    private final String title;
    private final String text;
}
