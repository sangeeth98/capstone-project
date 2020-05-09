package com.example.capstoneprototype;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class Settings extends AppCompatActivity {
EditText changetxt,pswdtxt;
Button  chngemail,chngpswd,chngemer,changeconfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changetxt = (EditText) findViewById(R.id.changetxt);
        pswdtxt = (EditText) findViewById(R.id.pswdtxt);
        chngpswd = (Button) findViewById(R.id.chngpswd);
        chngemer = (Button) findViewById(R.id.chngemer);
        chngemail = (Button) findViewById(R.id.chngemail);
        changeconfirm = (Button) findViewById(R.id.changeconfirm);
        changetxt.setVisibility(View.INVISIBLE);
        pswdtxt.setVisibility(View.INVISIBLE);
        changeconfirm.setVisibility(View.INVISIBLE);
        chngpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changetxt.setHint("");
                changetxt.setText("");
                pswdtxt.setText("");
                changetxt.setHint("Enter New Password");
                changetxt.setVisibility(View.VISIBLE);
                pswdtxt.setVisibility(View.VISIBLE);
                changeconfirm.setVisibility(View.VISIBLE);
                changeconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDetails("Password");
                        changetxt.setText("");
                        pswdtxt.setText("");
                    }
                });
            }
        });
        chngemer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changetxt.setHint("");
                changetxt.setText("");
                pswdtxt.setText("");
                changetxt.setHint("Enter New Emer Password");
                changetxt.setVisibility(View.VISIBLE);
                pswdtxt.setVisibility(View.VISIBLE);
                changeconfirm.setVisibility(View.VISIBLE);
                changeconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDetails("Emergency");
                        changetxt.setText("");
                        pswdtxt.setText("");
                    }
                });
            }
        });
        chngemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changetxt.setHint("");
                changetxt.setText("");
                pswdtxt.setText("");
                changetxt.setHint("Enter New Email");
                changetxt.setVisibility(View.VISIBLE);
                pswdtxt.setVisibility(View.VISIBLE);
                changeconfirm.setVisibility(View.VISIBLE);
                changeconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDetails("Email");
                        changetxt.setText("");
                        pswdtxt.setText("");
                    }
                });
            }
        });

    }
    public void changeDetails(final String ref) {
        Intent inte = getIntent();
        String logi = inte.getStringExtra("user");
        final String t1 = changetxt.getText().toString().trim();
        final String t2 = pswdtxt.getText().toString().trim();
        if (t1.equals("") || t2.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            builder.setTitle("One or more fields are empty");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
            Toast.makeText(Settings.this,logi,Toast.LENGTH_SHORT).show();
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Staffs/" + logi);
            db.child("Emergency").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class).equals(t2)) {
                        db.child(ref).setValue(t1);
                    } else {
                        Toast.makeText(Settings.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

}
