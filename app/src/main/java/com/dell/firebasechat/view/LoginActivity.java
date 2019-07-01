package com.dell.firebasechat.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dell.firebasechat.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnGoogle;
    EditText edMail, edPass;
    TextView tvRegister;
    Button btnFB;
    ImageView igmain;
    public static final int GOOGLE_SIGN = 123;

    FirebaseAuth auth;
    CallbackManager mCallbackManager;
    public final static String TAG = "FACEBOOK";
    DatabaseReference reference;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        initView();
        auth = FirebaseAuth.getInstance();

        onClick();
        // Initialize Facebook Login button


    }

    private void initView() {

        btnLogin = findViewById(R.id.btnLogin);
        btnFB = findViewById(R.id.btnFB);
        btnGoogle = findViewById(R.id.btnGoogle);

        edMail = findViewById(R.id.edEmailL);
        edPass = findViewById(R.id.edPassL);
        tvRegister = findViewById(R.id.tvRegister);
        igmain = findViewById(R.id.igMain);
        Glide.with(this)
                .load(R.drawable.nen)
                .apply(new RequestOptions().fitCenter())
                .into(igmain);
    }

    //onClick

    public void onClick(){
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edMail.getText().toString();
                String password = edPass.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "All fileds must not be empty",Toast.LENGTH_LONG).show();
                }else{
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, ListChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                FirebaseUser user = auth.getCurrentUser();
                                putDataRealtime(user,user.getEmail());
                            }else{
                                Toast.makeText(LoginActivity.this, "Login unsuccessfull!!!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
// ...
            }
        });


        GoogleSignInOptions options = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,options);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!=null){
                    firebaseAuthWithGoogle(account);
                }
            }catch (ApiException e){

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e("TAG","firebaseAuthwithGoogle: "+account.getId());
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.e("TAG", "Success sign in");
                            FirebaseUser user = auth.getCurrentUser();
                            putDataRealtime(user,user.getEmail());
                            updateUI(user,"GOOGLE");

                        }else{
                            Log.e("TAG","signin failure",task.getException());
                            Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null,null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser currentUser,String action) {
        Intent intent = new Intent(getApplicationContext(), ListChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Logout", action);
        startActivity(intent);

        finish();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            putDataRealtime(user,user.getEmail());
                            updateUI(user,"FACEBOOK");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null) {
            updateUI(currentUser,null);
        }
    }
    private void signInGoogle(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_SIGN);
    }

    private void putDataRealtime(FirebaseUser currentUser,String username){
        if(currentUser!=null){
            String uid = currentUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("id",uid);
            hashMap.put("username",username);
            reference.setValue(hashMap);
            Log.e("USERGF",currentUser.getUid()+currentUser.getDisplayName());
        }
    }
}
