package com.alsiddiquetech.pennypinch.Registration;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alsiddiquetech.pennypinch.MainActivity;
import com.alsiddiquetech.pennypinch.R;
import com.alsiddiquetech.pennypinch.databinding.ActivityLoginBinding;
import com.alsiddiquetech.pennypinch.utils.PasswordHasher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private PasswordHasher passwordHasher;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        passwordHasher = new PasswordHasher();


        binding.loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.userEmail.getText().toString().trim();
                String password = binding.userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    binding.userEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    binding.userPassword.setError("Password is required");
                    return;
                }

                // Retrieve the hashed password for the given email from the database
                mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.getChildren().iterator().next().getKey();
                            String storedPasswordHash = (String) dataSnapshot.child(userId).child("password").getValue();

                            // Hash the entered password and compare it to the stored password hash
                            byte[] enteredPasswordHash = passwordHasher.hashPassword(password.toCharArray());
                            if (enteredPasswordHash.equals(storedPasswordHash)) {
                                // Passwords match, sign in the user with Firebase Authentication
                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Passwords don't match
                                binding.userPassword.setError("Incorrect password");
                            }
                        } else {
                            // No user found with the given email
                            binding.userEmail.setError("No user found with this email");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}



