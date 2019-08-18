import java.io.*;
import java.io.DataInputStream;
import java.net.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Pattern;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.*;
/**
 * The server program implements the server side functionality using a passConnection class responsible for connecting
 * to clients through a Java RMI implementation through an Interface called FileInterface and the Interface Impl called
 * FileImpl by first creating an FileImpl object and then binding the object to the registry using the provided run method.
 *
 * @author Navjot Aulakh 100488741
 * @title TruePass Server
 * @date Oct, 25, 2018
 * @version 2.0
 */
public class Server {

    /**
     * The main method that creates a Server object, and then runs it
     * @param arg   this is for any arguments received when running java Server arg1 arg2 etc
     * @throws RemoteException  RemoteException in case any of Remote objects or registry were not found
     */
    public static void main(String[] arg) throws RemoteException {

        //System.setSecurityManager(new SecurityManager());
        //Generates a server object
        Server myPassServer = new Server();
        myPassServer.run(); //runs it
    }

    public void run() {

        //Trys to generate a ServerSocket type object and initializes it on port 7896
        try {

            //Creates an object for Interface implementation called passObj and initializes it using PassServer
            FileImpl fileObj = new FileImpl("fileServer");
            //Binds the object to the registry so that any client can access the implementation of the methods
            Naming.rebind("//127.0.0.1/fileServer", fileObj);
            System.out.println("Server bound in registry");

        //Catchs any exceptions thrown by the try block and print it on server terminal
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}