package com.serkan.kelimeapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {
    private String requestUid;
    private TextView idTVCongo;
    private boolean isSender;
    private ProgressBar progressBar;
    private TextView progressBar_textView;
    private boolean isEditTextClicked;
    private Handler handler;

    int i;


    private EditText edt11, edt12, edt13, edt14, edt15,
            edt21, edt22, edt23, edt24, edt25,
            edt31, edt32, edt33, edt34, edt35,
            edt41, edt42, edt43, edt44, edt45,
            edt51, edt52, edt53, edt54, edt55,
            edt61, edt62, edt63, edt64, edt65;

    private String WORD ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        idTVCongo=findViewById(R.id.idTVCongo);
        edt11 = findViewById(R.id.edt_11);
        edt12 = findViewById(R.id.edt_12);
        edt13 = findViewById(R.id.edt_13);
        edt14 = findViewById(R.id.edt_14);
        edt15 = findViewById(R.id.edt_15);
        edt21 = findViewById(R.id.edt_21);
        edt22 = findViewById(R.id.edt_22);
        edt23 = findViewById(R.id.edt_23);
        edt24 = findViewById(R.id.edt_24);
        edt25 = findViewById(R.id.edt_25);
        edt31 = findViewById(R.id.edt_31);
        edt32 = findViewById(R.id.edt_32);
        edt33 = findViewById(R.id.edt_33);
        edt34 = findViewById(R.id.edt_34);
        edt35 = findViewById(R.id.edt_35);
        edt41 = findViewById(R.id.edt_41);
        edt42 = findViewById(R.id.edt_42);
        edt43 = findViewById(R.id.edt_43);
        edt44 = findViewById(R.id.edt_44);
        edt45 = findViewById(R.id.edt_45);
        edt51 = findViewById(R.id.edt_51);
        edt52 = findViewById(R.id.edt_52);
        edt53 = findViewById(R.id.edt_53);
        edt54 = findViewById(R.id.edt_54);
        edt55 = findViewById(R.id.edt_55);
        edt61 = findViewById(R.id.edt_61);
        edt62 = findViewById(R.id.edt_62);
        edt63 = findViewById(R.id.edt_63);
        edt64 = findViewById(R.id.edt_64);
        edt65 = findViewById(R.id.edt_65);

        EditText[] editTexts = {
                edt11, edt12, edt13, edt14, edt15,
                edt21, edt22, edt23, edt24, edt25,
                edt31, edt32, edt33, edt34, edt35,
                edt41, edt42, edt43, edt44, edt45,
                edt51, edt52, edt53, edt54, edt55,
                edt61, edt62, edt63, edt64, edt65
        };

        for (EditText editText : editTexts) {
            editText.setOnTouchListener((v, event) -> {
                // When any EditText is clicked, set isEditTextClicked to true
                isEditTextClicked = true;
                return false;
            });
        }

        progressBar=findViewById(R.id.countdown_progress);
        progressBar_textView=findViewById(R.id.progressBar_centerText);
        progressBar.setVisibility(View.GONE);
        progressBar_textView.setVisibility(View.GONE);

        startTimer();


        isSender = getIntent().getBooleanExtra("isSender", false);
        requestUid = getIntent().getStringExtra("requestUid");
        System.out.println("RequestID"+requestUid);

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(requestUid).child("sentMessage");

        requestRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (isSender) {
                        WORD = snapshot.child("receiverText").getValue(String.class);
                    } else {
                        WORD = snapshot.child("senderText").getValue(String.class);
                    }
                } else {
                    Log.d("SendWord", "Veri bulunamadı.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SendWord", "Veri alınamadı: " + error.getMessage());
            }
        });

        System.out.println("KELİME----->"+WORD);



        keepPassingFocus();


        edt15.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt11, edt12, edt13, edt14, edt15);
                }
            }
        });

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

        edt35.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt31, edt32, edt33, edt34, edt35);
                }
            }
        });

        edt45.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt41, edt42, edt43, edt44, edt45);
                }
            }
        });

        edt55.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt51, edt52, edt53, edt54, edt55);
                }
            }
        });

        edt65.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    validateRow(edt61, edt62, edt63, edt64, edt65);
                }
            }
        });




    }

    private void startTimer() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!isEditTextClicked) {

                } else {
                    cancel();
                    isEditTextClicked = false;
                    progressBar.setVisibility(View.GONE);
                    progressBar_textView.setVisibility(View.GONE);

                    startTimer();
                }
            }

            public void onFinish() {
                if (!isEditTextClicked) {
                    Toast.makeText(GameActivity.this, "Please make a selection.", Toast.LENGTH_SHORT).show();
                    isEditTextClicked = false;
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar_textView.setVisibility(View.VISIBLE);

                    startCountdown();
                }
            }
        }.start();
    }

    private void startCountdown() {
        new CountDownTimer(10000, 1000) { // 10 saniye, her saniye için
            public void onTick(long millisUntilFinished) {
                if (isEditTextClicked) {
                    cancel();
                    startTimer();
                } else {
                    progressBar.setProgress((int) (millisUntilFinished / 1000));
                    progressBar_textView.setText(String.valueOf(millisUntilFinished / 1000));
                }
            }

            public void onFinish() {
                makeGameInactive();
                startTimer();
                progressBar.setVisibility(View.GONE);
                progressBar_textView.setVisibility(View.GONE);
            }
        }.start();

    }


        private void makeGameInactive() {

        edt11.setEnabled(false);
        edt12.setEnabled(false);
        edt13.setEnabled(false);
        edt14.setEnabled(false);
        edt15.setEnabled(false);
        edt21.setEnabled(false);
        edt22.setEnabled(false);
        edt23.setEnabled(false);
        edt24.setEnabled(false);
        edt25.setEnabled(false);
        edt31.setEnabled(false);
        edt32.setEnabled(false);
        edt33.setEnabled(false);
        edt34.setEnabled(false);
        edt35.setEnabled(false);
        edt41.setEnabled(false);
        edt42.setEnabled(false);
        edt43.setEnabled(false);
        edt44.setEnabled(false);
        edt45.setEnabled(false);
        edt51.setEnabled(false);
        edt52.setEnabled(false);
        edt53.setEnabled(false);
        edt54.setEnabled(false);
        edt55.setEnabled(false);
        edt61.setEnabled(false);
        edt62.setEnabled(false);
        edt63.setEnabled(false);
        edt64.setEnabled(false);
        edt65.setEnabled(false);
    }

    private void validateRow(EditText edt1, EditText edt2, EditText edt3, EditText edt4, EditText edt5) {
        String edt1Txt = edt1.getText().toString();
        String edt2Txt = edt2.getText().toString();
        String edt3Txt = edt3.getText().toString();
        String edt4Txt = edt4.getText().toString();
        String edt5Txt = edt5.getText().toString();

        String w1 = String.valueOf(WORD.charAt(0));
        String w2 = String.valueOf(WORD.charAt(1));
        String w3 = String.valueOf(WORD.charAt(2));
        String w4 = String.valueOf(WORD.charAt(3));
        String w5 = String.valueOf(WORD.charAt(4));

        int greenCount = 0;
        int yellowCount = 0;

        if (edt1Txt.equals(w2) || edt1Txt.equals(w3) || edt1Txt.equals(w4) || edt1Txt.equals(w5)) {
            yellowCount++;
            edt1.setBackgroundColor(Color.parseColor("#ffff00"));
        }
        if (edt2Txt.equals(w1) || edt2Txt.equals(w3) || edt2Txt.equals(w4) || edt2Txt.equals(w5)) {
            yellowCount++;
            edt2.setBackgroundColor(Color.parseColor("#ffff00"));
        }
        if (edt3Txt.equals(w1) || edt3Txt.equals(w2) || edt3Txt.equals(w4) || edt3Txt.equals(w5)) {
            yellowCount++;
            edt3.setBackgroundColor(Color.parseColor("#ffff00"));
        }
        if (edt4Txt.equals(w1) || edt4Txt.equals(w2) || edt4Txt.equals(w3) || edt4Txt.equals(w5)) {
            yellowCount++;
            edt4.setBackgroundColor(Color.parseColor("#ffff00"));
        }
        if (edt5Txt.equals(w1) || edt5Txt.equals(w2) || edt5Txt.equals(w3) || edt5Txt.equals(w4)) {
            yellowCount++;
            edt5.setBackgroundColor(Color.parseColor("#ffff00"));
        }

        if (edt1Txt.equals(w1)) {
            greenCount++;
            edt1.setBackgroundColor(Color.parseColor("#33cc33"));
        }
        if (edt2Txt.equals(w2)) {
            greenCount++;
            edt2.setBackgroundColor(Color.parseColor("#33cc33"));
        }
        if (edt3Txt.equals(w3)) {
            greenCount++;
            edt3.setBackgroundColor(Color.parseColor("#33cc33"));
        }
        if (edt4Txt.equals(w4)) {
            greenCount++;
            edt4.setBackgroundColor(Color.parseColor("#33cc33"));
        }
        if (edt5Txt.equals(w5)) {
            greenCount++;
            edt5.setBackgroundColor(Color.parseColor("#33cc33"));
        }

        if (!edt1Txt.equals(w1) && !edt1Txt.equals(w2) && !edt1Txt.equals(w3) && !edt1Txt.equals(w4) && !edt1Txt.equals(w5)) {
            edt1.setBackgroundColor(Color.parseColor("#ff3333"));
        }

        if (!edt2Txt.equals(w1) && !edt2Txt.equals(w2) && !edt2Txt.equals(w3) && !edt2Txt.equals(w4) && !edt2Txt.equals(w5)) {
            edt2.setBackgroundColor(Color.parseColor("#ff3333"));
        }

        if (!edt3Txt.equals(w1) && !edt3Txt.equals(w2) && !edt3Txt.equals(w3) && !edt3Txt.equals(w4) && !edt3Txt.equals(w5)) {
            edt3.setBackgroundColor(Color.parseColor("#ff3333"));
        }

        if (!edt4Txt.equals(w1) && !edt4Txt.equals(w2) && !edt4Txt.equals(w3) && !edt4Txt.equals(w4) && !edt4Txt.equals(w5)) {
            edt4.setBackgroundColor(Color.parseColor("#ff3333"));
        }

        if (!edt5Txt.equals(w1) && !edt5Txt.equals(w2) && !edt5Txt.equals(w3) && !edt5Txt.equals(w4) && !edt5Txt.equals(w5)) {
            edt5.setBackgroundColor(Color.parseColor("#ff3333"));
        }

        if (edt1Txt.equals(w1) && edt2Txt.equals(w2) && edt3Txt.equals(w3) && edt4Txt.equals(w4) && edt5Txt.equals(w5)) {
            idTVCongo.setText("Tebrikler, Kelimeyi doğru tahmin ettiniz.");
            idTVCongo.setVisibility(View.VISIBLE);
            makeGameInactive();
            Toast.makeText(getApplicationContext(), "Tebrikler, Kelimeyi doğru tahmin ettiniz.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edt5.getId() == R.id.edt_65) {
            idTVCongo.setText("Maalesef kaybettiniz.");
            idTVCongo.setVisibility(View.VISIBLE);
            makeGameInactive();
            Toast.makeText(getApplicationContext(), "Maalesef kaybettiniz.", Toast.LENGTH_SHORT).show();
        }

        int totalScore = (greenCount * 10) + (yellowCount * 5);
    }


    private void keepPassingFocus() {

        passFocusToNextEdt(edt11, edt12);
        passFocusToNextEdt(edt12, edt13);
        passFocusToNextEdt(edt13, edt14);
        passFocusToNextEdt(edt14, edt15);

        passFocusToNextEdt(edt21, edt22);
        passFocusToNextEdt(edt22, edt23);
        passFocusToNextEdt(edt23, edt24);
        passFocusToNextEdt(edt24, edt25);

        passFocusToNextEdt(edt31, edt32);
        passFocusToNextEdt(edt32, edt33);
        passFocusToNextEdt(edt33, edt34);
        passFocusToNextEdt(edt34, edt35);

        passFocusToNextEdt(edt41, edt42);
        passFocusToNextEdt(edt42, edt43);
        passFocusToNextEdt(edt43, edt44);
        passFocusToNextEdt(edt44, edt45);

        passFocusToNextEdt(edt51, edt52);
        passFocusToNextEdt(edt52, edt53);
        passFocusToNextEdt(edt53, edt54);
        passFocusToNextEdt(edt54, edt55);

        passFocusToNextEdt(edt61, edt62);
        passFocusToNextEdt(edt62, edt63);
        passFocusToNextEdt(edt63, edt64);
        passFocusToNextEdt(edt64, edt65);
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
}
