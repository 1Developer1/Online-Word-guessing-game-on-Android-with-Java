package com.serkan.kelimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SendWord extends AppCompatActivity {

    private EditText edt21, edt22, edt23, edt24, edt25;
    private Button send_button;
    private String requestUid;
    boolean isSender;

    private String mergedText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_word);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edt21 = findViewById(R.id.edt_21);
        edt22 = findViewById(R.id.edt_22);
        edt23 = findViewById(R.id.edt_23);
        edt24 = findViewById(R.id.edt_24);
        edt25 = findViewById(R.id.edt_25);
        send_button=findViewById(R.id.btnSubmitWord);
        requestUid = getIntent().getStringExtra("requestUid");
        isSender = getIntent().getBooleanExtra("isSender", false);
        System.out.println(isSender);
        System.out.println("RequestID"+requestUid);

        keepPassingFocus();

        edt25.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt21, edt22, edt23, edt24, edt25);
                }
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(requestUid).child("sentMessage");

                Map<String, Object> requestData = new HashMap<>();

                String dataToPut;
                if (isSender) {
                    dataToPut = mergedText;
                    requestData.put("senderText", dataToPut);
                } else {
                    dataToPut = mergedText;
                    requestData.put("receiverText", dataToPut);
                }

                requestRef.updateChildren(requestData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Başarıyla eklenirse yapılacak işlemler
                                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                                intent.putExtra("requestUid", requestUid);
                                intent.putExtra("isSender", isSender);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendWord.this, "Veri eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });




    }
    private void validateRow(EditText edt1, EditText edt2, EditText edt3, EditText edt4, EditText edt5) {
        mergedText = mergeEditTexts(edt1, edt2, edt3, edt4, edt5);
        System.out.println(mergedText);

    }
    private void keepPassingFocus() {
        passFocusToNextEdt(edt21, edt22);
        passFocusToNextEdt(edt22, edt23);
        passFocusToNextEdt(edt23, edt24);
        passFocusToNextEdt(edt24, edt25);
    }
    private void passFocusToNextEdt(EditText edt1, EditText edt2) {

        edt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    edt2.requestFocus();
                }
            }
        });
    }

    private String mergeEditTexts(EditText... editTexts) {
        StringBuilder mergedText = new StringBuilder();
        for (EditText editText : editTexts) {
            mergedText.append(editText.getText().toString());
        }
        return mergedText.toString();
    }

}