package com.serkan.kelimeapp;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class KanalActivity extends AppCompatActivity {
    private EditText receiverUsernameEditText;
    private DatabaseReference requestRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String username;
    LinearLayout harfSabitiOlmayanLayout;
    LinearLayout getHarfSabitiOlanLayout;
    Button harfSabitiOlan_button;
    Button getHarfSabitiOlmayan_button;
    Button sendRequestButton;
    Button acceptRequestButton;
    LinearLayout onlineUsersVerticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kanal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        requestRef = FirebaseDatabase.getInstance().getReference("requests");

        receiverUsernameEditText = (EditText) findViewById(R.id.receiver_username_edit_text);
        sendRequestButton = (Button) findViewById(R.id.send_request_button);
        acceptRequestButton=(Button)findViewById(R.id.acceptRequest_button);
        onlineUsersVerticalLayout = findViewById(R.id.onlineUsers_verticalLayout);
        displayOnlineUsers(); // Online kullanıcıları görüntüle
        displayRequests();
        getUsernameFromUid(currentUser.getUid());


        System.out.println("Username--------: "+username);
        acceptRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");
                System.out.println(requestRef.getKey());
                System.out.println("currentUser  2: "+currentUser);


                // İsteklerin dinlendiği referansa bir ValueEventListener ekleyerek kullanıcının isteklerini kontrol edelim
                requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String receiver = dataSnapshot.child("receiver").getValue(String.class);
                            String status = dataSnapshot.child("status").getValue(String.class);
                            System.out.println("-------> "+receiver+" ------- "+status+"------------"+dataSnapshot.getKey());

                            if (receiver != null && receiver.equals(username) && status.equals("pending")) {
                                acceptRequest(dataSnapshot.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(KanalActivity.this, "Veri okunurken bir hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverUsername = receiverUsernameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(receiverUsername)) {
                    sendRequest(receiverUsername);
                } else {
                    Toast.makeText(KanalActivity.this, "Alıcı kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void sendRequest(String receiverUsername) {
        // İstek verisi oluştur
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("sender", username); // İstek gönderenin kullanıcı adı
        requestMap.put("receiver", receiverUsername); // İstek alıcısının kullanıcı adı
        requestMap.put("status", "pending"); // İstek durumu
        requestMap.put("user_id", currentUser.getUid()); // İstek gönderenin kullanıcı ID'si

        DatabaseReference newRequestRef = requestRef.push();

        String requestId = newRequestRef.getKey();

        newRequestRef.setValue(requestMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(KanalActivity.this, "İstek gönderildi", Toast.LENGTH_SHORT).show();
                        System.out.println("Send Request içi: " + requestId);
                        checkRequestStatus(requestId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KanalActivity.this, "İstek gönderilirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void acceptRequest(String requestId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(requestId);

        requestRef.child("status").setValue("accepted")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(KanalActivity.this, "İstek kabul edildi", Toast.LENGTH_SHORT).show();
                        boolean isSender = false;
                        Intent intent = new Intent(getApplicationContext(), SendWord.class);
                        intent.putExtra("requestUid", requestId);
                        intent.putExtra("isSender", isSender);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KanalActivity.this, "İstek kabul edilirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkRequestStatus(String requestId) {

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(requestId);
        System.out.println("CheckRequest1"+requestId);
        System.out.println("Request REF: "+requestRef);
        System.out.println("Status REF: "+requestRef.child("status"));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            System.out.println("-----"+dataSnapshot.getValue());
                        }
                        System.out.println("TRUE Mİ FALSE Mİ "+snapshot.exists());
                        System.out.println("--"+snapshot.getValue());
                        if (snapshot.exists()) {
                            System.out.println("CheckRequest2");
                            System.out.println("--"+snapshot.getValue());
                            String status = snapshot.child("status").getValue(String.class);
                            System.out.println("STATUS: "+status);
                            if (status != null && status.equals("accepted")) {
                                System.out.println("CheckRequest3");
                                boolean isSender = true;
                                Intent intent = new Intent(getApplicationContext(), SendWord.class);
                                intent.putExtra("requestUid", requestId);
                                intent.putExtra("isSender", isSender);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            checkRequestStatus(requestId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(KanalActivity.this, "İstek durumu kontrol edilirken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 10000); // 10 saniye sonra kontrol et
    }


    private void displayOnlineUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onlineUsersVerticalLayout.removeAllViews(); // Önceki kullanıcıları temizle

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String username = dataSnapshot.child("Username").getValue(String.class);
                    Boolean status = dataSnapshot.child("status").getValue(Boolean.class);

                    if (username != null && status != null && status) {
                        Button userButton = new Button(KanalActivity.this);
                        userButton.setText(username);
                        onlineUsersVerticalLayout.addView(userButton); // Kullanıcıyı lineer düzeneğe ekle
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KanalActivity.this, "Kullanıcılar alınırken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUsernameFromUid(String uid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username = snapshot.child("Username").getValue(String.class);
                    if (username != null) {
                        Toast.makeText(KanalActivity.this, "Kullanıcı adı: " + username, Toast.LENGTH_SHORT).show();
                    } else {
                        // Kullanıcı adı bulunamadı
                        Toast.makeText(KanalActivity.this, "Kullanıcı adı bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(KanalActivity.this, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KanalActivity.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRequests() {
        // İsteklerin Firebase veritabanındaki referansını al
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("requests");

        // ValueEventListener kullanarak istekleri dinle
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Her istek için döngü
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // İstek verilerini al
                    String sender = dataSnapshot.child("sender").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    // Alıcı benim ise ve istek durumu bekliyorsa
                    if (currentUser != null && sender != null && sender.equals(currentUser.getDisplayName()) && status != null && status.equals("pending")) {
                        // Buton oluştur
                        Button requestButton = new Button(KanalActivity.this);
                        requestButton.setText("Yeni bir istek var!");

                        // Butona tıklama dinleyicisi ekle
                        requestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // İsteği kabul et
                                acceptRequest(dataSnapshot.getKey());
                            }
                        });

                        // Butonu layouta ekle
                        onlineUsersVerticalLayout.addView(requestButton);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hata durumunda kullanıcıyı bilgilendir
                Toast.makeText(KanalActivity.this, "İstekler alınırken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }




}

