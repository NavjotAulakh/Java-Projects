package ca.uoit.projectxiphos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class ShowPassword extends AppCompatActivity {

    SimpleCursorAdapter adaptr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_password);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        ListView lv =(ListView) findViewById(R.id.listView);
        List<String> data = MainActivity.mySQLDBHelper.getPassData();
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data));

    }

    protected void cancel(View view) {

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
