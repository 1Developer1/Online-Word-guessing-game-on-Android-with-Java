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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class GirisYap extends AppCompatActivity implements View.OnClickListener {
    private String email;
    private String password;
    private String guncelleText;
    EditText passwordEditText;
    EditText emailEditText;
    EditText guncelleEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReferance;
    private HashMap<String,Object> mData;
    Button login_button;
    Button hesapYoksaKayıtOl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_giris_yap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        passwordEditText=findViewById(R.id.login_password);
        emailEditText=findViewById(R.id.login_email);
        login_button=(Button)findViewById(R.id.login_button);
        hesapYoksaKayıtOl=(Button)findViewById(R.id.hesap_yoksa_kayitolButton);

        hesapYoksaKayıtOl.setOnClickListener(this);
        login_button.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==login_button.getId()) {
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            System.out.println(password);
            System.out.println(email);


            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mUser=mAuth.getCurrentUser();
                                System.out.println("Kullanıcı adı:"+mUser.getDisplayName());
                                System.out.println("Kullanıcı Email:"+mUser.getEmail());
                                System.out.println("Kullanıcı Uid:"+mUser.getUid());
                                //Kullanıcı giriş yaptığında "status" alanını true olarak güncelle
                                HashMap<String,Object> updateData = new HashMap<>();
                                updateData.put("status", true);
                                veriGuncelle(updateData,mUser.getUid());
                                veriGetir(mUser.getUid());

                                Intent intent = new Intent(getApplicationContext(), KanalActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GirisYap.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_LONG).show();

            }
        }


        if(v.getId()==R.id.hesap_yoksa_kayitolButton){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void veriGetir(String uID){
        mReferance=FirebaseDatabase.getInstance().getReference("Users").child(uID);
        mReferance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    System.out.println(dataSnapshot.getKey()+"="+dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GirisYap.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void veriGuncelle(HashMap<String,Object> hashMap,final String uid){
        mReferance=FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mReferance.updateChildren(hashMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            System.out.println("Güncellenen veri");
                            veriGetir(uid);
                        }
                    }
                });


    }
}