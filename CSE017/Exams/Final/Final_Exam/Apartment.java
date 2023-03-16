import java.util.Comparator;

/**
 * Class Apartment to model an apartment attributes and operations
 */
public class Apartment implements Comparable<Apartment> {
    //<rent extends Comparable<rent>> 
    private String number;
    private int rooms;
    private double rent;
    private boolean rented;

    public Apartment(){
    }
    public Apartment(String number, int rooms, double rent){
        this.number = number;
        this.rooms = rooms;
        this.rent = rent;
        rented = false;
    }
    public String getNumber(){ return number;}
    public int getRooms(){ return rooms;}
    public double getRent() { return rent;}
    public boolean isRented() { return rented;}


    public void setNumber(String number){ this.number = number;}
    public void setRooms(int rooms){ this.rooms = rooms;}
    public void setRent(double rent){ this.rent = rent;}
    public void rent(){ rented = true;}
    public void release(){ rented = false;}
    

    public String toString(){
        return String.format("%-10s\t%-10d\t$%-10.2f\t%-5s", number, rooms, rent, isRented()?"Rented":"Free");
    }

    public int compareTo(Apartment o) {

        if (this.getRent() < o.getRent() ) {
            return -1;
        } else if (this.getRent() > o.getRent()) {
            return 1;
        }
        return 0;
    }
    

}