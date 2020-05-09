package com.example.capstoneprototype;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Emergency extends AppCompatActivity  {
    Spinner typesp,areasp;
    EditText detailstxt;
    Button sendd;
    String[] types={"Fire","Fight Between Inmates","Suspicious Person","Suspicious Object","Increase Survailance","Others"};
    String[] areas={"Mess Hall","GYM","West Wing","East Wing","North Wing","South Wing"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        typesp=(Spinner) findViewById(R.id.typesp);
        areasp=(Spinner) findViewById(R.id.areasp);
        detailstxt=(EditText)findViewById(R.id.detailstxt);
        sendd=(Button) findViewById(R.id.submitbtn);
        ArrayAdapter typea=new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        typea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typesp.setAdapter(typea);
        ArrayAdapter areaa=new ArrayAdapter(this,android.R.layout.simple_spinner_item,areas);
        areaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areasp.setAdapter(areaa);
        sendd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeem=typesp.getSelectedItem().toString();
                String detailsem=areasp.getSelectedItem().toString()+"\n"+detailstxt.getText().toString();
               // Toast.makeText(Emergency.this, typeem+" "+ detailsem,Toast.LENGTH_SHORT).show();
                String email="kishoreabhishek28@gmail.com";
                Uri uri = Uri.parse("mailto:" + email)
                        .buildUpon()
                        .appendQueryParameter("subject", typeem)
                        .appendQueryParameter("body", detailsem)
                        .build();

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(emailIntent);
                }

        });
    }
}