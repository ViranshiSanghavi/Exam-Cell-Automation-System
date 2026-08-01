package com.example.login;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationId;
    private LinearLayout otpContainer;
    private Button btnSubmit;
    private EditText[] otpFields;
    private static final int OTP_LENGTH = 6; // Adjust according to your OTP length

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpContainer = findViewById(R.id.otpContainer);
        btnSubmit = findViewById(R.id.btnSubmit);
        mAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");

        otpFields = new EditText[OTP_LENGTH];
        for (int i = 0; i < OTP_LENGTH; i++) {
            final int index = i;
            EditText otpField = new EditText(this);
            otpField.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            otpField.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            otpField.setTextSize(18);
            otpField.setPadding(16, 16, 16, 16);
            otpField.setEms(2);
            otpField.setMaxLines(1);
            otpField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < OTP_LENGTH - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpFields[i] = otpField;
            otpContainer.addView(otpField);
        }

        btnSubmit.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText field : otpFields) {
                otp.append(field.getText().toString());
            }

            if (otp.length() != OTP_LENGTH) {
                Toast.makeText(OtpActivity.this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.toString());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();

                            // Redirect to StudentDashboardActivity
                            Intent intent = new Intent(OtpActivity.this, StudentDashboardActivity.class);
                            intent.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber")); // Pass the phone number to the dashboard
                            startActivity(intent);
                            finish(); // Close this activity
                        } else {
                            Toast.makeText(OtpActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
