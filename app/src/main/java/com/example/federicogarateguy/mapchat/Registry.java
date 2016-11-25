package com.example.federicogarateguy.mapchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Registry extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        auth = FirebaseAuth.getInstance();
        progressBar = new ProgressDialog(this);
    }


    public void registry(View v) {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        progressBar.setMessage(this.getResources().getString(R.string.waiting));
        progressBar.show();
        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUser();
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Registry.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                )
        ;
    }

    public void updateUser() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        auth.getCurrentUser().updateProfile(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.hide();
                        Toast.makeText(Registry.this, R.string.successful_register, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Registry.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Registry.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );

    }
}
