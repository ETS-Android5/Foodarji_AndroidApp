package com.example.project.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.Activity.MainActivity;
import com.example.project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputEditText textInputEditTextEmailLog, textInputEditTextPasswordLog;
    private Button buttonLogin;
    private TextView textViewSignUp, textForgetPassword;
    private ProgressBar progressBar;
    private SensorManager sensorManager;
    private FirebaseAuth mAuth;
    private float acelVal, acelLast, shake; //current-Last-Differ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        mAuth = FirebaseAuth.getInstance();
        textInputEditTextEmailLog = (TextInputEditText) findViewById(R.id.emailLogxml);
        textInputEditTextPasswordLog = (TextInputEditText) findViewById(R.id.passwordxml);
        buttonLogin = (Button) findViewById(R.id.buttonLoginxml);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        textViewSignUp = (TextView) findViewById(R.id.signUpTextxml);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressxml);
        /*textForgetPassword= (TextView) findViewById(R.id.ForgetPasswordTextxml);
        textForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgetPassword.class));
            }
        });*/
        mAuth = FirebaseAuth.getInstance();
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x= event.values[0];
            float y= event.values[1];
            float z= event.values[2];
            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal-acelLast;
            shake = shake*0.9f + delta;
            if (shake>8){
                startActivity(new Intent(Login.this, ForgetPassword.class));
                Toast.makeText(Login.this, "Time to reset Password!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void userLogin() {
        String email = textInputEditTextEmailLog.getText().toString().trim();
        String password = textInputEditTextPasswordLog.getText().toString().trim();

        if (email.isEmpty()){
            textInputEditTextEmailLog.setError("Email is required!");
            textInputEditTextEmailLog.requestFocus();
            return;
        }
        if (password.isEmpty()){
            textInputEditTextPasswordLog.setError("Password is required!");
            textInputEditTextPasswordLog.requestFocus();
            return;
        }
        if (password.length() < 6 ){
            textInputEditTextPasswordLog.setError("Min password length is 6 Characters!");
            textInputEditTextPasswordLog.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()){
                    Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Login.this, MainActivity.class));
                }else {
                    user.sendEmailVerification();
                    Toast.makeText(Login.this, "Check your Email to verify your account!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Failed to login! Please check you credentials", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}