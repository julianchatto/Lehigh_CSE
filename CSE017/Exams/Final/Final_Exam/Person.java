/**
 * Abstract class to model a person attributes and operations
 */
public abstract class Person{
    private String name;
    private String phone;
    private String email;

    protected Person(){

    }
    protected Person(String n, String p, String e){
        name = n;
        phone = p;
        email = e;
    }
    public String getName(){ return name;}
    public String getPhone(){ return phone;}
    public String getEmail(){ return email;}

    public void setName(String n){ name = n;}
    public void setPhone(String p){ phone = p;}
    public void setEmail(String e){ email = e;}

    public String toString(){
        return String.format("%-20s\t%-10s\t%-10s", name, phone, email);
    }
}