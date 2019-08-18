/* Author: Jeffrey Zhang */
import java.io.*;
import java.io.DataInputStream;
import java.net.*;
import java.util.ArrayList;

// Server is multithreaded, so that it can continue to accept clients.
class Server implements Runnable
{
    // Setup static variables that are accessible to all threads.
    static int maxClients = 3;
    static int currentClients = 0;
    Socket client;
    public Server(Socket s)
    {
        client = s;
    }
    public static void main(String[] args)
    {
        try
        {
            // Create server socket, and listen in for connection requests. If found, creates a new thread
            // for handling that client.
            ServerSocket serverSocket = new ServerSocket(38742);
            while(true)
            {
                try
                {
                    if (currentClients < maxClients)
                    {
                        System.out.println("Now accepting new clients. Current Count: " + currentClients);
                        Socket newClient = serverSocket.accept();
                        currentClients++;
                        System.out.println ("New Client, now there is: " + currentClients);

                        (new Thread(new Server(newClient))).start();
                    }
                    else
                    {
                        System.out.println("Full of clients.");
                        Socket newClient = serverSocket.accept();
                        PrintWriter w;
                        BufferedReader r;
                        DataOutputStream clientOut;
                        DataInputStream clientIn;
                        try
                        {
                            w = new PrintWriter(newClient.getOutputStream(), true);
                            clientIn=new DataInputStream(newClient.getInputStream());
                            clientOut = new DataOutputStream(newClient.getOutputStream());
                            r = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
                            w.println("Busy.");
                            newClient.close();
                        }
                        catch (IOException e)
                        {
                            try
                            {
                                System.out.println("Error setting up i/o. Resetting connection.");
                                currentClients--;
                                newClient.close();
                            }
                            catch (IOException ex)
                            {
                                System.out.println("Error while getting socket streams.."+ex);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Exception:" + e);
                    return;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error with creating server socket or something else.");
        }

    }

    public void run()
    {
        PrintWriter w;
        BufferedReader r;
        DataInputStream clientIn;
        DataOutputStream clientOut;
        try
        {
            // Establish I/O streams, and open the resource folder.
            w = new PrintWriter(client.getOutputStream(), true);
            r = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientIn=new DataInputStream(client.getInputStream());
            clientOut = new DataOutputStream(client.getOutputStream());
            FileImpl exec = new FileImpl("FileServer");
            w.println("OK");
            while(true)
            {
                try
                {
                    // Accepts specific protocols 'CTest', 'Request Playlist', and 'Song Request'.
                    String s = r.readLine();
                    if (s.equals("Login"))
                    {
                        String user, pw;
                        user = r.readLine();
                        pw = r.readLine();
                        String msg = exec.login(user, pw);
                        w.println(msg);
                    }
                    else if (s.equals("Register"))
                    {
                        String user, pw, email;
                        user = r.readLine();
                        pw = r.readLine();
                        email = r.readLine();
                        String msg = exec.register(user, pw, email);
                        w.println(msg);
                    }
                    else if (s.equals("getUsers"))
                    {
                        // Get song name from client, fetch file, transmit it.
                        ArrayList<String> listOfUsers = new ArrayList<String>();

                        listOfUsers = exec.getListOfUsers();
                        ObjectOutputStream objOut = new ObjectOutputStream(client.getOutputStream());
                        objOut.writeObject(listOfUsers);

                    }
                    else if  (s.equals("getFiles"))
                    {
                        ArrayList<CFile> listOfFiles = new ArrayList<CFile>();
                        String user = r.readLine();
                        listOfFiles = exec.getListOfFiles(user);
                        ObjectOutputStream objOut = new ObjectOutputStream(client.getOutputStream());
                        objOut.writeObject(listOfFiles);

                    }

                    else if  (s.equals("upload"))
                    {
                        CFile file;
                        int size, count = 0;
                        size =Integer.parseInt(r.readLine());
                        byte[] data = new byte[(int) size];
                        ObjectInputStream dIn = new ObjectInputStream(clientIn);
                        clientIn.read(data, 0, size);
                        file = (CFile) dIn.readObject();
                        exec.uploadFile(data, file);
                    }

                    else if  (s.equals("download"))
                    {
                        CFile file;
                        String username = r.readLine();
                        ObjectInputStream dIn = new ObjectInputStream(clientIn);
                        file = (CFile) dIn.readObject();
                        byte[] buffer = exec.downloadFile( file,  username);
                        clientOut.write(buffer);
                    }
                    else
                    {
                        System.out.println("Invalid Request from Client");
                    }

                }
                catch (Exception e)
                {
                    currentClients--;
                    System.out.println ("Client Disconnected. Remaining: " + currentClients + " Exception: " + e);
                    break;
                }
            }
        }
        catch (IOException e)
        {
            try
            {
                System.out.println("Error setting up i/o. Resetting connection.");
                currentClients--;
                client.close();
            }
            catch (IOException ex)
            {
                System.out.println("Error while getting socket streams.."+ex);
            }
        }

    }
}
