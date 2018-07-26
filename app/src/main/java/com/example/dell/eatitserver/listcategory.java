package com.example.dell.eatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Interface.ItemClickListner;
import com.example.dell.eatitserver.viewHolder.sublistviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;


public class listcategory extends AppCompatActivity {

    RecyclerView rv;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference myref;
    static final int choose_img=101;
    String categoryId="";
    FirebaseRecyclerAdapter<Modelsublist,sublistviewholder> adapter;
    List<String> suggestions=new ArrayList<>();
    FloatingActionButton fab;
    EditText listaddname,listadddes,listaddprice,listadddis;
    FButton fbtnselect,fbtnupload;
    Modelsublist modellist;
    Uri uri;
    StorageReference storageref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcategory);
        storageref= FirebaseStorage.getInstance().getReference();

        fab=(FloatingActionButton)findViewById(R.id.fab);
        rv=(RecyclerView)findViewById(R.id.rv);
        layoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        myref= FirebaseDatabase.getInstance().getReference().child("/Food");
        if(getIntent()!=null)
        {
            categoryId=getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId!=null)
        {
            loadlistFood(categoryId);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });


    }

    private void showdialog() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(listcategory.this);
        alertdialog.setTitle("Add New Category");
        alertdialog.setMessage("Please fill full information");

        LayoutInflater inflator=this.getLayoutInflater();
        View add_new=inflator.inflate(R.layout.listcategoryaddanother,null);
        listaddname= (EditText) add_new.findViewById(R.id.listaddname);
        listadddes= (EditText) add_new.findViewById(R.id.listadddes);
        listaddprice= (EditText) add_new.findViewById(R.id.listaddprice);
        listadddis= (EditText) add_new.findViewById(R.id.listadddis);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==choose_img && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            fbtnselect.setText("Image Selected");

        }
    }


    private void uploadimg() {
        if (uri != null) {
            final ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();


            //String name = UUID.randomUUID().toString();
            final StorageReference mref = storageref.child("images/" );
            mref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    mref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            modellist = new Modelsublist();
                            modellist.setName(listaddname.getText().toString());
                            modellist.setDescription(listadddes.getText().toString());
                            modellist.setPrice(listaddprice.getText().toString());
                            modellist.setDiscount(listadddis.getText().toString());
                            modellist.setMenuId(categoryId);
                            modellist.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("Uploaded " + progress + "%");
                }
            });


        }
    }



    private void loadlistFood(String categoryId) {

        adapter=new FirebaseRecyclerAdapter<Modelsublist, sublistviewholder>(
                Modelsublist.class,
                R.layout.custom_subrow,
                sublistviewholder.class,
                myref.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(sublistviewholder viewHolder, Modelsublist model, int position) {
                viewHolder.cardtext.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.cardimg);
                final Modelsublist clickitem=model;
                viewHolder.setItemclicklistner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean islongpressed) {

                    }
                });

            }
        };
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showupdatedialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else{
            showdeleteialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showdeleteialog(String key) {
        myref.child(key).removeValue();
    }

    private void showupdatedialog(final String key, final Modelsublist item) {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(listcategory.this);
        alertdialog.setTitle("Update Category");
        alertdialog.setMessage("Please fill full information");

        LayoutInflater inflator=this.getLayoutInflater();
        View add_new=inflator.inflate(R.layout.listcategoryaddanother,null);
        listaddname= (EditText) add_new.findViewById(R.id.listaddname);
        listadddes= (EditText) add_new.findViewById(R.id.listadddes);
        listaddprice= (EditText) add_new.findViewById(R.id.listaddprice);
        listadddis= (EditText) add_new.findViewById(R.id.listadddis);
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
                changeimg(item);
            }
        });
        alertdialog.setView(add_new);
        alertdialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);


        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setName(listaddname.getText().toString());
                item.setDescription(listadddes.getText().toString());
                item.setPrice(listaddprice.getText().toString());
                item.setDiscount(listadddis.getText().toString());
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
    private void changeimg(final Modelsublist item) {
        if(uri!=null)
        {
            final ProgressDialog mdialog=new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();


            //String name=editinput.getText().toString();
            final StorageReference mref=storageref.child("images/");
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
