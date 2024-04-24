package com.serkan.kelimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String email;
    private String password;
    private String username;
    EditText passwordEditText;
    EditText emailEditText;
    EditText usernameEditText;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReferance;
    private HashMap<String,Object>mData;
    Button kayıtOl;
    Button girisYap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        passwordEditText=(EditText)findViewById(R.id.signup_password);
        emailEditText=(EditText) findViewById(R.id.signup_email);
        usernameEditText=(EditText)findViewById(R.id.signup_username);
        kayıtOl=(Button)findViewById(R.id.signup_button);
        girisYap=(Button)findViewById(R.id.signupLogin_button);

        girisYap.setOnClickListener(this);
        kayıtOl.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        mReferance= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==kayıtOl.getId()) {
            username=usernameEditText.getText().toString();
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();


            if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    mUser=mAuth.getCurrentUser();
                                    mData=new HashMap<>();
                                    mData.put("Username",username);
                                    mData.put("Email",email);
                                    mData.put("Password",password);
                                    mData.put("UserID",mUser.getUid());
                                    mData.put("status", false);


                                    mReferance.child("Users").child(mUser.getUid())
                                            .setValue(mData)
                                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                        Toast.makeText(MainActivity.this,"Kayıt İşlemi Başarılı",Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                                else
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_LONG).show();

            }
        }
        if(v.getId()==R.id.signupLogin_button){
            Intent i = new Intent(getApplicationContext(),GirisYap.class);
            startActivity(i);
            finish();
        }





    }
}