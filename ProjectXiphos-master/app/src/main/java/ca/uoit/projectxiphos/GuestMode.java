package ca.uoit.projectxiphos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GuestMode extends AppCompatActivity {

    EditText passwordField;
    TextView listPasswords;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_mode);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        passwordField = (EditText) findViewById(R.id.passwordField);
        listPasswords = (TextView) findViewById(R.id.listPasswords);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    protected void passwordStrength(View view) throws IOException {

        String password = passwordField.getText().toString();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        File sdcard = Environment.getExternalStorageDirectory();
        BufferedReader fileIn = new BufferedReader(new FileReader(new File("/sdcard/", "commonPass.txt")));
        String result = password;
        String line;
        boolean done = false;
        // Checks if the password length is less or equal to 5
        if (passArray.length <= 5) {
            listPasswords.setText("0|Password is extremely insecure. " + "Recommendation: Password Length should be greater than 5");
            progressBar.setProgress(0);

        } else {
            // While loops through all of the lines of the commonPass.txt file until end is reached or match is found
            while ((line = fileIn.readLine()) != null) {
                // Tries to calculate the percentage of the match and pattern of the password for each line of the file
                try {
                    double containsPercent = (double) result.length() / (double) line.length() * 100;
                    double containsCommon = (double)line.length() / (double)result.length() * 100;
                    // Checks if an exact match is found, or high percent match is found, or most of the pattern is found
                    if (line.equals(result) | containsCommon >= 100) {
                        listPasswords.setText("Extremely Insecure: Exact match found! " + Double.toString(containsPercent) + "%");
                        progressBar.setProgress(0);
                        done = true;
                    } else if ((line.contains(result) & (containsPercent >= 90)) | (result.contains(line) & (containsCommon  >= 90))) {
                        listPasswords.setText("Very Insecure: Match found! Password -> Match: " + Double.toString(containsPercent) +
                                "% & Match -> Password: " + containsCommon + "%");
                        progressBar.setProgress(25);
                        done = true;
                    } else if ((line.contains(result) & (containsPercent >= 70)) | (result.contains(line) & (containsCommon >= 70))) {
                        listPasswords.setText("Insecure: Match found! Password -> Match: " + Double.toString(containsPercent) +
                                "% & Match -> Password: " + containsCommon + "%");
                        progressBar.setProgress(25);
                        done = true;
                    }
                    // catches for Arithmetic exceptions that are thrown by the try block
                } catch (ArithmeticException arex) {
                    System.out.print(arex); //prints the exception to the terminal
                }
            }
            fileIn.close(); //closes the file
        }
        if (done == false) {
            // Checks for different associated counts for security in order to return a corresponding message
            if (low >= 2 & upp >= 1 & dig >= 2 & spe >= 1) {
                listPasswords.setText("Password is very secure. " + "Recommendation: Increase password length to improve" +
                        " security");
                progressBar.setProgress(100);
            } else if (low >= 2 & upp >= 1 & dig >= 2) {
                listPasswords.setText("Password is secure. " + "Recommendation: Password strength can be improved by " +
                        "adding Special Characters");
                progressBar.setProgress(75);
            } else if ((low >= 4 & upp >= 1) | (low >= 4 & dig >= 1) | (upp >= 1 & dig >= 1)) {
                listPasswords.setText("Password is insecure. " + "Recommendation: Password should contain the " +
                        "combination of lowercase, uppercase, and numbers");
                progressBar.setProgress(25);
            } else if ((low >= 5 | upp >= 5) | dig >= 5 | spe >= 5) {
                listPasswords.setText("Password is insecure. " + "Recommendation: Password should contain the " +
                        "combination of lowercase, uppercase, and numbers");
                progressBar.setProgress(25);
            } else {
                listPasswords.setText("Password is Average. " + "Recommendation: Password Length should be increased including " +
                        "the count for uppercase and digits");
                progressBar.setProgress(50);
            }
        }
    }

    protected void cancelStr(View view){
        passwordField.setText("");
        onBackPressed();
    }

}
