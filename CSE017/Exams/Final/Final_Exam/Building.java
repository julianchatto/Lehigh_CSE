/**
 * Class Building to model a building attributes and operations
 */
import java.util.ArrayList;

public class Building{
    private String name;
    private String address;
    private TreeMap<String, Apartment> apartments;

    public Building(){
    }
    public Building(String n, String a){
        name = n;
        address = a;
        apartments = new TreeMap<>();
    }
    public String getName(){ return name;}
    public String getAddress(){ return address;}

    public void setName(String name){ this.name = name;}
    public void setAddress(String address){ this.address = address;}

    public Apartment findApartment(String number){
        return apartments.get(number);
    }
    public boolean addApartment(String number, int rooms, double rent){
        Apartment apt = new Apartment(number, rooms, rent);
        return apartments.add(number, apt);
    }
    /**
     * Method to filter the apartments with rent less than amount
     * @param amount of rent
     * @return array list of apartments with rent less than amount
     */
    public ArrayList<Apartment> filterApartments(double amount){
        ArrayList<Apartment> apts = apartments.values();
        ArrayList<Apartment> newList = new ArrayList<>();
        int size = apts.size();
        for(Apartment a: apts){
            if(a.getRent() < amount){
                newList.add(a);
            }
        }
        return newList;
    }
    /**
     * Write the definition of this method
     * @return total amount of rent for rented apartments only
     */
    public double getTotalRent(){
        double total = 0;
        ArrayList<Apartment> apts = apartments.values();
        for (Apartment a: apts) {
            if (a.isRented()) {
                total += a.getRent();
            }
        }
        return total;
    }
}
