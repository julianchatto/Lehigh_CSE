/**
 * Test class for the Rental Manager class and the related classes
 */
public class Test{
    public static void main(String[] args){
        RentalManager myRental = new RentalManager("buildings.txt", "tenants.txt");
        System.out.println("\nList of tenants");
        myRental.viewApartmentTenants();
        System.out.println("\nList of apartments with a rent below $1000");
        myRental.filterApartments(1000);
        System.out.println("\nTotal rent income by building");
        myRental.viewTotalRent();
    }
}