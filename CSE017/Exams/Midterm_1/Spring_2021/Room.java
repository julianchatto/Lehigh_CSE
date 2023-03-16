public abstract class Room implements Comparable<Room> {
    private String number;
    private int capacity;
    private int area;

    protected Room(String number, int capacity, int area) {
        this.number = number;
        this.capacity = capacity;
        this.area = area;
    }

    public String getNumber() {
        return number;
    }

    public int getCapacity() {
        return capacity;
    }
    public int getArea() {
        return area;
    }

    public void setNumber(String n) {
        number = n;
    }

    public void setCapacity(int c) {
        capacity = c;

    }

    public void setArea(int a) {
        area = a;
    }

    public String toString() {
        return "Number: " + number + ", Capacity: " + capacity + ", Area: " + area;
    }

    public int compareTo(Room r) {
        if (this.getCapacity() == r.getCapacity()) {
            return 0;
        }
        if (this.getCapacity() < r.getCapacity()) {
            return -1;
        }
        return 1;
    }
}
