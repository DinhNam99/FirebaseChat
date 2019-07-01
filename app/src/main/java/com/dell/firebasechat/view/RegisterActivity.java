package com.dell.firebasechat.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dell.firebasechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edUsername, edEmail, edPass, edRePass;
    Button btnRegister;
    FirebaseAuth auth;
    ImageView igmain;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        //init
        initView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String email = edEmail.getText().toString();
                String pass = edPass.getText().toString();
                String repass = edRePass.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)|| TextUtils.isEmpty(repass)){
                    Toast.makeText(RegisterActivity.this,"All fileds must not be empty",Toast.LENGTH_LONG).show();
                }else if(pass.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Password must be more thsn 6 characters",Toast.LENGTH_LONG).show();
                }else if(!pass.equals(repass)){
                    Toast.makeText(RegisterActivity.this, "Re_enter password must be equal with password",Toast.LENGTH_LONG).show();
                }else{
                    register(username,email,pass);
                }
            }
        });
    }

    private void initView() {
        edUsername = findViewById(R.id.edUser);
        edEmail = findViewById(R.id.edEmailR);
        edPass = findViewById(R.id.edPassR);
        edRePass = findViewById(R.id.edPassRR);

        btnRegister = findViewById(R.id.btnRegister);
        igmain = findViewById(R.id.igMainR);
        Glide.with(this)
                .load(R.drawable.nen)
                .apply(new RequestOptions().fitCenter())
                .into(igmain);

    }

    private void register (final String username, String email, String pass){
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",uid);
                            hashMap.put("username",username);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(RegisterActivity.this, ListChatActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }else{
                            Toast.makeText(RegisterActivity.this, "Register unsuccessful!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
