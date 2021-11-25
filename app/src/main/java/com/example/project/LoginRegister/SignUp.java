package com.example.project.LoginRegister;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText textInputEditTextFullName,textInputEditTextAge, textInputEditTextPassword, textInputEditTextEmail;
    private Button buttonSignUp;
    private TextView textViewLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewLogin = (TextView) findViewById(R.id.loginTextxml);
        textViewLogin.setOnClickListener(this);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUpxml);
        buttonSignUp.setOnClickListener(this);

        textInputEditTextFullName = (EditText) findViewById(R.id.fullnamexml);
        textInputEditTextAge = (EditText) findViewById(R.id.agexml);
        textInputEditTextPassword = (EditText) findViewById(R.id.passwordxml);
        textInputEditTextEmail = (EditText) findViewById(R.id.emailxml);
        progressBar = (ProgressBar) findViewById(R.id.progressxml);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginTextxml:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.buttonSignUpxml:
                buttonSignUp();
                break;
        }

    }

    private void buttonSignUp() {
        String fullName = textInputEditTextFullName.getText().toString().trim();
        String age = textInputEditTextAge.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();
        String email = textInputEditTextEmail.getText().toString().trim();

        if (fullName.isEmpty()){
            textInputEditTextFullName.setError("Full name is required!");
            textInputEditTextFullName.requestFocus();
            return;
        }
        if (age.isEmpty()){
            textInputEditTextAge.setError("Age is required!");
            textInputEditTextAge.requestFocus();
            return;
        }
        if (password.isEmpty()){
            textInputEditTextPassword.setError("Password is required!");
            textInputEditTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            textInputEditTextPassword.setError("Min password length should be 6 characters!");
            textInputEditTextPassword.requestFocus();
            return;
        }
        if (email.isEmpty()){
            textInputEditTextEmail.setError("Email is required!");
            textInputEditTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textInputEditTextEmail.setError("Please provide valid email");
            textInputEditTextEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(fullName, age, email);
                        db.collection("Users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(SignUp.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(SignUp.this, Login.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUp.this, "Failed to register! Try Again", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                    }

                });
    }
}