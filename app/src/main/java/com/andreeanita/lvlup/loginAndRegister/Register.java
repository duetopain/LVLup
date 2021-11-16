package com.andreeanita.lvlup.loginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andreeanita.lvlup.Database.DatabaseHelper;
import com.andreeanita.lvlup.R;

public class Register extends AppCompatActivity {
    TextView login;
    Button signUp;
    EditText etName, etEmail, etPassword, etConfirmPassword;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        etName = (EditText) findViewById(R.id.editTextRegisterName);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPassword = (EditText) findViewById(R.id.editTextPassword);
        etConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        signUp = (Button) findViewById(R.id.buttonRegister);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (name.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields Required", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPassword)) {
                        Boolean checkEmail = databaseHelper.CheckEmail(email);
                        if (checkEmail == true) {
                            Boolean insert = databaseHelper.Insert(name, email, password);
                            if (insert == true) {
                                Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                                etName.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                                etConfirmPassword.setText("");
                                openLogin();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        login = (TextView) findViewById(R.id.textViewLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
    }

    public void openLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}