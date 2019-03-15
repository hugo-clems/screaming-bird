package hutoch.m2dl.screamingbird.models;

public class Level {

    private int id;

    private String name;

    public Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
