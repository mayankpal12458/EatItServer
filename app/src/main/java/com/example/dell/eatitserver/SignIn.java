package com.example.dell.eatitserver;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {
    FButton fbtnsignin;
    // FirebaseAuth auth;
    EditText editemail, editpass;
    FirebaseDatabase database;
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        fbtnsignin = (FButton) findViewById(R.id.fbtnsignin);
        editemail = (EditText) findViewById(R.id.editemail);
        editpass = (EditText) findViewById(R.id.editpass);
        // auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("Users");
        fbtnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ModelUser model=dataSnapshot.getValue(ModelUser.class);
                        if(model.getPassword().equals(editpass.getText().toString()))
                        {
                            Intent intent=new Intent(SignIn.this,Listfoods.class);
                            startActivity(intent);
                            finish();
                            //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext()," Not Successs",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}


