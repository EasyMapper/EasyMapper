package test.easymapper;

import lombok.Getter;

@Getter
public class ItemView {

    private final long id;
    private final String name;
    private final Price listPrice;

    public ItemView(long id, String name, Price listPrice) {
        this.id = id;
        this.name = name;
        this.listPrice = listPrice;
    }
}
