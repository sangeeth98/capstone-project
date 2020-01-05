package com.example.capstoneprototype;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    EditText login,password;
    Button loginbtn;
    FirebaseDatabase firedb;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=(EditText)findViewById(R.id.LOGIN);
        password=(EditText)findViewById(R.id.PASSWORD);
        loginbtn=(Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String logi=login.getText().toString().trim();
                final String pass=password.getText().toString().trim();
                firedb=FirebaseDatabase.getInstance();
                db=firedb.getReference("Staffs");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(logi))
                        {
                            Iterable<com.google.firebase.database.DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                            while (iterator.hasNext()) {
                                com.google.firebase.database.DataSnapshot next = (DataSnapshot) iterator.next();
                                if( logi.equals(next.child("name").getValue()))
                                {
                                    if(pass.equals(next.child("Password").getValue()))
                                    {

                                        Intent intent=new Intent(getApplicationContext(),Monitoring.class);
                                        intent.putExtra("id",logi);
                                        startActivity(intent);
                                        login.setText("");
                                        password.setText("");

                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
