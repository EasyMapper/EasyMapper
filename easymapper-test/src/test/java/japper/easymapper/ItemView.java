package japper.easymapper;

public final class ItemView {

    private long id;
    private String name;
    private Price listPrice;

    public ItemView(long id, String name, Price listPrice) {
        this.id = id;
        this.name = name;
        this.listPrice = listPrice;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Price getListPrice() {
        return listPrice;
    }
}
