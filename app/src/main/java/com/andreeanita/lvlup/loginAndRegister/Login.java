package com.andreeanita.lvlup.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andreeanita.lvlup.Database.DatabaseHelper;
import com.andreeanita.lvlup.R;
import com.andreeanita.lvlup.gpsTracking.GPSActivity;
import com.andreeanita.lvlup.gpsTracking.MapsActivity;

import java.io.Serializable;

public class Login extends AppCompatActivity {
    TextView register;
    EditText etEmail, etPassword;
    Button login;
    static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DatabaseHelper databaseHelper;

        databaseHelper = new DatabaseHelper(this);

        etEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        etPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        login = (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                Boolean checklogin = databaseHelper.CheckLogin(email, password);
                if (checklogin == true) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                    /*Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    Intent intent = new Intent(Login.this, MapsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);*/

                    openGPSActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register = (TextView) findViewById(R.id.textViewRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        //runtime error
        /*Intent intent = new Intent(Login.this, MapsActivity.class);
        String emailLogin=null;
        intent.putExtra( "emailLogin", etEmail.getText().toString());
        startActivity(intent);*/
    }

    public void openRegister() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void openGPSActivity() {
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
    }


}