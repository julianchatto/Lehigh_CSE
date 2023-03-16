public class Circle extends Shape {
    private double radius;

    public Circle() {
        super();
        radius = 1.0;
    }

    public Circle (String c, double r) {
        super(c);
        radius = r;
    }
    public String toString() {
        return super.toString() + ", " + radius + ", " + getArea() + ", " + getPerimeter();
    }
    public double getArea() {
        return Math.PI * radius *radius;
    }
    public double getPerimeter() {
        return 2*Math.PI*radius;
    }
}