package com.example.capstoneprototype;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String ro,dss;
    PieChartView pieChartView;
    DatabaseReference db;
    long ol,ul;
    Spinner roomsp,datesp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        roomsp=(Spinner)findViewById(R.id.roomnosp);
        datesp=(Spinner)findViewById(R.id.datesp);
        final Intent int2=getIntent();
        final String user=int2.getStringExtra("user");
        db=FirebaseDatabase.getInstance().getReference();
        db.child("Room Occupied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final List<String> roomnumbers = new ArrayList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String rName = areaSnapshot.getKey();
                    roomnumbers.add(rName);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Navigation.this, android.R.layout.simple_spinner_item, roomnumbers);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roomsp.setAdapter(areasAdapter);
                roomsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (roomsp.getSelectedItem() != null) {
                            db.child("Room Occupied").child(roomsp.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final List<String> dates = new ArrayList<String>();
                                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                        String date = areaSnapshot.getKey();
                                        dates.add(date);
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Navigation.this, android.R.layout.simple_spinner_item, dates);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    datesp.setAdapter(arrayAdapter);
                                    ro=roomsp.getSelectedItem().toString();
                                    dss=datesp.getSelectedItem().toString();
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
        roomsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pieChartMaker();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        datesp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pieChartMaker();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


//Toast.makeText(Navigation.this,ol+"1"+ul,Toast.LENGTH_SHORT).show();
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.mipmap.ic_launcheremergency);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(getApplicationContext(),Emergency.class);
               intent.putExtra("user",user);
               startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_monitor) {
            Intent intent = new Intent(getApplicationContext(), Monitoring.class);
            startActivity(intent);
        } else if (id == R.id.nav_occupy) {

            Intent intent = new Intent(getApplicationContext(), Room_Occupied.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if(id==R.id.nav_view_profile)
        {
           Intent inte2=getIntent();
           String user=inte2.getStringExtra("user");
            Intent intent=new Intent(getApplicationContext(),ViewProfile.class);
            intent.putExtra("user",user);
            startActivity(intent);
        }
        else if(id==R.id.nav_settings)
        {
            Intent inte2=getIntent();
            String user=inte2.getStringExtra("user");
            Intent intent=new Intent(getApplicationContext(),Settings.class);
            intent.putExtra("user",user);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    public void pieChartMaker()
    {
        ro=roomsp.getSelectedItem().toString();
        dss=datesp.getSelectedItem().toString();
        db.child("Room Occupied").child(ro).child(dss).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    if(ds.getKey().equals("Occupied"))
                        ol=ds.getValue(Long.class);
                    else if(ds.getKey().equals("Unoccupied"))
                        ul=ds.getValue(Long.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pieChartView = findViewById(R.id.chart);
        List pieData = new ArrayList<>();
        pieData.add(new SliceValue(ol, Color.BLUE).setLabel("Occupied"));
        pieData.add(new SliceValue(ul, Color.RED).setLabel("Unoccupied"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(10);
        pieChartView.setPieChartData(pieChartData);
        Toast.makeText(Navigation.this,ol+" "+ul,Toast.LENGTH_SHORT).show();
    }

}

