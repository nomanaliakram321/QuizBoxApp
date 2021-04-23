package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
EditText email,password;
TextView login;
    private Dialog isLoading;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);

        isLoading=new Dialog(SignInActivity.this);
        isLoading.setContentView(R.layout.loading_screen);
        isLoading.setCancelable(false);
        isLoading.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        isLoading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        firebaseAuth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String Email=email.getText().toString();
//                String Password=password.getText().toString();

                if(email.getText().toString().isEmpty())
                {
                    email.setError("Enter Your Email ");
                    return;
                }
                else if(!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()))
                {
                    email.setError("Please Enter the Valid Email Address");
                }

                else if(password.getText().toString().isEmpty())
                {
                    password.setError("Enter Your Password ");
                    return;
                }
                else
                {
                    email.setError(null);
                    firebaseLogin();
                }


            }
        });
        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(SignInActivity.this,CategoryActivity.class));
            finish();

        }
    }

    private void firebaseLogin() {

        isLoading.show();
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignInActivity.this,"Successfully Login",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this,CategoryActivity.class));
                        finish();
                }
                else
                {
                    Toast.makeText(SignInActivity.this,"Login Failure",Toast.LENGTH_SHORT).show();

                }
                isLoading.dismiss();

            }
        });

    }
}