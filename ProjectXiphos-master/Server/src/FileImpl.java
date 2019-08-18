
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FileImpl
{

    String name;
    File loginFile = new File("loginInfo.txt");
    File fileDB = new File("fileDB.txt");
    public final static String SERVICENAME = "fileService";
    public Pattern userPat = Pattern.compile("[A-Za-z0-9_]+");

    /**
     * The constructor for the server.
     * @param s A String is required to setup the object's name.
     * @throws RemoteException As with all remote object methods, it throws a special type of exception when the registry is not available.
     */

    public FileImpl(String s) throws RemoteException {
        super();
        name = s;
        System.out.println("File Server initialized.");
    }
    String cUser;
    String cPass;
    /**
     * This method is used to register accounts for the clients and storing the usernames and passwords into a login file
     * @param username username input sent by the client to register
     * @param password password input sent by the client to register
     * @return return an appropriate message to client so they can login to access storing functionality
     * @throws IOException exception in case the login file couldn't be created or accessed
     */
    public String register(String username, String password, String email) throws IOException {
        // Generates a file for storing accounts with format: usernames#passwords(encrypted with SHA-256)
        File loginFile = new File("loginInfo.txt");
        loginFile.createNewFile(); // Create file if it doesn't exist
        if (userPat.matcher(username).matches() & username.length() > 3) {
            //Checks if username alread exists and registers if it doesn't
            if (checkUsername(username) == 1) {
                return "Username Already Exits. Try Another Username!"; //reply to client
            } else {
                System.out.println(username);
                System.out.println(password);
                writeUserPassToFile(username, password, email);
                return "Registered Account"; //reply to client
            }
        } else {
            return "Invalid Username. Please try another username with greater than 3 characters";
        }
    }

    /**
     * This method is used to login in a user by checking their username and password with the loginInfo on the server
     * @param username the username input received by the client
     * @param password the password input received by the client
     * @return returns an appropriate message to the user and activates additional functionality
     * @throws RemoteException exception in case connection fails or disconnects
     * @throws IOException exception in case the login file couldn't be created or opened
     */
    public String login(String username, String password) throws RemoteException, IOException {

        cUser = username;
        cPass = password;
        // Generates a file for storing accounts with format: usernames#passwords(encrypted with SHA-256)
        File loginFile = new File("loginInfo.txt");
        loginFile.createNewFile(); // Create file if it doesn't exist

        int loginCheck = checkLogin(username, password); //check if login is matched by reading loginInfo.txt
        //Check if login info is correct and log user into account
        if (loginCheck == 1) {
            return ("Welcome " + username); // reply welcome message
        } else if (loginCheck == 2) {
            return "Password is Incorrect"; // reply
        } else {
            return "Account Does not Exist"; // reply
        }
    }

    /**
     * This method is to logout thus clearing user variables in server and client
     * @return returns a goodbye message when the user clicks logout
     */
    public String logout() {
        cUser = ""; //clear user string
        return ("Good Bye "); //reply logout message

    }

    /**
     * This method generates the MD5 hash equivalent of the provided password
     *
     * @param passInput - input for generating MD5 hash as a String
     * @return return the generated MD5 hash as a String
     */
    public String generatePassHash(String passInput) {
        //tries to generate the hash value using the MessageDigest object
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hash = md5.digest();
            //Coverts the byte hash into a  String using the DatatypeConverter
            String passHash = DatatypeConverter.printHexBinary(hash).toUpperCase();
            return passHash; //return
        } catch (NoSuchAlgorithmException nex) {
            System.out.println("No Algorithm Found for hashing!"); //message if NoSuchAlgorithmException thrown
        }
        return "fail"; // return fail if pass couldn't be hashed
    }

    /**
     * This method takes username, password as inputs to write them into the loginInfo.txt file using formation
     * username#password using the BufferredWriter object and FileWriter.
     * @param username - for storing username of the account supplied by the client
     * @param password - for storing password of the account supplied by the client
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public void writeUserPassToFile(String username, String password, String email) throws IOException {
        // Generates and initialize a BufferedWriter object called fileOut using FileWriter with loginInfo.txt
        BufferedWriter fileOut = new BufferedWriter(new FileWriter("loginInfo.txt", true));
        new FileWriter(username + ".txt", true); //Creates a file for user for storage of supplied passwords
        fileOut.append(username + "#" + password + "#" + email); //Appends the string parameters into the file
        fileOut.newLine(); //Adds new line
        fileOut.close(); //Closes the file
    }

    /**
     * This method takes username, password as inputs to write the password into a username.txt file that was created
     * since registeration of account for storage
     * @param username - for storing username of the account supplied by the client
     * @param password - for storage of multiple passwords in the username.txt file
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public String storePassToFile(String username, String password) throws IOException {
        // Generates and initialize a BufferedWriter object called fileOut using FileWriter with username.txt
        BufferedWriter fileOut = new BufferedWriter(new FileWriter(username + ".txt", true));

        // Checks if the provided password already exists in storage using the checkStoredPass method
        if (checkStoredPass(username, password) == 0) {
            fileOut.append(password); //Appends the password to store into username.txt file
            fileOut.newLine(); //adds new line into the file
            fileOut.close(); //closes the file
            return "Password Stored"; //replies to client
        } else {
            return "Password Already Stored"; //replies to client if password is already stored
        }
    }

    /**
     * This method takes username as input in order to check if it already exists inside of loginInfo.txt file
     * @param username - for checking username
     * @return 0 if no is found and 1 if match is found
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public int checkUsername(String username) throws IOException {
        // Generates and initializes an object called fileIn in order to read file called loginInfo.txt
        BufferedReader fileIn = new BufferedReader(new FileReader("loginInfo.txt"));
        String line;
        String delims = "[#]"; //sets delims for splitting the string into an array
        int i = 0;

        // While loops reads through all the line of the file and splits each line into two using delims #
        while ((line = fileIn.readLine()) != null) {
            String[] temp = line.split(delims);

            //Checks if any of the lines usernames inside the file matches the input username
            if (username.equals(temp[0])) {
                fileIn.close(); //if matched close file
                return 1; //return 1 for true as match was found
            }
        }
        fileIn.close();// close file
        return i; //return 0 for false as no match was found
    }
    /**
     * This method takes username, password as inputs to check if the password is in storage of file username.txt
     * @param username - for username.txt file of the account supplied by the client
     * @param password - for checking stored password supplied by the client
     * @return 0 if no match is found and 1 if match is found
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public int checkStoredPass(String username, String password) throws IOException {
        // Generates and initializes an object called fileIn in order to read file called (username).txt
        BufferedReader fileIn = new BufferedReader(new FileReader(username + ".txt"));
        String line;
        int i = 0;

        //While loop reads through all the files of the storage file until end of file or match is found
        while ((line = fileIn.readLine()) != null) {
            //Checks if password was found in the file
            if (password.equals(line)) {
                fileIn.close(); //closes file
                return 1; //return 1 if match found
            }
        }
        fileIn.close(); //closes file
        return i; //return 0 if match was not found
    }

    /**
     * This method takes username, password as inputs to check for account information from the loginInfo.txt file using
     * format username#password using the BufferredReader object and FileReader.
     * @param username - for checking username of the account supplied by the client
     * @param password - for storing password of the account supplied by the client
     * @return 0 if username and password doesn't match, 2 if username matches only, 1 if both username and password match
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public int checkLogin(String username, String password) throws IOException {
        // Generates and initializes an object called fileIn in order to read file called loginInfo.txt
        BufferedReader fileIn = new BufferedReader(new FileReader("loginInfo.txt"));
        String line;
        String delims = "[#]";
        int i = 0;
        //While loops through all of the lines of the file until end of file is reached or match is found
        while ((line = fileIn.readLine()) != null) {
            String[] temp = line.split(delims); //splits the line into two one for username other for password
            //Checks if username matches the username part of the lines
            if (username.equals(temp[0])) {
                //Checks if password matches the password part of the line for matches username
                if (password.matches(temp[1])) {
                    fileIn.close(); //closes file
                    return 1; //return 1 as both username and password matched
                } else {
                    System.out.println(temp[1] + "Given " + password);
                    fileIn.close(); //closes file
                    return 2; //return 2 as only the username matched
                }
            }
        }
        fileIn.close(); //closes file
        return i; //returns 0 as no match found
    }

    /**
     * This listPass method takes the user as input for the username of the account to read the content of user.txt file
     * and sends the resulting string that is generated by appending each line with |
     * @param user - for finding the accounts password storage file with user.txt
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public String listPass(String user) throws IOException {

        // Generates and initializes an object called fileIn in order to read file called (username).txt
        BufferedReader fileIn = new BufferedReader(new FileReader(user + ".txt"));
        String result = user;
        String line;
        //While loop through each of the lines in the file until end of file is reached
        while ((line = fileIn.readLine()) != null) {
            result = result + "|" + line; //appends each line of the file to the string with |
        }
        //Sends the string created of all stored password to the client using the writeToClient stream
        fileIn.close(); //closes file
        return result;
    }

    /**
     * This checkPassword method is responsible for check for the strength of the provided password through two methods.
     * One that calculates how many lowercase, uppercase, numbers, and special characters are present in the password
     * while the other method utilizes a commnPass.txt file to check for any patterns or parts of the password that are
     * found in the commonly used words/password by password crackers,
     * @param password - for checking if password is weak and/or matches with a large part of the common passwords
     * @return a string type message that is then sent to the client
     * @throws IOException - for catching any exceptions caused by open, writing, closing files
     */
    public String checkPassword(String password) throws IOException {

        //Generates and initializes variables to storing count of different types of characters and for pattern matching
        int low = 0, upp = 0, dig = 0, spe = 0;
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digit = "0123456789";
        char[] passArray = password.toCharArray(); //Converts the password input from string to a char[] array

        //Iterates through all the characters of the char array
        for (char c: passArray) {
            //Checks if the char was lowercase, uppercase, digit, or special and increment the count for them
            if (lower.contains(Character.toString(c))) {
                low++;
            } else if (upper.contains(Character.toString(c))) {
                upp++;
            } else if (digit.contains(Character.toString(c))) {
                dig++;
            } else {
                spe++;
            }
        }

        // Generates and initializes an object called fileIn in order to read file called commonPass.txt
        BufferedReader fileIn = new BufferedReader(new FileReader("commonPass.txt"));
        String result = password;
        String line;
        // Checks if the password length is less or equal to 5
        if (passArray.length <= 5) {
            return "0|Password is extremely insecure. " + "Recommendation: Password Length should be greater than 5";
        } else {
            // While loops through all of the lines of the commonPass.txt file until end is reached or match is found
            while ((line = fileIn.readLine()) != null) {
                // Tries to calculate the percentage of the match and pattern of the password for each line of the file
                try {
                    double containsPercent = (double) result.length() / (double) line.length() * 100;
                    double containsCommon = (double)line.length() / (double)result.length() * 100;
                    // Checks if an exact match is found, or high percent match is found, or most of the pattern is found
                    if (line.equals(result)) {
                        return "0|Extremely Insecure: Exact match found! " + Double.toString(containsPercent) + "%";
                    } else if ((line.contains(result) & (containsPercent >= 90)) | (result.contains(line) & (containsCommon  >= 90))) {
                        return "25|Very Insecure: Match found! Password -> Match: " + Double.toString(containsPercent) +
                                "% & Match -> Password: " + containsCommon + "%";
                    } else if ((line.contains(result) & (containsPercent >= 70)) | (result.contains(line) & (containsCommon >= 70))) {
                        return "50|Insecure: Match found! Password -> Match: " + Double.toString(containsPercent) +
                                "% & Match -> Password: " + containsCommon + "%";
                    }
                    // catches for Arithmetic exceptions that are thrown by the try block
                } catch (ArithmeticException arex) {
                    System.out.print(arex); //prints the exception to the terminal
                }
            }
            fileIn.close(); //closes the file
        }
        // Checks for different associated counts for security in order to return a corresponding message
        if (low >= 2 & upp >= 1 & dig >= 2 & spe >= 1) {
            return "100|Password is very secure. " + "Recommendation: Increase password length to improve" +
                    " security";
        } else if (low >= 2 & upp >= 1 & dig >= 2) {
            return "75|Password is secure. " + "Recommendation: Password strength can be improved by " +
                    "adding Special Characters";
        } else if ((low >= 4 & upp >= 1) | (low >= 4 & dig >= 1) | (upp >= 1 & dig >= 1)) {
            return "25|Password is insecure. " + "Recommendation: Password should contain the " +
                    "combination of lowercase, uppercase, and numbers";
        } else if ((low >= 5 | upp >= 5) | dig >= 5 | spe >= 5) {
            return "25|Password is insecure. " + "Recommendation: Password should contain the " +
                    "combination of lowercase, uppercase, and numbers";
        } else {
            return "50|Password is Average. " + "Recommendation: Password Length should be increased including " +
                    "the count for uppercase and digits";
        }
    }

    /**
     * The method for the client to download files.
     * @param file A file object, as listed in the server's file directory. Thankfully, the objects are captured perfectly for the client.
     * @throws RemoteException As with all remote object methods, it throws a special type of exception when the registry is not available.
     * @return Returns a byte array that contains all of the data of the file. Does not support large files.
     */
    public byte[] downloadFile(CFile file, String username) throws RemoteException
    {
        // Code to download file here. Create a buffer
        try
        {

            byte buffer[] = new byte[(int) file.size];
            int count;
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file.path));
            input.read(buffer, 0, buffer.length);
            input.close();
            System.out.println("File is: " + file.name + " and has size: " + file.size + " bytes.");
            return (buffer);

        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
        }
        return null;
    }
    /**
     * The method for getting a list of all available files.
     * @throws RemoteException As with all remote object methods, it throws a special type of exception when the registry is not available.
     * @return Returns a String of all the file names, separated by a new line character.
     */
    public ArrayList<CFile> getListOfFiles(String username) throws RemoteException
    {
        // Code to get list of accessible files.
        ArrayList<CFile> fileList = new ArrayList<CFile>();
        try
        {
            BufferedReader fileIn = new BufferedReader(new FileReader("fileDB.txt"));
            String line;
            String delims = "\\|";
            // While loops reads through all the lines of the file and splits each line into parts using delims #
            while (true)
            {
                // Iterate over fileDB.txt until EOF. Collects information inside and builds a CFile object.
                line = fileIn.readLine();
                System.out.println(line);
                if (line == null)
                {
                    break;
                }
                else
                {
                    String[] temp = line.split(delims); //splits the line into part
                    System.out.println(Arrays.toString(temp));
                    CFile file = new CFile();
                    System.out.println(temp[0] + temp[1]);
                    file.name = temp[0];
                    file.size = Long.parseLong(temp[1]);
                    file.group = temp[2];
                    file.owner = temp[3];
                    file.path = temp[4];
                    file.isPrivate = Boolean.parseBoolean(temp[5]);
                    if (checkPermissions(username, file))
                    {
                        fileList.add(file);
                    }
                }
            }
            fileIn.close();// close file
            return fileList;
        }

        catch(Exception e)
        {
            System.out.println("Server: "+e.getMessage());
            e.printStackTrace();
            return(null);
        }
    }

    public ArrayList<String> getListOfUsers() throws RemoteException
    {
        // Code to get list of accessible files.
        ArrayList<String> userList = new ArrayList<String>();
        try
        {
            BufferedReader fileIn = new BufferedReader(new FileReader("loginInfo.txt"));
            String line;
            String delims = "[#]";
            // While loops reads through all the lines of the file and splits each line into parts using delims #
            while (true)
            {
                // Iterate over fileDB.txt until EOF. Collects information inside and builds a CFile object.
                line = fileIn.readLine();
                System.out.println(line);
                if (line == null)
                {
                    break;
                }
                else
                {
                    String[] temp = line.split(delims); //splits the line into part
                    System.out.println(Arrays.toString(temp));
                    String user = new String();
                    System.out.println(temp[0] + temp[1]);
                    user = temp[0];
                    userList.add(user);
                }
            }
            fileIn.close();// close file
            return userList;
        }

        catch(Exception e)
        {
            System.out.println("Server: "+e.getMessage());
            e.printStackTrace();
            return(null);
        }
    }

    /**
     * The method for uploading a file to the server.
     * @param file A byte array containing the data of the file. Doesn't work on large files.
     * @throws RemoteException As with all remote object methods, it throws a special type of exception when the registry is not available.
     */
    public void uploadFile(byte[] data, CFile file) throws RemoteException
    {
        try
        {
            System.out.println("Received file: " + file.name);
            if (file.group == null)
            {
                file.group = file.owner;
            }
            // Make the directories if they do not already exist.
            file.path = "Files\\" + file.group + "\\";
            new File(file.path).mkdirs();

            // Finish the full path, write the file, and add a new file into the file database.
            file.path = file.path + file.name;
            File newfile = new File(file.path);

            // Create the file on the server side in the Files folder.
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.path));
            output.write(data,0, data.length);
            output.close();
            System.out.println("file: "+ name + " has been created and has size: " + file.size + " bytes.");
            fileDB.createNewFile(); // Create file if it doesn't exist

            // Write the file details to fileDB.txt. Steals navjot's formating idea.
            BufferedWriter fileOut = new BufferedWriter(new FileWriter("fileDB.txt", true));
            fileOut.append(file.name);
            fileOut.append("|" + Long.toString(file.size));
            fileOut.append("|" + file.group);
            fileOut.append("|" + file.owner);
            fileOut.append("|" + file.path);
            fileOut.append("|" + Boolean.toString(file.isPrivate));
            fileOut.newLine();
            fileOut.close();

        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
        }
    }

    private boolean checkPermissions(String username, CFile file)
    {
        // W.I.P. Stub that always grants permission.

        if (file.owner == username)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
