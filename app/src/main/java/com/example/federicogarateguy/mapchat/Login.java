package com.example.federicogarateguy.mapchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
    }


    public void login(View v) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String pass = ((EditText) findViewById(R.id.password)).getText().toString();
        if (!email.equals("") && !pass.equals("")) {
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    )
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
                                    SharedPreferences.Editor e = sp.edit();
                                    e.putString("user_data", authResult.getUser().getUid());
                                    e.commit();
                                    finish();
                                }
                            }
                    );
        } else {
            Toast.makeText(Login.this, R.string.fillform, Toast.LENGTH_LONG).show();
        }
    }


    public void registry(View v) {
        Intent registro = new Intent(this, Registry.class);
        startActivity(registro);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
