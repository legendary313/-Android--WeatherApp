package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.weatherapp.Adapter.MessageAdapter;
import com.example.weatherapp.Models.AllMethod;
import com.example.weatherapp.Models.Message;
import com.example.weatherapp.Models.User;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messagedb;
    MessageAdapter messageAdapter;
    User u;
    List<Message> messageList;

    RecyclerView rvMessage;
    EditText etMessage;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        RelativeLayout kayout=findViewById(R.id.rl);
        kayout.setBackgroundResource(BgImage.getInstance().getImageName());
        init();
    }
    private void init(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        u = new User();

        rvMessage = findViewById(R.id.rvMessage);
        etMessage = findViewById(R.id.etMessage);
        imgButton = findViewById(R.id.btnSend);
        imgButton.setOnClickListener(this);
        messageList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(!TextUtils.isEmpty(etMessage.getText().toString()))
        {
            Message message = new Message(etMessage.getText().toString(),u.getName());
            etMessage.setText("");
            messagedb.push().setValue(message);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Bạn phải nhập nội dung binh luận.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuLogout)
        {
            auth.signOut();
            finish();
            startActivity(new Intent(CommentActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = auth.getCurrentUser();

        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());

        database.getReference("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
                u.setUid(currentUser.getUid());
                AllMethod.name = u.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messagedb = database.getReference("messageList");
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                messageList.add(message);
                displayMessages(messageList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());

                List<Message> newMessageList = new ArrayList<Message>();

                for(Message m :messageList)
                {
                    if(m.getKey().equals(message.getKey()))
                    {
                        newMessageList.add(message);
                    }
                    else
                    {
                        newMessageList.add(m);
                    }
                }
                messageList = newMessageList;
                displayMessages(messageList);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                List<Message> newMessageList = new ArrayList<Message>();
                for(Message m :messageList)
                {
                    if(!m.getKey().equals(message.getKey()))
                    {
                        newMessageList.add(m);
                    }
                }
                messageList = newMessageList;
                displayMessages(messageList);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageList = new ArrayList<>();
    }

    private void displayMessages(List<Message> messageList) {
        rvMessage.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        messageAdapter = new MessageAdapter(CommentActivity.this, messageList, messagedb);
        rvMessage.setAdapter(messageAdapter);
    }
}

