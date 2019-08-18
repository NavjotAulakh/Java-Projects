package ca.uoit.projectxiphos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.w3c.dom.Text;

public class UserPanel extends AppCompatActivity
{

    TextView userName;
    static String username;
    static String password;
    static ArrayList<String> userList = new ArrayList<String>();
    static ArrayList<CFile> fileList = new ArrayList<CFile>();
    LinearLayout scrollable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        userName = (TextView) findViewById(R.id.userLabel);
        scrollable = (LinearLayout) findViewById(R.id.fileList);
        scrollable.removeAllViews();
        Log.d("Test:", "launched userpanel");
        // Setup the temporary storage files and start connection.

        try
        {

        }
        catch (Exception e)
        {
            System.out.println("Unable to establish connection.");
            finish();
        }
    }

    protected void storePassword(View view){
        Intent intent = new Intent(this, StorePassword.class);
        startActivity(intent);
    }

    protected void showPassword(View view){

    }

    protected void logout(View view) {
        userName.setText("");
        onBackPressed();

    }

}


class connectAndGetList extends AsyncTask<String, Void, LinearLayout> {

    private Exception exception;
    private Context context;
    private Socket client;
    private static String username;
    private static String password;
    private LinearLayout scrollable;
    private ArrayList<LinearLayout> fileLayout = new ArrayList<LinearLayout>();
    private ArrayList<TextView> myTextViews = new ArrayList<>();
    private ArrayList<Button> myDLBtns = new ArrayList<Button>();
    private ArrayList<Button> myShareBtns = new ArrayList<Button>();
    static ArrayList<String> userList = new ArrayList<String>();
    static ArrayList<CFile> fileList = new ArrayList<CFile>();
    UserDBHandler dbHandler;

    public connectAndGetList(Context mcontext, String user, String pw) {
        context = mcontext;
        username = user;
        password = pw;
        scrollable = new LinearLayout(context);
        dbHandler = new UserDBHandler(context, null, null, 1);
    }

    protected LinearLayout doInBackground(String... strings) {

        try
        {
            getListOfFiles();
            return scrollable;
        }
        catch (Exception e)
        {
            this.exception = e;
            return scrollable;
        }

    }

    protected LinearLayout onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
        return scrollable;
    }

    void uploadGeneric(Socket client)
    {
        // i is the index of all things.
        CFile fd = new CFile();
        fd.name = "Generic File";
        fd.owner = username;
        fd.size = 10;

        try
        {
            byte[] data = new byte[(int)fd.size];
            dbHandler.addFile(fd);
        }
        catch (Exception e)
        {
            Log.d("Error:", e.toString());
        }
    }

    void getListOfFiles()
    {

        try
        {
            // get ArrayList<CFile> from db
            fileList = dbHandler.getListOfFiles();

            // Put elements into linear layout
            for (int i = 0; i < fileList.size(); i++)
            {
                myTextViews.add(new TextView(context));
                myTextViews.get(i).setText(fileList.get(i).name);
                myDLBtns.add(new Button(context));
                myDLBtns.get(i).setText("Download");
                myDLBtns.get(i).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        // Download the selected file.
                        for (int i = 0; i < fileList.size(); i++)
                        {
                            if (v == fileLayout.get(i).getChildAt(1))
                            {
                                // i is the index of all things.
                                CFile fileDescription = fileList.get(i);
                                downloadFile(fileDescription);
                            }
                        }
                    }
                });

                fileLayout.add(new LinearLayout(context));
                fileLayout.get(i).addView( myTextViews.get(i));
                fileLayout.get(i).addView( myDLBtns.get(i));
                scrollable.addView(fileLayout.get(i));

                //scrollable.addView(); //put horizontal linear layout here with completed setup
                // Also add download buttons and upload buttons.
            }
        }
        catch (Exception e)
        {
            Log.d("Error:", e.toString());
        }

    }

    void downloadFile( CFile fd)
    {

        try
        {
            // Get file from db
            for(int k = 0; k < fileList.size(); k++)
            {
                if (fd == fileList.get(k))
                {
                    // found it. ready to place into storage

                }
            }
        }
        catch (Exception e)
        {
            Log.d("Error:", e.toString());
        }
    }

}