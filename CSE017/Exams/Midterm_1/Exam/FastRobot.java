public class FastRobot extends Robot{
    public FastRobot(){
        super();
    }
    public FastRobot(String name, int position){
        super(name, position);
    }
    public void move(int max) throws OutOfRangeException {
        int position = this.getPosition();
        position += 2;

        if (position > max ) {
            throw new OutOfRangeException();
        } else {
            this.setPosition(position);
        }
    }
    public Object clone(){
        return new FastRobot(this.getName(), this.getPosition());
    }
    public String toString(){
        return String.format("%-10s\t%s", "Fast", super.toString());
    }
}