package model;


public class Client implements Entity {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;

//======================================================================================================================

    public Client (int id, String firstName, String lastName, String email, String phoneNumber, String companyName) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName; 
    }

//======================================================================================================================

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    

//======================================================================================================================

    @Override
    public String toString() {
        return "Client [id= " + this.getId() + ", name= '" + this.getFirstName() + " " + this.getLastName() + "', email= '" + this.getEmail() +  "', phone= '" + this.getPhoneNumber() + "', company= '" + this.getCompanyName() + "']";
    }
}
