package top.itfinally.builder.repository.po.base;

public class VideoCategoryEntity extends BaseEntity<VideoCategoryEntity> {
    private String name;
    private int weight;

    public String getName() {
        return name;
    }

    public VideoCategoryEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public VideoCategoryEntity setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return "VideoCategoryEntity{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
