package com.alsiddiquetech.pennypinch.Registration;


import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alsiddiquetech.pennypinch.R;
import com.alsiddiquetech.pennypinch.databinding.ActivityRegisterBinding;
import com.alsiddiquetech.pennypinch.models.User;
import com.alsiddiquetech.pennypinch.utils.PasswordHasher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    DatabaseReference databaseRef;
    FirebaseAuth mAuth;
    PasswordHasher passwordHasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        // Get a reference to the Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


// Get a reference to the "users" node

        String question1 = getString(R.string.question_1);
        String question2 = getString(R.string.question_2);
        String question3 = getString(R.string.question_3);
        String question4 = getString(R.string.question_4);
        String[] questions = {question1, question2, question3, question4};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.securityQuestion1Spinner.setAdapter(adapter);


        binding.registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Validate form fields
                if (!validateName() || !validateEmail() || !validatePassword() || !validateConfirmPassword() || !validateGender() || !validateQuestionFromUser()) {
                    return;
                }
                saveData();
            }
        });

    }

    private void addUserDataToRealtimeDatabase(User users, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            // Save user data to the Realtime Database
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            mDatabase.setValue(users);

                            // Update UI
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void saveData() {
        String name = binding.fullNameUser.getText().toString().trim();
        String email = binding.emailUser.getText().toString().trim();
        String password = binding.userPassword.getText().toString().trim();
        passwordHasher = new PasswordHasher();
        byte[] passwordHash = passwordHasher.hashPassword(password.toCharArray());
        String passwordHashString = Base64.encodeToString(passwordHash, Base64.DEFAULT);

        String confirmPassword = binding.userConfirmPassword.getText().toString().trim();
        String gender = binding.maleRadioButton.isChecked() ? "Male" :
                binding.femaleRadioButton.isChecked() ? "Female" : "";
        String securityQuestion1 = binding.securityQuestion1Spinner.getSelectedItem().toString().trim();
        String answer1 = binding.securityQuestion1Answer.getText().toString().trim();

        User user = new User(name, email, passwordHashString, gender, securityQuestion1, answer1);
        addUserDataToRealtimeDatabase(user, email, password);

    }

    private boolean validateName() {
        String name = binding.fullNameUser.getText().toString().trim();

        if (name.isEmpty()) {
            binding.fullNameUser.setError("Please enter your name");
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        String email = binding.emailUser.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailUser.setError("Please enter your email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailUser.setError("Please enter a valid email address");
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        String password = binding.userPassword.getText().toString().trim();

        if (password.isEmpty()) {
            binding.userPassword.setError("Please enter a password");
            return false;
        } else if (password.length() < 6) {
            binding.userPassword.setError("Password must be at least 6 characters long");
            return false;
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        String confirmPassword = binding.userConfirmPassword.getText().toString().trim();

        if (confirmPassword.isEmpty()) {
            binding.userConfirmPassword.setError("Please confirm your password");
            return false;
        } else if (!confirmPassword.equals(binding.userPassword.getText().toString())) {
            binding.userConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean validateGender() {

// Get the selected radio button ID
        int selectedRadioButtonId = binding.genderRadioGroup.getCheckedRadioButtonId();
        String gender = "";
// Check if a radio button is selected
        if (selectedRadioButtonId == -1) {
            // No radio button is selected, show an error message
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // A radio button is selected, do something with the selected radio button
            if (selectedRadioButtonId == binding.maleRadioButton.getId()) {
                // Male radio button is selected
                gender = "Male";
            } else if (selectedRadioButtonId == binding.femaleRadioButton.getId()) {
                // Female radio button is selected
                gender = "Female";
            }
        }
        return true;

    }

    public boolean validateQuestionFromUser() {
        String name = binding.securityQuestion1Answer.getText().toString().trim();

        if (name.isEmpty()) {
            binding.securityQuestion1Answer.setError("Please enter your Answer");
            return false;
        }

        return true;

    }
}