public class Pro {
    private String Address;
    private String contact;
    private String Email;
    private String Name;
    private String password;

    public Pro()
    {

    }
    public Pro(String name, String contact, String email,String password) {
        this.contact = contact;
        this.Email = email;
        this.Name = name;
        this.password=password;
    }


    public String getName() {
        return Name;
    }

    public String getPassword() {
        return password;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return Email;
    }

    public void setAddress(String pass) {
        this.password = pass;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        Name = name;
    }

}
