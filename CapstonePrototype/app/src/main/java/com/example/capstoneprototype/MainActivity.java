package com.example.capstoneprototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    EditText login,password;
    Button loginbtn,newUser,emer;
    int wrongcre=0;
    ProgressBar progressBar;
    FirebaseDatabase firedb;
    DatabaseReference db;
    String m_Text,emerpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        login=(EditText)findViewById(R.id.LOGIN);
        password=(EditText)findViewById(R.id.PASSWORD);
        loginbtn=(Button) findViewById(R.id.loginbtn);
        newUser=(Button) findViewById(R.id.newUserbtn);
        emer=(Button) findViewById(R.id.emergbtn);
        progressBar.setVisibility(View.GONE);
        firedb=FirebaseDatabase.getInstance();
        db=firedb.getReference("Staffs");
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String logi=login.getText().toString().trim();
                final String pass=password.getText().toString().trim();
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(logi))
                        {
                            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                            while (iterator.hasNext()) {
                                DataSnapshot next = (DataSnapshot) iterator.next();
                                if( logi.equals(next.child("name").getValue()))
                                {
                                    if(pass.equals(next.child("Password").getValue()))
                                    {

                                        progressBar.setVisibility(View.GONE);
                                        Intent intent=new Intent(getApplicationContext(),Navigation.class);
                                        startActivity(intent);
                                        login.setText("");
                                        password.setText("");

                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        wrongcre++;
                                        if(wrongcre>4)
                                        {
                                            abortapp();
                                        }
                                        Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            wrongcre++;
                            if(wrongcre>4)
                            {
                                abortapp();
                            }
                            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),NewUser.class);
                startActivity(intent);
            }
        });
        emer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logi=login.getText().toString().trim();
                if(logi.equals(""))
                {
                    Toast.makeText(MainActivity.this,"Enter Login",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.child(logi).child("Emergency").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            emerpass = dataSnapshot.getValue(String.class);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Enter Emergency Password");
                            final EditText input = new EditText(MainActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_Text = input.getText().toString();
                                    if (m_Text.equals(emerpass)) {
                                        Intent intent = new Intent(getApplicationContext(), Emergency.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this,"Please use correct credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this,"Database Error,Please use correct credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void  abortapp()
    {
        finish();
        System.exit(0);
    }
}
