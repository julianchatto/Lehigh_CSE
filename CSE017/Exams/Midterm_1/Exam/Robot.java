public abstract class Robot implements Cloneable, Movable, Comparable<Robot> {
    private String name;
    private int position;
    protected Robot(){
        name = "none";
        position = 0;
    }
    protected Robot(String name, int position){
        this.name = name;
        this.position = position;
    }
    public String getName(){
        return name;
    }
    public int getPosition(){
        return position;
    }
    public void setName(String n){
        name = n;
    }
    public void setPosition(int p){
        position = p;
    }
    public String toString(){
        return String.format("%-10s\t%-5d", name, position);
    }
    public abstract void move(int max) throws OutOfRangeException;
    public abstract Object clone();

    public int compareTo(Robot r) {
        if (position == r.position) {
            return 0;
        }
        if (position < r.position) {
            return -1;
        }
        return 1;
    }
    
}