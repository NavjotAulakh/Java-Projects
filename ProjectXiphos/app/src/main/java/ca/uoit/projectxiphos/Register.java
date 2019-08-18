package ca.uoit.projectxiphos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Register extends AppCompatActivity {

    static UserDBHandler mySQLDBHelper;
    EditText nameR;
    EditText passR;
    EditText emailR;
    EditText firstName;
    EditText lastName;
    CheckBox checkBox;

    Button regB;
    Button cancel;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        nameR = (EditText) findViewById(R.id.userR);
        passR = (EditText) findViewById(R.id.passR);
        emailR = (EditText) findViewById(R.id.emailR);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    protected void registerUser(View view){
        int checkB = 0;
        if (checkBox.isSelected()) {
            checkB = 1;
        }

        User user = new User(nameR.getText().toString(), generatePassHash(passR.getText().toString()),
                emailR.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), checkB);

        boolean userCheck = MainActivity.mySQLDBHelper.checkUser(user);

        if (userCheck == false){
            MainActivity.mySQLDBHelper.addUser(user);
            Context context = getApplicationContext();
            CharSequence text = "User has been Registered!";
            int duration = Toast.LENGTH_SHORT;

            try
            {
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            catch (Exception e)
            {

            }

        } else {
            Context context = getApplicationContext();
            CharSequence text = "User Already Exists! Please Use Another UserName";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        onBackPressed();
    }

    protected void cancel(View view) {
        nameR.setText("");
        passR.setText("");
        emailR.setText("");
        onBackPressed();
    }

    /**
     * This method generates the MD5 hash equivalent of the provided password
     *
     * @param passInput - input for generating MD5 hash as a String
     * @return return the generated MD5 hash as a String
     */
    public String generatePassHash(String passInput) {
        //tries to generate the hash value using the MessageDigest object
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(passInput.getBytes(),0,passInput.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
