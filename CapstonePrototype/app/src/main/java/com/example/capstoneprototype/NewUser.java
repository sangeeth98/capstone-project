package com.example.capstoneprototype;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewUser extends AppCompatActivity {
Button newuser;
EditText name,emp,pswd,emer;
String gname,gemp,gpswd,gemer;

DatabaseReference db;
boolean b=false,b2=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        newuser=(Button)findViewById(R.id.regibtn);
        name=(EditText)findViewById(R.id.nametxt);
        emp=(EditText)findViewById(R.id.empidtxt);
        pswd=(EditText)findViewById(R.id.pswdtxt);
        emer=(EditText)findViewById(R.id.emertxt);
        db= FirebaseDatabase.getInstance().getReference();
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gemp=emp.getText().toString();
                gname=name.getText().toString();
                gpswd=pswd.getText().toString();
                gemer=emer.getText().toString();
                db.child("Empids").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            if(ds.getKey().equals(gemp))
                            {
                                b=true;
                                if(ds.getValue(long.class)==0)
                                {
                                    DatabaseReference sete=FirebaseDatabase.getInstance().getReference(("Empids/"+gemp+"/"));
                                    sete.setValue(1);
                                    regiterUser();
                                    b2=true;
                                }
                                else
                                {
                                    name.setText(" ");
                                    emp.setText(" ");
                                    pswd.setText(" ");
                                    emer.setText(" ");
                                    Toast.makeText(NewUser.this,"User Already Exists",Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(b2)
                                break;
                        }
                        if(b=false)
                        {
                            Toast.makeText(NewUser.this,"Invalid Employee ID",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(NewUser.this,"Database Error,Please try again later",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    public void regiterUser()
    {
        DatabaseReference db2=FirebaseDatabase.getInstance().getReference("Staffs/"+gname+"/name/");
        db2.setValue(gname);
        db2=FirebaseDatabase.getInstance().getReference("Staffs/"+gname+"/Password/");
        db2.setValue(gpswd);
        db2=FirebaseDatabase.getInstance().getReference("Staffs/"+gname+"/Emergency/");
        db2.setValue(gemer);
        db2=FirebaseDatabase.getInstance().getReference("Staffs/"+gname+"/Empid/");
        db2.setValue(gemp);
        Toast.makeText(NewUser.this,"Registration Successfull,Redirecting",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }

}
