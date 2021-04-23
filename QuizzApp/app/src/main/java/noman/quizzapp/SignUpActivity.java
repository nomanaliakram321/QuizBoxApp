package noman.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import noman.quizzapp.models.ModelClass;

public class SignUpActivity extends AppCompatActivity {
TextView signinbtn,proceed;
EditText username,email,password,confirm_password;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign_up);
        signinbtn=findViewById(R.id.signinbtn);
        proceed=findViewById(R.id.proceed);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);


        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String UserName=username.getText().toString();
                String Email=email.getText().toString();
                String Password=password.getText().toString();
                String Confirm_Password=confirm_password.getText().toString();
                if(TextUtils.isEmpty( UserName))
                {
                    username.setError("This Field Can't be Empty");
                }
                else if(!(Patterns.EMAIL_ADDRESS.matcher(Email).matches()))
                {
                    email.setError("Please Enter the Valid Email Address");
                }
                else if(!(Confirm_Password).equals( Password))
                {
                    confirm_password.setError("This Password not Match");
                    return;
                }
                else if(!(Password.length()>= 6))
                {
                    password.setError("This Password must contain 6 characters");
                }
                else if(TextUtils.isEmpty( Email))
                {
                    email.setError("This Field Can't be Empty");
                }
               else if(TextUtils.isEmpty( Password))
                {
                    password.setError("This Field Can't be Empty");
                }
               else if(TextUtils.isEmpty(Confirm_Password))
                {
                    confirm_password.setError("This Field Can't be Empty");
                }
               else {
//                    startActivity(new Intent(SignUpActivity.this, CategoryActivity.class));

               mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        ModelClass values=new ModelClass(UserName,Email,Password);
                        FirebaseDatabase.getInstance().getReference("DataofUsers").
                                child(FirebaseAuth.getInstance().getUid()).setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(SignUpActivity.this,"Successfully submitted",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(SignUpActivity.this,"There is Some Error",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(SignUpActivity.this,"There is Some Error",Toast.LENGTH_SHORT).show();

                    }
                   }
               });
                }
               }
        });

    }
}