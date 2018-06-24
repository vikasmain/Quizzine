package com.vikkyb.check.devsir;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

        private static final String TAG = "SignInActivity";
        private static final int RC_SIGN_IN = 9001;
        private DatabaseReference users;
        private GoogleApiClient mGoogleApiClient;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;

        private CallbackManager mCallbackManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                setContentView(R.layout.activity_login);
                // Facebook Login
                users= FirebaseDatabase.getInstance().getReference().child("Users");
                users.keepSynced(true);
                FacebookSdk.sdkInitialize(getApplicationContext());
                mCallbackManager = CallbackManager.Factory.create();


                // Facebook Login
                FacebookSdk.sdkInitialize(getApplicationContext());
                mCallbackManager = CallbackManager.Factory.create();




                // Google Sign-In
                // Assign fields

                ImageView mFacebookSignInButton = (ImageView) findViewById(R.id.facebook_login_button);
                mFacebookSignInButton.setOnClickListener(this);






                ImageView mGoogleSignInButton = (ImageView) findViewById(R.id.google_login_button);

                // Set click listeners
                mGoogleSignInButton.setOnClickListener(this);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                // Initialize FirebaseAuth
                mAuth = FirebaseAuth.getInstance();

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                        // User is signed in
                                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                                } else {
                                        // User is signed out
                                        Log.d(TAG, "onAuthStateChanged:signed_out");
                                }
                        }
                };
        }

        @Override
        public void onStart() {
                super.onStart();
                mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
                super.onStop();
                if (mAuthListener != null) {
                        mAuth.removeAuthStateListener(mAuthListener);
                }
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
                Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                                Log.w(TAG, "signInWithCredential", task.getException());
                                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                        } else {
                                                checkuserexists();
                                        }
                                }
                        });
        }

        private void firebaseAuthWithFacebook(AccessToken token) {
                Log.d(TAG, "handleFacebookAccessToken:" + token);

                final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                                Log.w(TAG, "signInWithCredential", task.getException());
                                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                        } else {
                                                startActivity(new Intent(LoginActivity.this, SetupAccount.class));
                                                finish();
                                        }
                                }
                        });
        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                        case R.id.google_login_button:
                                signIn();
                                break;

                        case R.id.facebook_login_button:
                                signInfb();
                                break;

                        default:
                                return;
                }
        }


        private void signInfb() {

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_birthday", "user_friends"));

                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                                firebaseAuthWithFacebook(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                                Log.d(TAG, "facebook:onCancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                        }
                });
        }


        private void signIn() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                mCallbackManager.onActivityResult(requestCode, resultCode, data);

                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                        if (result.isSuccess()) {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = result.getSignInAccount();
                                firebaseAuthWithGoogle(account);
                        } else {
                                // Google Sign In failed
                                Log.e(TAG, "Google Sign In failed.");
                        }
                }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                // be available.
                Log.d(TAG, "onConnectionFailed:" + connectionResult);
                Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }
        private void checkuserexists() {
                if(mAuth.getCurrentUser() !=null)
                {
                        final String uid = mAuth.getCurrentUser().getUid();


                        users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(uid)) {
                                                Intent mintent = new Intent(LoginActivity.this, HomeActivity.class);
                                                mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(mintent);
                                                finish();
                                        }
                                        else
                                        {
                                                Intent mintent = new Intent(LoginActivity.this, SetupAccount.class);
                                                mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(mintent);
                                                finish();
                                        }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                        });
                }
                else
                {
                        Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
                }
        }
}

