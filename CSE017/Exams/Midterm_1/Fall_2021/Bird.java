public class Bird extends Animal implements CanFly {
    private int flyingSpeed;
    public Bird(String name, double weight, int lifespan, int flyingSpeed)  {
        super(name, weight, lifespan);
        this.flyingSpeed = flyingSpeed;
    }

    public int flies() {
        return flyingSpeed;
    }
}