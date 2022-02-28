package com.app.appbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    ProgressBar pBar;
    FirebaseAuth fAuth;

    RelativeLayout googleBtn;

    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        googleBtn = findViewById(R.id.google_btn);
        pBar = findViewById(R.id.progress_bar);
        fAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });


    }
    private void signIn(){
        pBar.setVisibility(View.VISIBLE);
        Intent singInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            FirebaseGoogleAuth(null);
            pBar.setVisibility(View.GONE);

        }

    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        if (acct != null){
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            fAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = fAuth.getCurrentUser();
                                updateUi(user);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                updateUi(null);
                            }
                        }
                    });
        }

    }
    private void updateUi(FirebaseUser fUser) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            pBar.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, CourseCategory.class);
            Toast.makeText(LoginActivity.this, "Google Signed", Toast.LENGTH_SHORT).show();
            startActivity(intent);

        }else {
            Toast.makeText(this, "No Account", Toast.LENGTH_SHORT).show();
        }

    }

    public void SignUptext(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void OnLogInClick(View view) {
        pBar = findViewById(R.id.progress_bar);
        pBar.setVisibility(View.VISIBLE);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        fAuth = FirebaseAuth.getInstance();

        String emailLogin = email.getText().toString();
        String passLogin = password.getText().toString();

        if (TextUtils.isEmpty(emailLogin)) {
            email.setError("Email is Required");
            pBar.setVisibility(View.GONE);
            return;
        } else if (TextUtils.isEmpty(passLogin)) {
            password.setError("Password is Required");
            pBar.setVisibility(View.GONE);
            return;
        } else if(!TextUtils.isEmpty(emailLogin) && !TextUtils.isEmpty(passLogin)) {

            fAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText( LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        pBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(),CourseCategory.class);
                        startActivity(intent);
                       finishAffinity();
                    } else {
                        Toast.makeText( LoginActivity.this, "Logged In Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        pBar.setVisibility(View.GONE);

                    }

                }
            });
        }



    }
}