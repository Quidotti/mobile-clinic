package com.example.mobileclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText email, password, confirmPassword;
    Button returntoLogin;
    Button registerBtn;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox isDoctorBox, isPatientBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        returntoLogin = findViewById(R.id.returntoLogin);

        isDoctorBox = findViewById(R.id.isDoctor);
        isPatientBox = findViewById(R.id.isPatient);

//      sanitizing check box logic
        isDoctorBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isDoctorBox.setChecked(true);
                }
            }
        });

        isPatientBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isPatientBox.setChecked(true);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(email);
                checkField(password);
                checkField(confirmPassword);

//              authenticating the user password and confirm password
                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(Register.this, "password mismatch", Toast.LENGTH_SHORT).show();
                }
//              authenticating the checkbox value
                if(!(isDoctorBox.isChecked() || isPatientBox.isChecked())){
                    Toast.makeText(Register.this, "Please select account type.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(valid){

//                start the user registration process
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("UserEmail", email.getText().toString());
                            userInfo.put("Password", password.getText().toString());

//                          specify if the user is doctor or patient
                            if(isDoctorBox.isChecked()){
                                userInfo.put("isDoctor", "1");
                            }
                            if(isPatientBox.isChecked()){
                                userInfo.put("isPatient", "1");
                            }

//                         now putting all information into the database
                           df.set(userInfo);
                           if(isDoctorBox.isChecked()){
                               startActivity(new Intent(getApplicationContext(), Login.class));
                               finish();
                           }

                           if(isPatientBox.isChecked()){
                               startActivity(new Intent(getApplicationContext(), Login.class));
                               finish();
                           }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to create account", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

//     click to return to login ui
        returntoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}
