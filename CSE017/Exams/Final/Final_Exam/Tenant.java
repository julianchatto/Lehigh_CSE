/**
 * Class to model a tenant of an apartment
 */
public class Tenant extends Person{
    private String apptNumber;
    private String buildingName;

    public Tenant(){
        super();
    }
    public Tenant(String name, String phone, String email, 
                  String bname, String number){
        super(name, phone, email);
        apptNumber = number;
        buildingName = bname;
    }
    public String getAptNumber(){ return apptNumber;}
    public void setAptNumber(String number){ apptNumber = number;}

    public String getbuildingName(){ return buildingName;}
    public void setBuildingName(String name){ buildingName = name;}

    public boolean equals(Object o){
        if(o instanceof Tenant){
            Tenant t = (Tenant) o;
            return getName().equals(t.getName());
        }
        return false;
    }
    public String toString(){
        return String.format("%s\t%-5s\t%-10s", super.toString(), buildingName, apptNumber);
    }
}