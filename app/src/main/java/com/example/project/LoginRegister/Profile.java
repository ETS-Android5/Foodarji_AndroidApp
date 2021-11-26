package com.example.project.LoginRegister;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project.Activity.IntroActivity;
import com.example.project.Activity.MainActivity;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {
    private FirebaseUser user;
    private CollectionReference reference;
    private String userID;

    Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseFirestore.getInstance().collection("Users");
        userID = user.getUid();

        Logout = (Button) findViewById(R.id.LogoutProfile);

        final TextView textFullName= (TextView) findViewById(R.id.FullName);
        final TextView textEmail = (TextView) findViewById(R.id.EmailAddress);
        final TextView textAge = (TextView) findViewById(R.id.Age);

        reference.document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
               User userprofile = value.toObject(User.class);
               if (userprofile != null){
                   String fullName = userprofile.fullName;
                   String email = userprofile.email;
                   String age = userprofile.age;

                   textFullName.setText(fullName);
                   textEmail.setText(email);
                   textAge.setText(age);

                   Toast.makeText(Profile.this, "Successfully!", Toast.LENGTH_LONG).show();
               }else {
                   Toast.makeText(Profile.this, "Failed to get data!", Toast.LENGTH_LONG).show();
               }
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });
    }
}