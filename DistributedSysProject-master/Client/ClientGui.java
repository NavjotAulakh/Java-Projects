
import javax.crypto.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.NotBoundException;
import java.net.*;
import javax.swing.border.TitledBorder;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.lang.String;
import java.util.Base64;

/**
 * The ClientGui program implements the client side functionality using a ClientGui class responsible for user functionality
 * and ClientGui.form file for building a swing awt format GUI platform for the client to utilize the features. This is
 * done using the provided forms_rt.jar which is providing the GridLayoutDesigner methods for utilizing .form files.
 *
 * @author Navjot Aulakh 100488741
 * @version 2.0
 * @title TruePass ClientGui
 * @date Oct, 25, 2018
 */
public class ClientGui {

    private static final int AES_Key_Size = 128;
    //Initializes and generates the required variables for Swing, awt and ClientGUI
    private JPanel panel1;
    private JTextField clientInput;
    private JButton fileDownload;
    private JLabel clientResult;
    private JButton fileUpload;
    private JFormattedTextField fileV;
    private JFormattedTextField groupID;
    private JLabel title;
    private JFormattedTextField username;
    private JPasswordField loginPass;
    private JButton loginButton;
    private JButton registerButton;
    private JButton logoutButton;
    private JList passList;
    private DefaultListModel listModel = new DefaultListModel();
    private DefaultListModel gModel = new DefaultListModel();
    private JButton listFiles;
    private JLabel listLabel;
    private JLabel loginLabel;
    private JLabel groupLabel;
    private JButton closeFileList;
    private JProgressBar strengthBar;
    private JLabel userL;
    private JLabel passL;
    private JLabel groupNameL;
    private JRadioButton privateRadioButton;
    private JRadioButton groupRadioButton;
    private JButton closeGroupList;
    private JList groupList;
    private JLabel groupListLabel;
    private JButton createGroup;
    private JButton viewGroup;
    private JButton addUser;
    private JButton createKey;
    private JLabel groupMsg;
    private ArrayList<CFile> fileList;
    private ArrayList<String> gList;
    private SecretKeySpec aeskeySpec;

