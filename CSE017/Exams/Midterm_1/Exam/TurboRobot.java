

public class TurboRobot extends Robot{
    public TurboRobot(){
        super();
    }
    public TurboRobot(String name, int position){
        super(name, position);
    }
    public void move(int max) throws OutOfRangeException {
        
        int position = this.getPosition();
        position *= 2;

        if (position > max ) {
            throw new OutOfRangeException();
        } else {
            this.setPosition(position);
        }
    }
    public Object clone(){
        return new TurboRobot(this.getName(), this.getPosition());
    }
    public String toString(){
        return String.format("%-10s\t%s", "Turbo", super.toString());
    }
}