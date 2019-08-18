package ca.uoit.projectxiphos;

/**
 This class is used to handel passwords information which is going to be stored in te database
 */
public class Password {
    private int passid;
    private String passuser;
    private String passtitle;
    private String password;

    public Password(String user, String title, String pass) {
        this.passuser = user;
        this.passtitle = title;
        this.password = pass;
    }

    public int getId() {
        return passid;
    }
    public String getUser() {
        return passuser;
    }
    public String getTitle() { return passtitle; }
    public String getPass() {
        return password;
    }

    public void setPassId(int pid) { this.passid = pid; }
}
