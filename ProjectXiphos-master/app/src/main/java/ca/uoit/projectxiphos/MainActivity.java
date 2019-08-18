package ca.uoit.projectxiphos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    static UserDBHandler mySQLDBHelper;
    static String loggedUser;
    EditText nameInput;
    EditText passInput;

    Button loginB;
    Button registerB;
    Button themesB;
    Button guestB;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameInput = (EditText) findViewById(R.id.nameInput);
        passInput = (EditText) findViewById(R.id.passInput);
    }

    /**
     Intent the user added to the RegisterActivity
     */
    protected void register(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    /**
    Intent the user added to the RegisterActivity
     */
    protected void guestMode(View view){
        Intent intent = new Intent(this, GuestMode.class);
        startActivity(intent);
    }

    /**
     Intent the user added to the RegisterActivity
     */
    protected void login(View view){
        User logUser = mySQLDBHelper.getUser(nameInput.getText().toString());

        if (logUser == null) {

            Context context = getApplicationContext();
            CharSequence text = "User not found!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (logUser.getPass().equals(generatePassHash(passInput.getText().toString()))) {

            loggedUser = logUser.getName();
            Intent intent = new Intent(this, UserPanel.class);
            intent.putExtra("username", nameInput.getText());
            intent.putExtra("password", passInput.getText());
            startActivity(intent);
        } else {

            Context context = getApplicationContext();
            CharSequence text = "Incorrect Password!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

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
