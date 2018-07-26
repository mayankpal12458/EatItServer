package com.example.dell.eatitserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.ULocale;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Interface.ItemClickListner;
import com.example.dell.eatitserver.viewHolder.mainlistviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import info.hoang8f.widget.FButton;

public class Listfoods extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rv;
    static final int choose_img=101;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference myref;
    DrawerLayout mydrawer;
    ActionBarDrawerToggle mtoggle;
    NavigationView mynavigation;
    FloatingActionButton fab;
    FirebaseRecyclerAdapter<Modellist,mainlistviewholder> adapter;
    FButton fbtnupload,fbtnselect;
    EditText editinput;
    Uri uri;
   // ImageView editimg;
    StorageReference storageref;
    Modellist modellist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfoods);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fbtnupload=(FButton)findViewById(R.id.fbtnupload);
        fbtnselect=(FButton)findViewById(R.id.fbtnselect);
       // editimg=(ImageView)findViewById(R.id.editimg);

        storageref= FirebaseStorage.getInstance().getReference();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });

        mydrawer = (DrawerLayout) findViewById(R.id.mydrawer);
        mtoggle = new ActionBarDrawerToggle(this, mydrawer, R.string.open, R.string.close);
        mydrawer.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mynavigation = (NavigationView) findViewById(R.id.mynavigation);
        mynavigation.bringToFront();
        mynavigation.setNavigationItemSelectedListener(Listfoods.this);

        myref = FirebaseDatabase.getInstance().getReference().child("/Category");
        rv = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        adapter=new FirebaseRecyclerAdapter<Modellist, mainlistviewholder>(
                Modellist.class,
                R.layout.custom_row,
                mainlistviewholder.class,
                myref
        ) {
            @Override
            protected void populateViewHolder(mainlistviewholder viewHolder, Modellist model, int position) {
                viewHolder.cardtextmain.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.cardimgmain);
                Modellist clickitem=model;
                viewHolder.setItemclicklistner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean islongpressed) {
                        Intent intent=new Intent(Listfoods.this,listcategory.class);
                        intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(intent);


                    }
                });

            }
        };
        rv.setAdapter(adapter);


    }

    private void showdialog() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(Listfoods.this);
        alertdialog.setTitle("Add New Category");
        alertdialog.setMessage("Please fill full information");

        LayoutInflater inflator=this.getLayoutInflater();
        View add_new=inflator.inflate(R.layout.inputoutput,null);
        editinput= (EditText) add_new.findViewById(R.id.editinput);
        fbtnselect= (FButton) add_new.findViewById(R.id.fbtnselect);
        fbtnupload= (FButton) add_new.findViewById(R.id.fbtnupload);
        fbtnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),choose_img);

            }
        });
        fbtnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadimg();
            }
        });
        alertdialog.setView(add_new);
        alertdialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);


        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if(modellist!=null){
                    myref.push().setValue(modellist);
                }

            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertdialog.show();

    }

    private void uploadimg() {
        if(uri!=null)
        {
            final ProgressDialog mdialog=new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();


            String name=editinput.getText().toString();
            final StorageReference mref=storageref.child("images/"+name);
            mref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Uploaded!!!",Toast.LENGTH_SHORT).show();
                    mref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            modellist=new Modellist(uri.toString(),editinput.getText().toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("Uploaded "+progress+"%");
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==choose_img && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            fbtnselect.setText("Image Selected");

        }
    }



    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_menu){

        }
        if(id==R.id.nav_cart){

        }
        if(id==R.id.nav_order){
            Intent intent=new Intent(Listfoods.this,OrderStatus.class);
            startActivity(intent);

        }

        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(mtoggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)) {
            showupdatedialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else{
            showdeleteialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showdeleteialog(String key) {
        myref.child(key).removeValue();
    }

    private void showupdatedialog(final String key, final Modellist item) {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(Listfoods.this);
        alertdialog.setTitle("Update Category");
        alertdialog.setMessage("Please fill full information");

        LayoutInflater inflator=this.getLayoutInflater();
        View add_new=inflator.inflate(R.layout.inputoutput,null);
        editinput= (EditText) add_new.findViewById(R.id.editinput);
        fbtnselect= (FButton) add_new.findViewById(R.id.fbtnselect);
        //editimg= (ImageView) add_new.findViewById(R.id.editimg);
        fbtnupload= (FButton) add_new.findViewById(R.id.fbtnupload);
        editinput.setText(item.getName());
        fbtnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),choose_img);

            }
        });
        fbtnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeimg(item);
            }
        });
        alertdialog.setView(add_new);
        alertdialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);


        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setName(editinput.getText().toString());
                myref.child(key).setValue(item);

            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertdialog.show();

    }
    private void changeimg(final Modellist item) {
        if(uri!=null)
        {
            final ProgressDialog mdialog=new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();


            String name=editinput.getText().toString();
            final StorageReference mref=storageref.child("images/"+name);
            mref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Uploaded!!!",Toast.LENGTH_SHORT).show();
                    mref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("Uploaded "+progress+"%");
                }
            });

        }
    }
}
