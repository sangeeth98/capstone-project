package com.example.capstoneprototype;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
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

public class Room_Occupied extends AppCompatActivity {
    FirebaseDatabase fire;
    DatabaseReference db;
    Spinner roomsp,datesp;
    Button showbtn;
    TextView txtarea1,txtarea2;
    String co,bo,name,value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room__occupied);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        roomsp=(Spinner)findViewById(R.id.roomnosp);
        datesp=(Spinner)findViewById(R.id.datesp);
        showbtn=(Button)findViewById(R.id.showbtn);
        fire=FirebaseDatabase.getInstance();
        db=fire.getReference();
        db.child("Room Occupied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final List<String> roomnumbers = new ArrayList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String rName = areaSnapshot.getKey();
                    roomnumbers.add(rName);
                }


                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Room_Occupied.this, android.R.layout.simple_spinner_item, roomnumbers);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roomsp.setAdapter(areasAdapter);
                roomsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (roomsp.getSelectedItem() != null) {
                             co = roomsp.getSelectedItem().toString();
                            db.child("Room Occupied").child(co).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final List<String> dates = new ArrayList<String>();
                                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                        String date = areaSnapshot.getKey();
                                        dates.add(date);
                                    }


                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Room_Occupied.this, android.R.layout.simple_spinner_item, dates);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    datesp.setAdapter(arrayAdapter);
                                     bo = datesp.getSelectedItem().toString();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Select Room First", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                    }
                });
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils util = new Utils();
                txtarea1 = (TextView) findViewById(R.id.textArea1);
                txtarea2 = (TextView) findViewById(R.id.textArea2);
                util.enableScroll(txtarea1);
                util.enableScroll(txtarea2);
                if (co.equals(null) || bo.equals(null))
                    Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                else {
                    db.child("Room Occupied").child(co).child(bo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            txtarea1.setText("");
                            txtarea2.setText("");
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                name = ds.getKey();
                                value = ds.getValue(String.class);

                                if (value.equals("Occupied"))
                                    txtarea1.append(name + "\n");
                                else
                                    txtarea2.append(name + "\n");
                                txtarea1.setMovementMethod(new ScrollingMovementMethod());
                                txtarea2.setMovementMethod(new ScrollingMovementMethod());
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });
    }

}
