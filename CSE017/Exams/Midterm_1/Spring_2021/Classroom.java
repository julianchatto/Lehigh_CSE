public class Classroom extends Room {
    public Classroom(String number, int capacity, int area) {
        super(number, capacity, area);
    }
    public String toString() {
        return "Classroom: " + super.toString();
    } 
    
}
