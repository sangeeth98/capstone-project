package com.example.capstoneprototype;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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
                Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","abhishekkishore.2016@vitstudent.ac.in",null));
                intent.putExtra(Intent.EXTRA_SUBJECT, typesp.getSelectedItem().toString());
                intent.putExtra(Intent.EXTRA_TEXT, areasp.getSelectedItem().toString()+"\n"+detailstxt.getText().toString());
                startActivity(intent);
            }
        });
    }
}