    /**
     * The ClientGUI constructor that initializes all actionListeners for the GUI and their functionality
     */
    public ClientGui() {
        //tries to establish connect with the server
        try {

            //Generate and initialize object and variable required for the connection to the server
            FileInterface FileI = (FileInterface) Naming.lookup("//localhost/fileServer");
            System.out.println("Connected with fileServer"); //print successful message
            final String[] cUser = {""};
            final String[] selectedFile = {""};
            final String[] selectedGroup = {""};
            logoutButton.setVisible(false); //makes button invisible

            createGroup.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // Get group name
                    String group = groupID.getText();
                    String msg = fileV.getText();
                    if (groupID.getText().equals("") | fileV.getText().equals("")) {
                        clientResult.setText("Group name or msg not entered!");
                    } else {
                        try {
                            FileI.createGroup(msg, group);
                            FileI.addUserToGroup(cUser[0], group); // This doesn't work, need a way to add the CURRENT USER to the group.
                        } catch (Exception e) {
                            System.out.println("Exception when creating group:" + e);
                        }
                    }
                }
            });

            groupList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent arg0) {
                    if (!arg0.getValueIsAdjusting()) {
                        if (!groupList.isSelectionEmpty()) {

                            selectedGroup[0] = (groupList.getSelectedValue().toString());
                            System.out.println(selectedFile[0]);
                        }
                    }
                }
            });


            //Action listener for the list stored passwords button
            viewGroup.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //tries to list the password stored server-side in a JList for the user
                    try {
                        //output.println("listFiles|");
                        gList = FileI.getGroups(cUser[0]);
                        System.out.print(gList.toString());
                        //String delims = "[|]";
                        //System.out.println(serverResponse);
                        //String[] passwords = serverResponse.split(delims);
                        groupList.clearSelection();
                        gModel.clear(); //clear the previous listModel
                        int first = 0;
                        //Iterate through the password array to add them into the listt model
                        for (String val : gList) {
                            if (first == 0) {
                                selectedGroup[0] = val;
                                first++;
                            }
                            gModel.addElement(val);
                        }

                        first = 0;
                        gModel.trimToSize(); //trims list model
                        groupList.setModel(gModel); //set passList(JList) to the listModel to display all the passwords
                        groupList.setVisible(true);
                        listLabel.setVisible(true);
                        fileUpload.setVisible(true);
                        closeFileList.setVisible(true);
                        clientResult.setText("Stored Files Retrieved"); //set client result field
                    } catch (IOException e) { //catch for ioexception
                        e.printStackTrace();
                    } catch (NullPointerException nex) { //catch for nullpointer exception
                        nex.printStackTrace();
                    }
                }
            });

            addUser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (groupID.getText().equals("") | fileV.getText().equals("")) {
                        clientResult.setText("Group name or msg not entered!");
                    } else {
                        try {
                            FileI.addUserToGroup(fileV.getText(), groupID.getText());
                        } catch (Exception e) {
                            System.out.println("Exception when adding a user to gropu:" + e);
                        }
                    }
                }
            });

            createKey.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    try {
                        String msg = FileI.getGroupMsg(cUser[0]);
                        clientResult.setText(msg);
                        String pass = FileI.generatePassHash(groupID.getText());
                        byte[] decodedKey = Base64.getDecoder().decode(pass);
                        // rebuild key using SecretKeySpec
                        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                        byte[] bKey = aesKey.getEncoded();
                        File fs = new File(selectedGroup[0] + "-GroupKey.txt");
                        fs.createNewFile();
                        saveKey(fs, bKey);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            //ActionList for the password Strength checker button
            fileDownload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Checks if password is not supplied
                    if (selectedFile[0].equals(null)) {
                        clientResult.setText("No File has been Selected!"); //set result message
                    } else {
                        //tries to check strength by messaging to the server and then setting response as result
                        try {
                            //Generates and initializes variable used for checking password strength
                            CFile cf = new CFile();
                            for (CFile val : fileList) {
                                if (val.name.equals(selectedFile[0])) {
                                    cf = val;
                                }
                            }
                            byte buffer[] = new byte[(int) cf.size];
                            String path = "Files\\" + cf.name;
                            new File("Files\\").mkdirs();
                            File newFile = new File(path);
                            newFile.createNewFile();
                            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(newFile, false));
                            buffer = FileI.downloadFile(cf, cUser[0]);
                            output.write(buffer, 0, buffer.length);
                            output.close();
                            SecureFile sFile = new SecureFile("AES/ECB/PKCS5Padding", path);
                            String response = sFile.decryptFile(cUser[0], path);
                            Files.deleteIfExists(Paths.get(path));
                            System.out.println(response);
                        } catch (Exception e) { //catches if any ioexception thrown by try block
                            e.printStackTrace();
                        }
                    }
                }
            });

            // Action listener for the register button
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Check if username or password is not enterned
                    if (username.getText().equals("") | loginPass.getText().equals("")) {
                        clientResult.setText("Username or Password not entered!");
                    } else {
                        //Tries to register account by sending username and password to the client
                        try {
                            //Generate and initialize variables required for register
                            String userInput = username.getText();
                            String passInput = FileI.generatePassHash(loginPass.getText());
                            System.out.println("register|" + userInput + "|" + passInput);
                            //output.println("register|" + userInput + "|" + passInput);
                            String serverResponse = FileI.register(userInput, passInput);

                            clientResult.setText(serverResponse);
                            System.out.println("Server: " + serverResponse);
                            if (serverResponse.equals("Registered Account")) {
                                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                                kgen.init(AES_Key_Size);
                                SecretKey key = kgen.generateKey();
                                byte[] aesKey = key.getEncoded();
                                File fs = new File(userInput + "-PrivateKey.txt");
                                fs.createNewFile();
                                saveKey(fs, aesKey);
                            } else {
                                System.out.println("Server: " + serverResponse);
                            }

                        } catch (IOException | GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            // Action listener for the login button
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // Checks if username or password is not entered
                    if (username.getText().equals("") | loginPass.getText().equals("")) {
                        clientResult.setText("Username or Password not entered!");
                    } else {
                        // Tries to login into the account
                        try {
                            // Generate and initialize variables required for login
                            String userInput = username.getText();
                            cUser[0] = userInput;
                            String passInput = FileI.generatePassHash(loginPass.getText()); //generate hash for password
                            System.out.println("login|" + userInput + "|" + passInput);
                            //output.println("login|" + userInput + "|" + passInput);
                            String serverResponse = FileI.login(userInput, passInput);
                            clientResult.setText(serverResponse);
                            System.out.println("Server: " + serverResponse);
                            // Checks if server response has Welcome in order to setup login UI for user
                            if (serverResponse.contains("Welcome")) {
                                loginLabel.setVisible(false);
                                loginButton.setVisible(false);
                                registerButton.setVisible(false);
                                logoutButton.setVisible(true);
                                fileUpload.setVisible(true);
                                listFiles.setVisible(true);
                                username.setVisible(false);
                                userL.setVisible(false);
                                loginPass.setVisible(false);
                                passL.setVisible(false);
                                groupNameL.setVisible(true);
                                groupID.setVisible(true);
                                fileDownload.setVisible(true);
                                privateRadioButton.setVisible(true);
                                groupRadioButton.setVisible(true);
                                groupMsg.setVisible(true);
                                fileV.setVisible(true);
                            }
                        } catch (IOException e) { //catch for ioexception
                            e.printStackTrace();
                        }
                    }
                }
            });

            groupRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Disable private
                    privateRadioButton.setSelected(false);
                    groupRadioButton.setSelected(true);
                }
            });

            privateRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Disable private
                    groupRadioButton.setSelected(false);
                    privateRadioButton.setSelected(true);
                }
            });

            //Action listener for the logout button
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Setup logout out UI for user
                    logoutButton.setVisible(false);
                    fileUpload.setVisible(false);
                    listFiles.setVisible(false);
                    passList.setVisible(false);
                    listLabel.setVisible(false);
                    closeFileList.setVisible(false);
                    loginButton.setVisible(true);
                    registerButton.setVisible(true);
                    username.setVisible(true);
                    userL.setVisible(true);
                    loginPass.setVisible(true);
                    passL.setVisible(true);
                    groupNameL.setVisible(false);
                    groupID.setVisible(false);
                    fileDownload.setVisible(false);
                    privateRadioButton.setVisible(false);
                    groupRadioButton.setVisible(false);
                    groupMsg.setVisible(false);
                    fileV.setVisible(false);
                    loginLabel.setVisible(false);
                    //output.println("logout|");
                    //tries to set the server response into the result textfield
                    try {
                        clientResult.setText(FileI.logout() + cUser[0]);
                        cUser[0] = "";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //Action listener for store password button
            fileUpload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // Checks if a password was entered in the password field for storing
                    FileDialog fd = new FileDialog(new JFrame());
                    fd.setVisible(true);
                    File[] f = fd.getFiles();
                    String filePath = "";
                    if (f.length > 0) {
                        filePath = fd.getFiles()[0].getAbsolutePath();
                        System.out.println(fd.getFiles()[0].getAbsolutePath());
                    }
                    try {
                        SecureFile sFile = new SecureFile("AES/ECB/PKCS5Padding", filePath);
                        String response = sFile.encryptFile(cUser[0], filePath);
                        System.out.println(response);

                        int count;
                        filePath = filePath + ".enc";
                        byte buffer[] = new byte[(int) filePath.length()];
                        BufferedInputStream input = null;
                        input = new BufferedInputStream(new FileInputStream(filePath));
                        input.read(buffer, 0, buffer.length);
                        input.close();
                        File file = new File(filePath);
                        CFile newFile = new CFile();
                        newFile.name = file.getName();
                        newFile.size = file.length();
                        newFile.group = null;
                        newFile.owner = cUser[0];
                        newFile.isPrivate = true;
                        FileI.uploadFile(buffer, newFile);
                        Files.deleteIfExists(Paths.get(filePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*if (fileV.getText().equals("")) {
                        clientResult.setText("Password for storage not entered!");
                    } else {
                        //tries to store the provided password from the field to the server
                        try {
                            String storePassInput = fileV.getText();
                            //joiner to join both inputs
                            StringJoiner joiner = new StringJoiner("                              ");
                            String storeNameInput = groupID.getText();
                            joiner.add(storeNameInput + ": ").add(storePassInput);
                            String storeInput = joiner.toString(); //change to string to invoke method
                            System.out.println(storeInput);
                            //output.println("storeInput|" + storeInput); //tell server to store
                            String serverResponse = FileI.storePassToFile(cUser[0], storeInput);
                            clientResult.setText(serverResponse); //set server response into the result field
                        } catch (IOException e) { //catch for ioexception
                            e.printStackTrace();
                        } catch (NullPointerException nex) { //catch for nullpointerexception
                            nex.printStackTrace();
                        }
                    }*/
                }
            });

            passList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent arg0) {
                    if (!arg0.getValueIsAdjusting()) {
                        if (!passList.isSelectionEmpty()) {
                            selectedFile[0] = (passList.getSelectedValue().toString());
                            System.out.println(selectedFile[0]);
                        }
                    }
                }
            });


            //Action listener for the list stored passwords button
            listFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //tries to list the password stored server-side in a JList for the user
                    try {
                        //output.println("listFiles|");
                        fileList = FileI.getList(cUser[0]);
                        System.out.print(fileList.toString());
                        //String delims = "[|]";
                        //System.out.println(serverResponse);
                        //String[] passwords = serverResponse.split(delims);
                        passList.clearSelection();
                        listModel.clear(); //clear the previous listModel
                        int first = 0;
                        //Iterate through the password array to add them into the listt model
                        for (CFile val : fileList) {
                            if (first == 0) {
                                selectedFile[0] = val.name;
                                first++;
                            }
                            listModel.addElement(val.name);
                        }

                        first = 0;
                        listModel.trimToSize(); //trims list model
                        passList.setModel(listModel); //set passList(JList) to the listModel to display all the passwords
                        passList.setVisible(true);
                        listLabel.setVisible(true);
                        fileUpload.setVisible(true);
                        closeFileList.setVisible(true);
                        clientResult.setText("Stored Files Retrieved"); //set client result field
                    } catch (IOException e) { //catch for ioexception
                        e.printStackTrace();
                    } catch (NullPointerException nex) { //catch for nullpointer exception
                        nex.printStackTrace();
                    }
                }
            });

            //Action listener for closing password list button
            closeFileList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    //Make the list component plus the button invisible
                    passList.setVisible(false);
                    listLabel.setVisible(false);
                    groupLabel.setVisible(false);
                    closeFileList.setVisible(false);
                    passList.removeAll(); //remove all info of the JList called passList
                    listModel.clear(); //clear the listModel
                    clientResult.setText("List Closed"); //set client result field
                    selectedFile[0] = "";
                    passList.clearSelection();
                }
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {// Catch for remote exceptions (RMI)
            System.out.println("IO:" + e.getMessage()); //Prints exception message
        }
    }

    public static void saveKey(File file, byte[] key) throws IOException {
        /* Now store "encoded" somewhere. For example, display the key and
        ask the user to write it down. */
        String output = Base64.getEncoder().withoutPadding().encodeToString(key);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(output);
        // do stuff
        writer.close();
    }

    public static SecretKey loadKey(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        byte[] encoded = Base64.getDecoder().decode(reader.readLine());

        SecretKey aesKey = new SecretKeySpec(encoded, "AES");
        return aesKey;
    }

    /**
     * The main method that is used by the ClientGui class to setup the GUI frame, size, visibility and functionality
     *
     * @param args
     */
    public static void main(String[] args) {

        //Creates the ClientGUI frame, panel, and exit
        JFrame frame = new JFrame("ClientGui");
        //Sets the Content Pane and initiates the ClientGUI class functionality
        frame.setContentPane(new ClientGui().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Sets frame size, location, and makes it visible
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setAutoscrolls(true);
        Font panel2Font = this.$$$getFont$$$("Fira Code Medium", Font.PLAIN, 24, panel2.getFont());
        if (panel2Font != null) panel2.setFont(panel2Font);
        panel2.setInheritsPopupMenu(true);
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Distributed Secure File Storage System", TitledBorder.CENTER, TitledBorder.TOP, this.$$$getFont$$$(null, -1, -1, panel2.getFont()), new Color(-16776248)));
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(16, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setAutoscrolls(false);
        panel1.setBackground(new Color(-855310));
        panel1.setEnabled(true);
        Font panel1Font = this.$$$getFont$$$("Courier New", Font.PLAIN, 12, panel1.getFont());
        if (panel1Font != null) panel1.setFont(panel1Font);
        panel1.setForeground(new Color(-3750202));
        panel1.setInheritsPopupMenu(false);
        panel1.setOpaque(true);
        panel1.setVisible(true);
        panel2.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 600), new Dimension(-1, 800), 0, true));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        fileDownload = new JButton();
        fileDownload.setBackground(new Color(-16777216));
        Font fileDownloadFont = this.$$$getFont$$$(null, -1, 14, fileDownload.getFont());
        if (fileDownloadFont != null) fileDownload.setFont(fileDownloadFont);
        fileDownload.setForeground(new Color(-855310));
        fileDownload.setText("DOWNLOAD");
        fileDownload.setVisible(false);
        panel1.add(fileDownload, new com.intellij.uiDesigner.core.GridConstraints(12, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(238, 55), null, 0, false));
        fileV = new JFormattedTextField();
        Font fileVFont = this.$$$getFont$$$(null, -1, 12, fileV.getFont());
        if (fileVFont != null) fileV.setFont(fileVFont);
        fileV.setHorizontalAlignment(0);
        fileV.setToolTipText("Enter group permission password to recieve Group-PublicKey");
        fileV.setVerifyInputWhenFocusTarget(false);
        fileV.setVisible(false);
        panel1.add(fileV, new com.intellij.uiDesigner.core.GridConstraints(9, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        username = new JFormattedTextField();
        username.setHorizontalAlignment(0);
        username.setText("");
        username.setToolTipText("Enter your username here");
        panel1.add(username, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        loginPass = new JPasswordField();
        Font loginPassFont = this.$$$getFont$$$(null, -1, 14, loginPass.getFont());
        if (loginPassFont != null) loginPass.setFont(loginPassFont);
        loginPass.setHorizontalAlignment(0);
        loginPass.setToolTipText("Enter your password here");
        panel1.add(loginPass, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        loginButton = new JButton();
        loginButton.setBackground(new Color(-16777216));
        loginButton.setFocusable(false);
        Font loginButtonFont = this.$$$getFont$$$(null, -1, 14, loginButton.getFont());
        if (loginButtonFont != null) loginButton.setFont(loginButtonFont);
        loginButton.setForeground(new Color(-855310));
        loginButton.setText("Login");
        panel1.add(loginButton, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loginLabel = new JLabel();
        Font loginLabelFont = this.$$$getFont$$$("Consolas", Font.BOLD, 16, loginLabel.getFont());
        if (loginLabelFont != null) loginLabel.setFont(loginLabelFont);
        loginLabel.setForeground(new Color(-16776248));
        loginLabel.setHorizontalAlignment(0);
        loginLabel.setHorizontalTextPosition(0);
        loginLabel.setText("Login in to your account Or Register if you don't have an account");
        panel1.add(loginLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setBackground(new Color(-16777216));
        logoutButton.setEnabled(true);
        Font logoutButtonFont = this.$$$getFont$$$(null, -1, 14, logoutButton.getFont());
        if (logoutButtonFont != null) logoutButton.setFont(logoutButtonFont);
        logoutButton.setForeground(new Color(-855310));
        logoutButton.setText("Logout");
        logoutButton.setVisible(false);
        panel1.add(logoutButton, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientResult = new JLabel();
        Font clientResultFont = this.$$$getFont$$$(null, -1, 14, clientResult.getFont());
        if (clientResultFont != null) clientResult.setFont(clientResultFont);
        clientResult.setForeground(new Color(-16776248));
        clientResult.setHorizontalAlignment(0);
        clientResult.setHorizontalTextPosition(0);
        clientResult.setText("");
        panel1.add(clientResult, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(238, 76), null, 0, false));
        listLabel = new JLabel();
        listLabel.setBackground(new Color(-13619152));
        Font listLabelFont = this.$$$getFont$$$(null, Font.BOLD, 14, listLabel.getFont());
        if (listLabelFont != null) listLabel.setFont(listLabelFont);
        listLabel.setForeground(new Color(-16776248));
        listLabel.setHorizontalAlignment(0);
        listLabel.setHorizontalTextPosition(0);
        listLabel.setInheritsPopupMenu(false);
        listLabel.setText("Stored Files");
        listLabel.setVisible(true);
        panel1.add(listLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(119, 50), new Dimension(-1, 500), 0, false));
        closeFileList = new JButton();
        closeFileList.setBackground(new Color(-13750738));
        closeFileList.setEnabled(true);
        Font closeFileListFont = this.$$$getFont$$$(null, -1, 14, closeFileList.getFont());
        if (closeFileListFont != null) closeFileList.setFont(closeFileListFont);
        closeFileList.setForeground(new Color(-855310));
        closeFileList.setHideActionText(true);
        closeFileList.setText("Close File List");
        closeFileList.setVisible(true);
        panel1.add(closeFileList, new com.intellij.uiDesigner.core.GridConstraints(15, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strengthBar = new JProgressBar();
        strengthBar.setBackground(new Color(-855310));
        strengthBar.setBorderPainted(false);
        strengthBar.setEnabled(true);
        Font strengthBarFont = this.$$$getFont$$$(null, Font.BOLD, 14, strengthBar.getFont());
        if (strengthBarFont != null) strengthBar.setFont(strengthBarFont);
        strengthBar.setForeground(new Color(-16776248));
        strengthBar.setStringPainted(true);
        strengthBar.setVisible(false);
        panel1.add(strengthBar, new com.intellij.uiDesigner.core.GridConstraints(14, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupLabel = new JLabel();
        groupLabel.setBackground(new Color(-13750738));
        Font groupLabelFont = this.$$$getFont$$$(null, -1, 16, groupLabel.getFont());
        if (groupLabelFont != null) groupLabel.setFont(groupLabelFont);
        groupLabel.setForeground(new Color(-16776248));
        groupLabel.setText("Enter File Name to Download/Upload");
        groupLabel.setVisible(false);
        panel1.add(groupLabel, new com.intellij.uiDesigner.core.GridConstraints(8, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passList = new JList();
        passList.setAutoscrolls(true);
        passList.setBackground(new Color(-855310));
        passList.setEnabled(true);
        passList.setForeground(new Color(-16776248));
        passList.setInheritsPopupMenu(false);
        passList.setLayoutOrientation(0);
        passList.setOpaque(true);
        passList.setSelectionMode(0);
        passList.setVisible(true);
        panel1.add(passList, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 14, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 50), null, 0, false));
        title = new JLabel();
        title.setAutoscrolls(false);
        title.setBackground(new Color(-13750738));
        title.setDoubleBuffered(true);
        Font titleFont = this.$$$getFont$$$("Arial Black", Font.BOLD, 14, title.getFont());
        if (titleFont != null) title.setFont(titleFont);
        title.setForeground(new Color(-16776248));
        title.setHorizontalAlignment(0);
        title.setHorizontalTextPosition(0);
        title.setText("");
        panel1.add(title, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(164, 50), null, 0, false));
        groupID = new JFormattedTextField();
        groupID.setHorizontalAlignment(0);
        groupID.setToolTipText("Enter the name for your password");
        groupID.setVisible(false);
        panel1.add(groupID, new com.intellij.uiDesigner.core.GridConstraints(10, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        userL = new JLabel();
        userL.setForeground(new Color(-16776248));
        userL.setText("Username");
        panel1.add(userL, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passL = new JLabel();
        passL.setForeground(new Color(-16776248));
        passL.setText("Password");
        panel1.add(passL, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupMsg = new JLabel();
        groupMsg.setForeground(new Color(-16776248));
        groupMsg.setText("User/Msg");
        groupMsg.setToolTipText("Enter a group message for the question");
        groupMsg.setVisible(false);
        panel1.add(groupMsg, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        privateRadioButton = new JRadioButton();
        privateRadioButton.setHideActionText(true);
        privateRadioButton.setHorizontalAlignment(0);
        privateRadioButton.setHorizontalTextPosition(4);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");
        privateRadioButton.setVisible(false);
        panel1.add(privateRadioButton, new com.intellij.uiDesigner.core.GridConstraints(11, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupRadioButton = new JRadioButton();
        groupRadioButton.setLabel("Group");
        groupRadioButton.setText("Group");
        groupRadioButton.setVisible(false);
        panel1.add(groupRadioButton, new com.intellij.uiDesigner.core.GridConstraints(11, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registerButton = new JButton();
        registerButton.setBackground(new Color(-16777216));
        registerButton.setEnabled(true);
        Font registerButtonFont = this.$$$getFont$$$(null, -1, 14, registerButton.getFont());
        if (registerButtonFont != null) registerButton.setFont(registerButtonFont);
        registerButton.setForeground(new Color(-855310));
        registerButton.setText("Register");
        panel1.add(registerButton, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listFiles = new JButton();
        listFiles.setBackground(new Color(-16777216));
        listFiles.setEnabled(true);
        Font listFilesFont = this.$$$getFont$$$(null, -1, 14, listFiles.getFont());
        if (listFilesFont != null) listFiles.setFont(listFilesFont);
        listFiles.setForeground(new Color(-855310));
        listFiles.setText("VIEW STORED FILES");
        listFiles.setVisible(false);
        panel1.add(listFiles, new com.intellij.uiDesigner.core.GridConstraints(15, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(110, 55), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        groupNameL = new JLabel();
        groupNameL.setForeground(new Color(-16776248));
        groupNameL.setText("Group Name/Pass");
        groupNameL.setToolTipText("Enter group name or password");
        groupNameL.setVisible(false);
        panel1.add(groupNameL, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileUpload = new JButton();
        fileUpload.setBackground(new Color(-16777216));
        fileUpload.setEnabled(true);
        Font fileUploadFont = this.$$$getFont$$$(null, -1, 14, fileUpload.getFont());
        if (fileUploadFont != null) fileUpload.setFont(fileUploadFont);
        fileUpload.setForeground(new Color(-855310));
        fileUpload.setText("UPLOAD");
        fileUpload.setVisible(false);
        panel1.add(fileUpload, new com.intellij.uiDesigner.core.GridConstraints(13, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(110, 55), null, 0, false));
        groupListLabel = new JLabel();
        groupListLabel.setBackground(new Color(-13619152));
        Font groupListLabelFont = this.$$$getFont$$$(null, Font.BOLD, 14, groupListLabel.getFont());
        if (groupListLabelFont != null) groupListLabel.setFont(groupListLabelFont);
        groupListLabel.setForeground(new Color(-16776248));
        groupListLabel.setHorizontalAlignment(0);
        groupListLabel.setHorizontalTextPosition(0);
        groupListLabel.setInheritsPopupMenu(false);
        groupListLabel.setText("Group Manager");
        groupListLabel.setVisible(true);
        panel1.add(groupListLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(119, 50), new Dimension(-1, 500), 0, false));
        groupList = new JList();
        groupList.setAutoscrolls(true);
        groupList.setBackground(new Color(-855310));
        groupList.setEnabled(true);
        groupList.setForeground(new Color(-16776248));
        groupList.setInheritsPopupMenu(false);
        groupList.setLayoutOrientation(0);
        groupList.setOpaque(true);
        groupList.setSelectionMode(0);
        groupList.setVisible(true);
        panel1.add(groupList, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 9, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 50), null, 0, false));
        closeGroupList = new JButton();
        closeGroupList.setBackground(new Color(-13750738));
        closeGroupList.setEnabled(true);
        Font closeGroupListFont = this.$$$getFont$$$(null, -1, 14, closeGroupList.getFont());
        if (closeGroupListFont != null) closeGroupList.setFont(closeGroupListFont);
        closeGroupList.setForeground(new Color(-855310));
        closeGroupList.setHideActionText(true);
        closeGroupList.setText("Close Manager");
        closeGroupList.setVisible(true);
        panel1.add(closeGroupList, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewGroup = new JButton();
        viewGroup.setBackground(new Color(-13750738));
        viewGroup.setEnabled(true);
        Font viewGroupFont = this.$$$getFont$$$(null, -1, 14, viewGroup.getFont());
        if (viewGroupFont != null) viewGroup.setFont(viewGroupFont);
        viewGroup.setForeground(new Color(-855310));
        viewGroup.setHideActionText(true);
        viewGroup.setText("View Groups");
        viewGroup.setVisible(true);
        panel1.add(viewGroup, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addUser = new JButton();
        addUser.setBackground(new Color(-13750738));
        addUser.setEnabled(true);
        Font addUserFont = this.$$$getFont$$$(null, -1, 14, addUser.getFont());
        if (addUserFont != null) addUser.setFont(addUserFont);
        addUser.setForeground(new Color(-855310));
        addUser.setHideActionText(true);
        addUser.setText("Add User");
        addUser.setVisible(true);
        panel1.add(addUser, new com.intellij.uiDesigner.core.GridConstraints(13, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createGroup = new JButton();
        createGroup.setBackground(new Color(-13750738));
        createGroup.setEnabled(true);
        Font createGroupFont = this.$$$getFont$$$(null, -1, 14, createGroup.getFont());
        if (createGroupFont != null) createGroup.setFont(createGroupFont);
        createGroup.setForeground(new Color(-855310));
        createGroup.setHideActionText(true);
        createGroup.setText("Create Group");
        createGroup.setVisible(true);
        panel1.add(createGroup, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createKey = new JButton();
        createKey.setBackground(new Color(-13750738));
        createKey.setEnabled(true);
        Font createKeyFont = this.$$$getFont$$$(null, -1, 14, createKey.getFont());
        if (createKeyFont != null) createKey.setFont(createKeyFont);
        createKey.setForeground(new Color(-855310));
        createKey.setHideActionText(true);
        createKey.setText("Group Key");
        createKey.setVisible(true);
        panel1.add(createKey, new com.intellij.uiDesigner.core.GridConstraints(14, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }
}