package ca.uoit.projectxiphos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class StorePassword extends AppCompatActivity {

    TextView user;
    EditText title;
    EditText pass;

    Button storePass;
    Button cancelPass;

    Password password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_password);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        user = (TextView) findViewById(R.id.user);
        title = (EditText) findViewById(R.id.passwordTitle);
        pass = (EditText) findViewById(R.id.passwordInput);

    }

    protected void storePassword(View view){

        Password password = new Password(user.getText().toString(), title.getText().toString(), generatePassHash(pass.getText().toString()));
        boolean passCheck = MainActivity.mySQLDBHelper.checkPassword(password, user.getText().toString());
        if (passCheck == false){
            MainActivity.mySQLDBHelper.addPassword(password);
            Context context = getApplicationContext();
            CharSequence text = "Password has been Stored!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Title Already Exists! Please Use Another Title";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        onBackPressed();
    }

    protected void cancel(View view) {
        user.setText("");
        title.setText("");
        pass.setText("");
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
