package noman.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
TextView signupbtn,proceed;
private FirebaseAuth firebaseAuth;
EditText email,password;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signupbtn=findViewById(R.id.signupbtn);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        proceed=findViewById(R.id.proceed);
        loadingDialog = new Dialog(MainActivity.this);
        loadingDialog.setContentView(R.layout.loading_screen);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.edittext);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        firebaseAuth=FirebaseAuth.getInstance();


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()) {
                    email.setError("Enter Email ID");
                    return;
                }
                else
                {
                    email.setError(null);
                }

                if(password.getText().toString().isEmpty()) {
                    password.setError("Enter Password");
                    return;
                }
                else
                {
                    password.setError(null);
                }

                firebaseLogin();
            }
        });
        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
            startActivity(intent);
            finish();
        }

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));


            }
        });
    }
    private void firebaseLogin()
    {
        loadingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this,"Sucess",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                            startActivity(intent);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_SHORT).show();

                        }


                        loadingDialog.dismiss();
                        // ...
                    }
                });

    }
}