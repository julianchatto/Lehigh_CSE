public class Lab extends Room {
    private int computers;

    public Lab(String number, int capacity, int area, int computers) {
        super(number, capacity, area);
        this.computers = computers;
    }

    public int getCopmuters() {
        return computers;
    }

    public void setComputers(int c) {
        computers = c;
    }
   
    public String toString() {
        return "Lab: " + super.toString() + ", Computers: " + computers;
    }


}
