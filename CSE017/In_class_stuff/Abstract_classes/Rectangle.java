public class Rectangle extends Shape {
    private double length, width;

    public Rectangle() {
        super();
        length = width = 1.0;
    }

    public Rectangle(String c, double l, double w) {
        super(c);
        length = l;
        width = w;
    }

    public String toString() {
        return super.toString() + ", " + length + ", " + width + ", " + getArea() + ", " + getPerimeter();
    }
    public double getArea() {
        return length * width;
    
    }
    public double getPerimeter() {
        return 2*length + 2*width;
    }
}