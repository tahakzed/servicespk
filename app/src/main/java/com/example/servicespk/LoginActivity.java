package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton,sendButton;
    EditText phoneNumberField;
    EditText verificationCodeField;
    String phoneNumber;
    String name;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference docRef;
    String codeSent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton=(Button)findViewById(R.id.login_activity_login_button);
        sendButton=(Button)findViewById(R.id.login_activity_send_button);
        phoneNumberField =(EditText)findViewById(R.id.login_activity_login_username_field);
        verificationCodeField=(EditText)findViewById(R.id.login_activity_verification_field);
        db=FirebaseFirestore.getInstance();
        sendButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        loginButton.setEnabled(false);
        verificationCodeField.setEnabled(false);
        phoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        firebaseAuth= FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("ServicePkPrefs",MODE_PRIVATE);
        if(sharedPreferences.contains("phone") && sharedPreferences.contains("name"))
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("name",sharedPreferences.getString("name","NaN"));
            intent.putExtra("phone",sharedPreferences.getString("phone","00"));
            startActivity(intent);
            finish();
        }
            editor=sharedPreferences.edit();
    }
    @Override
    public void onClick(View view) {
        if(view.equals(sendButton)){
            loginButton.setEnabled(true);
            phoneNumberField.setEnabled(false);
            verificationCodeField.setEnabled(true);
            sendVerificationCode();
            sendButton.setEnabled(false);
            Toast.makeText(getApplicationContext(),"Code Sent!",Toast.LENGTH_SHORT).show();
        }
        else if(view.equals(loginButton)){
            verifySignInCode();
        }
    }

    private void verifySignInCode() {
        String code=verificationCodeField.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //after the code is verified
                                Toast.makeText(getApplicationContext(),"Code is verified!",Toast.LENGTH_SHORT).show();
                                verifyAccount();
                        }
                            else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Code is not verified!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void sendVerificationCode() {
        phoneNumber=phoneNumberField.getText().toString();
        Toast.makeText(getApplicationContext(),phoneNumber,Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(phoneNumber))
        {
            phoneNumberField.setError("This field is required!");
            phoneNumberField.requestFocus();
            return;
        }
        if(phoneNumber.length() < 11){
            phoneNumberField.setError("Enter valid phone number!");
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent=s;
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification failed:"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    };

    private void verifyAccount(){
        Toast.makeText(getApplicationContext(),"Please wait...",Toast.LENGTH_SHORT).show();
        phoneNumberField.setEnabled(false);
        sendButton.setEnabled(false);
        loginButton.setEnabled(false);
        verificationCodeField.setEnabled(false);
        docRef=db.document("users/"+phoneNumber);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name=document.getString("name");
                       Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                       intent.putExtra("name",name);
                       intent.putExtra("phone",phoneNumber);
                       startActivity(intent);
                       editor.putString("name",name);
                       editor.putString("phone",phoneNumber);
                       editor.commit();
                    } else {

                       Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                       intent.putExtra("phone",phoneNumber);
                       startActivity(intent);
                    }



                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Net issue! Please try again",Toast.LENGTH_SHORT).show();
                    phoneNumberField.setEnabled(true);
                    sendButton.setEnabled(true);
                    loginButton.setEnabled(false);
                    verificationCodeField.setEnabled(false);
                }
            }
        });
    }


}