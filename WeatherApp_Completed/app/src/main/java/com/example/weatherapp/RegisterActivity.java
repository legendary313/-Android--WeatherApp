package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.weatherapp.Models.User;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText textEmail, textPassword, textName;
    ProgressBar progressBar;
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LinearLayout kayout=findViewById(R.id.rl);
        kayout.setBackgroundResource(BgImage.getInstance().getImageName());
        textEmail=findViewById(R.id.email_ed_register);
        textPassword=findViewById(R.id.password_ed_register);
        textName=findViewById(R.id.name_ed_register);
        progressBar=findViewById(R.id.progressBarRegister);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void RegisterUser(View v)
    {
        progressBar.setVisibility(View.VISIBLE);
        final String email = textEmail.getText().toString();
        final String password = textPassword.getText().toString();
        final String name = textName.getText().toString();

        if(!email.equals("")  && !password.equals("") && password.length()>6)
        {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        //insert value to database
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        User u = new User();
                        u.setName(name);
                        u.setEmail(email);

                        reference.child(firebaseUser.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                    Intent i = new Intent(RegisterActivity.this, CommentActivity.class);
                                    startActivity(i);
                                }
                                else
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Đăng ký thất bại, xin kiểm tra email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void gotoLogin(View v)
    {
        Intent i = new Intent(RegisterActivity.this, SignIn.class);
        startActivity(i);
    }
}
