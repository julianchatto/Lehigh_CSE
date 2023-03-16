public class Bat extends Mammal implements CanFly {
    private int flyingSpeed;
    public Bat(String name, double weight, int lifespan, int flyingSpeed)  {
        super(name, weight, lifespan);
        this.flyingSpeed = flyingSpeed;
    }

    public int flies() {
        return flyingSpeed;
    }
}