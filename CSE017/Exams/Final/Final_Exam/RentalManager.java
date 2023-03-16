/**
 * Class to model the Real Estate company data and operations
 */
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RentalManager{
    private ArrayList<Building> buildings;
    private HashMap<String, Tenant> tenants;

    public RentalManager(String buildingFile, String tenantFile){
        tenants = new HashMap<>();
        buildings = new ArrayList<>();
        readTenants(tenantFile);
        readBuildings(buildingFile);
    }
    /**
     * Method to update the array list buildings
     * @param filename where the building information is
     */
    private void readBuildings(String filename){
        File file = new File(filename);
        
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String tempstore = "";
                String name = scan.next();
                int numApartments = Integer.valueOf(scan.next());
               
                String temp1 = scan.next(), temp2 = scan.next(), temp3 = scan.next();
                String address = temp1 +  " " + temp2 +  " " + temp3;
                
                Building build = new Building(name, address);
                String next = "";
                for (int i = 0; i < numApartments; i++) {
                    String apt = scan.next();
                    int x = Integer.parseInt(scan.next());
                    double rent = Double.parseDouble(scan.next());
                    build.addApartment(apt, x, rent);
                    if (aptIsRented(name, apt)) {
                        build.findApartment(apt).rent();
                    }
                }
                buildings.add(build);
              
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error while reading");
            System.exit(0);
        }
    }

    private boolean aptIsRented(String building, String apt){
        ArrayList<Tenant> list = tenants.values();
        for(Tenant t: list){
            if (t.getbuildingName().equals(building) && t.getAptNumber().equals(apt)){
                return true;
            }
        }
        return false;
    }
    private void readTenants(String filename){
        File file = new File(filename);
        try{
            Scanner readFile = new Scanner(file);
            while (readFile.hasNext()){
                String fname = readFile.next();
                String lname = readFile.next();
                String phone = readFile.next();
                String email = readFile.next();
                String building = readFile.next();
                String apt = readFile.next();
                String name = fname + " " + lname;
                Tenant t = new Tenant(name, phone, email, building, apt);
                tenants.put(name, t);
            }
            readFile.close();
        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
            System.exit(0);
        }
    }
    public Building findBuilding(String buildingName){
        for(int j=0; j<buildings.size(); j++){
            if(buildings.get(j).getName().equals(buildingName)){
                return buildings.get(j);
            }
        }
        return null;
    }
    public Tenant findTenant(String name){
        return tenants.get(name);
    }
    public void viewApartments(String number){
        for(int i=0; i<buildings.size(); i++){
            Apartment apt = buildings.get(i).findApartment(number);
            if(apt != null){
                System.out.println(buildings.get(i).getName() + apt);
            }
        }
    }
    /**
     * Method to display all the apartments with rent below a given amount
     * @param rent the amount
     */
    public void filterApartments(double rent){
        ArrayList<Apartment> list = new ArrayList<>();
        for (Building x: buildings) {
            ArrayList<Apartment> tList = x.filterApartments(rent);
            for (Apartment a: tList) {
                if(a.getRent() < rent) {
                    list.add(a);
                } 
            }
        }
        bubbleSort(list);

        System.out.printf("%-30s\t%-15s\t%-15s%-15s\n", "Apartment", "Rooms", "Rent", "Rented/Free");
        for (Apartment x: list) {
            String r = "Free";
            if (x.isRented()) {
                r = "Rented";
            }
            System.out.printf("%-30s\t%-15d\t%-15f\t%-15s\n", x.getNumber(), x.getRooms(), x.getRent(), r);
        }
    }
    /**
     * Bubble sort method to be used by filterApartments
     */ 
    private <E extends Comparable<E>> void bubbleSort(ArrayList<E> list) { 
        boolean sorted = false; 
        for (int k=1; k < list.size() && !sorted; k++) { 
            sorted = true; 
            for (int i=0; i<list.size()-k; i++) { 
                if (list.get(i).compareTo(list.get(i+1)) > 0) { 
                    E temp = list.get(i); 
                    list.set(i, list.get(i+1)); 
                    list.set(i+1, temp); 
                    sorted = false; 
                } 
            } 
        }
    }
    /**
     * Method to display the list of tenants and the apartments they are renting
     * See the sample output for the type of information that is displayed for eah tenant
     */
    public void viewApartmentTenants(){
       ArrayList<Tenant> list = tenants.values();
       System.out.printf("%-30s\t%-10s\t%-10s\t%-10s\t%-10s\n", "Tenant", "Building", "Apartment", "Rooms", "Rent");
       for (Tenant x: list) {
        int num = 0;
        for (int i = 0; i < buildings.size(); i++) {
            if(buildings.get(i).getName().equals(x.getbuildingName())) {
                System.out.printf("%-30s\t%-10s\t%-10s\t%-10d\t%-10f\n", x.getName(), x.getbuildingName(), x.getAptNumber(), buildings.get(i).findApartment(x.getAptNumber()).getRooms(),  buildings.get(i).findApartment(x.getAptNumber()).getRent());
            }
        }
        
       }
    }
    
    public void viewTotalRent(){
        double total = 0;
        System.out.printf("%-10s\t%-30s\t%-10s\n", "Building", "Address", "Total Rent");
         for(int i=0; i<buildings.size(); i++){
                Building b = buildings.get(i);
                double rent = b.getTotalRent();
                System.out.printf("%-10s\t%-30s\t%-10.2f\n", b.getName(), b.getAddress(), rent);
                total += rent;
        }
       System.out.printf("%-40s\t%-10.2f\n", "Total", total);
    }
}
