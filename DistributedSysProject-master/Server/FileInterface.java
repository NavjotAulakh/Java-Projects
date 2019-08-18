import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * This interface extends Remote to provide RMI functionality and defines the provided methods by the server
 */
public interface FileInterface extends Remote {
    //Initializes any variables to be used by the Remote connection and methods
    public final static String SERVICENAME = "fileService";
    public Pattern userPat = Pattern.compile("[A-Za-z0-9_]+");

    //The definitions of all provided methods by server through their implementation to be used by the client
    public String generatePassHash(String passInput) throws IOException;
    public void writeUserPassToFile(String username, String Password) throws IOException;
    public String storePassToFile(String username, String password) throws IOException;
    public int checkUsername(String username) throws RemoteException, IOException;
    public int checkStoredPass(String username, String password) throws IOException;
    public int checkLogin(String username, String password) throws IOException;
    public String listPass(String user) throws IOException;
    public String checkPassword(String password)  throws IOException;
    public String register(String username, String password) throws IOException;
    public String login(String username, String password) throws IOException;
    public String logout() throws  IOException;
    public byte[] downloadFile(CFile file, String username) throws RemoteException;
    public ArrayList<CFile> getList(String username) throws RemoteException;
    public void uploadFile(byte[] data, CFile file) throws RemoteException;
    public void addUserToGroup(String username, String group)throws RemoteException;
    public void createGroup(String msg, String groupName) throws RemoteException;
	public ArrayList<String> getGroups(String username) throws RemoteException;
}