package com.pranav.tech.dailybolts.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pranav.tech.dailybolts.HomeActivity;
import com.pranav.tech.dailybolts.R;
import com.pranav.tech.dailybolts.Utility.ConnectionManagement;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button login_btn, signup_btn;
    private CheckBox remember;
    private ProgressBar progressBar;

    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;
    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkInternetSnack();

        mAuth = FirebaseAuth.getInstance();
        initializeUI();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String roll = sharedPreferences.getString("email", "");
        String passString = sharedPreferences.getString("password", "");

        if (!roll.equals("") || !passString.equals("")) {
            if (email != null) {
                email.setText(roll);
            }
            if (pass != null) {
                pass.setText(passString);
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    private void checkInternetSnack() {
        if (!ConnectionManagement.isConnected(this)) {
            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet connection!!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(Color.CYAN);
            snackbar.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedLoginCheck = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedLoginCheck.edit();
                            editor.putInt("check", 1);
                            editor.commit();
                            finish();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String emailId, password;
        emailId = email.getText().toString().trim();
        password = pass.getText().toString().trim();

        if (TextUtils.isEmpty(emailId)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            openProfile();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initializeUI() {
        email = findViewById(R.id.email_edit);
        pass = findViewById(R.id.password_edit);
        remember = findViewById(R.id.remember_me);
        login_btn = findViewById(R.id.login_btn);
        signup_btn = findViewById(R.id.signup_btn);
        progressBar = findViewById(R.id.progress_login);
    }

    private void openProfile() {
        Toast.makeText(LoginActivity.this, "LoginActivity Successfully", Toast.LENGTH_LONG).show();
        if (remember.isChecked()) {
            if (!email.getText().equals(null) && !pass.getText().equals(null)) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email.getText().toString());
                editor.putString("password", pass.getText().toString());
                editor.commit();
            }
        }

        SharedPreferences sharedLoginCheck = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedLoginCheck.edit();
        editor.putInt("check", 1);
        editor.commit();

        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        finish();
        startActivity(i);
    }
}
