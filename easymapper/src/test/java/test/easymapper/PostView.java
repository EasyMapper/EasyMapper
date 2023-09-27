package test.easymapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostView {
    private String id;
    private String authorId;
    private String title;
    private String text;
}
