public abstract class Shape {
    private String color;

    protected Shape(){
        color = "none";
    }

    protected Shape(String c) {
        color = c;
    }

    public String toString() {
        return color;
    }

    public abstract double getArea();
    public abstract double getPerimeter();
}