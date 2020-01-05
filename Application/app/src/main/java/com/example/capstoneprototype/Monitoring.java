package com.example.capstoneprototype;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Monitoring extends AppCompatActivity {
    FirebaseDatabase fire;
    DatabaseReference db;
    Button activitybtn;
    TextView txtarea;
    Spinner pSpinner;
    String pselected;
    String name,value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_monitoring);
            activitybtn=(Button)findViewById(R.id.activitybtn);
            fire = FirebaseDatabase.getInstance();
            db = fire.getReference();
            pSpinner= (Spinner) findViewById(R.id.pselect);
            db.child("Monitoring").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final List<String> prisoners = new ArrayList<String>();

                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String pName = areaSnapshot.getKey();
                        prisoners.add(pName);
                    }


                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Monitoring.this, android.R.layout.simple_spinner_item, prisoners);
                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pSpinner.setAdapter(areasAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            activitybtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Utils util=new Utils();
                    txtarea=(TextView)findViewById(R.id.textArea);
                    util.enableScroll(txtarea);
                    pselected=pSpinner.getSelectedItem().toString();
                    db.child("Monitoring").child(pselected).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            txtarea.setText("");

                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                 name = ds.getKey();
                                 value= ds.getValue(String.class);
                                txtarea.append(name+": "+value+"\n");
                                txtarea.setMovementMethod(new ScrollingMovementMethod());
                            }

                          display(value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            });
    }

    public void display(String msg)
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(Monitoring.this);
        dialog.setMessage(msg);
        dialog.setTitle("Latest Activity");
        dialog.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(),"Complete Activity",Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();

    }

}

