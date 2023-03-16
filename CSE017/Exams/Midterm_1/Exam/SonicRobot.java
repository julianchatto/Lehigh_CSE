public class SonicRobot extends Robot{
    public SonicRobot(){
        super();
    }
    public SonicRobot(String name, int position){
        super(name, position);
    }
    public void move(int max) throws OutOfRangeException {
        int position = this.getPosition();
        position *= 10;

        if (position > max ) {
            throw new OutOfRangeException();
        } else {
            this.setPosition(position);
        }
    }
    public Object clone(){
        return new SonicRobot(this.getName(), this.getPosition());
    }
    public String toString(){
        return String.format("%-10s\t%s", "Sonic", super.toString());
    }
}