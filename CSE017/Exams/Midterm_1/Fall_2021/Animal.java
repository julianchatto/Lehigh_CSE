public abstract class Animal implements Comparable<Animal> {
    private String name;
    private double weight;
    private int lifespan;

    protected Animal(String name, double weight, int lifespan) {
        this.name = name;
        this.weight = weight;
        this.lifespan = lifespan;
    }

    public String toString() {
        return "Name: " + name + ", Weight: " + weight + ", Lifespan: " + lifespan;
    }

    public int compareTo(Animal a) {
        if (weight == a.weight) {
            return 0;
        }
        if (weight < a.weight) {
            return -1;
        }
        return 1;
    }
}