package ca.uoit.projectxiphos;

/**
 This class is used to handel users information which is going to be stored in te database
 */
public class User {
    private int userid;
    private String username;
    private String userpass;
    private String email;
    private String first;
    private String last;
    private int checkLocal;

    public User(String name, String pass, String email, String first, String last, int checkLocal) {
        this.username = name;
        this.userpass = pass;
        this.email = email;
        this.first = first;
        this.last = last;
        this.checkLocal = checkLocal;
    }

    public int getId() {
        return userid;
    }
    public String getName() {
        return username;
    }
    public String getPass() { return userpass; }
    public String getEmail() {
        return email;
    }
    public String getFirst() {
        return first;
    }
    public String getLast() { return last; }
    public int getCheck() {
        return checkLocal;
    }

    public void setProductId(int pid) { this.userid = pid; }
}
