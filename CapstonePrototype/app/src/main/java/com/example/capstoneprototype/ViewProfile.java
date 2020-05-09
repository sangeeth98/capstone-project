package com.example.capstoneprototype;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ViewProfile extends AppCompatActivity {
TextView name,password,email,emer,empid;
ImageView photoid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String logi=MainActivity.logi;
        setContentView(R.layout.activity_view_profile);
        photoid=(ImageView)findViewById(R.id.photoid);
        name=(TextView) findViewById(R.id.nametxt);
        password=(TextView) findViewById(R.id.pswdtxt);
        email=(TextView) findViewById(R.id.emailtxt);
        emer=(TextView) findViewById(R.id.emertxt);
        empid=(TextView) findViewById(R.id.empidtxt);
        StorageReference sr= FirebaseStorage.getInstance().getReference("Staff Members").child((logi+".jpg"));
        final long ONE_MEGABYTE = 1024 * 1024;
        sr.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                photoid.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), logi, Toast.LENGTH_LONG).show();
            }
        });
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("Staffs/"+logi+"/");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    switch(Objects.requireNonNull(ds.getKey()))
                    {
                        case "name":name.setText(ds.getValue(String.class));
                                    break;
                        case "Email":email.setText(ds.getValue(String.class));
                            break;
                        case "Password":password.setText(ds.getValue(String.class));
                            break;
                        case "Empid":empid.setText(ds.getValue(String.class));
                            break;
                        case "Emergency":emer.setText(ds.getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
