package pl.setlikD.restapi.star;

public class StarDto {
    private String name;
    private long distance;
    public StarDto(String name, long distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public long getDistance() {
        return distance;
    }
}
