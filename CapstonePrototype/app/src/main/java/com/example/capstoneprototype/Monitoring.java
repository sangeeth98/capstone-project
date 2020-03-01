package com.example.capstoneprototype;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class Monitoring extends AppCompatActivity {
    FirebaseDatabase fire;
    DatabaseReference db;
    Button activitybtn;
    TextView txtarea;
    Spinner pSpinner,dSpinner;
    String pselected;
    String name,value,co,bo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activitybtn = (Button) findViewById(R.id.activitybtn);
        fire = FirebaseDatabase.getInstance();
        db = fire.getReference();
        pSpinner = (Spinner) findViewById(R.id.pselect);
        dSpinner = (Spinner) findViewById(R.id.dateselect);
        db.child("Monitoring").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<String> roomnumbers = new ArrayList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String rName = areaSnapshot.getKey();
                    roomnumbers.add(rName);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Monitoring.this, android.R.layout.simple_spinner_item, roomnumbers);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pSpinner.setAdapter(areasAdapter);
                pSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (pSpinner.getSelectedItem() != null) {
                            co = pSpinner.getSelectedItem().toString();
                            db.child("Monitoring").child(co).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final List<String> dates = new ArrayList<String>();
                                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                        String date = areaSnapshot.getKey();
                                        dates.add(date);
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Monitoring.this, android.R.layout.simple_spinner_item, dates);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    dSpinner.setAdapter(arrayAdapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Select Prisoner First", Toast.LENGTH_SHORT).show();
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

        activitybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtarea = (TextView) findViewById(R.id.textArea);
                Utils.enableScroll(txtarea);
                co=pSpinner.getSelectedItem().toString();
                bo=dSpinner.getSelectedItem().toString();
                    db.child("Monitoring").child(co).child(bo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            txtarea.setText("");

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                name = ds.getKey();
                                value = ds.getValue(String.class);
                                txtarea.append(name + ": " + value + "\n");
                                txtarea.setMovementMethod(new ScrollingMovementMethod());
                            }

                            display(name,value);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();

                        }
                    });



            }
        });
    }

    public void display(String time,String place)
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(Monitoring.this);
        dialog.setMessage(time+" "+place);
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

