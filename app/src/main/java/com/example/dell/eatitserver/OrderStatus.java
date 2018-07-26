package com.example.dell.eatitserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Interface.ItemClickListner;
import com.example.dell.eatitserver.viewHolder.orderviewholder;
import com.example.dell.eatitserver.viewHolder.orderviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    RecyclerView rv;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference myref;
    FirebaseRecyclerAdapter<modelRequest,orderviewholder> adapter;
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        spinner=(MaterialSpinner)findViewById(R.id.spinner);

        rv=(RecyclerView)findViewById(R.id.rv);
        layoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        myref= FirebaseDatabase.getInstance().getReference("Requests");
        adapter=new FirebaseRecyclerAdapter<modelRequest, orderviewholder>(
                modelRequest.class,
                R.layout.order_layout,
                orderviewholder.class,
                myref
        ) {
            @Override
            protected void populateViewHolder(orderviewholder viewHolder, final modelRequest model, int position) {
                viewHolder.order_id.setText(adapter.getRef(position).getKey());
                viewHolder.order_address.setText(model.getAddress());
                viewHolder.order_phone.setText(model.getPhone());
                viewHolder.order_status.setText(Common.convertstatus(model.getStatus()));

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean islongpressed) {
                        Intent intent=new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentrequest=model;
                        startActivity(intent);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
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

    private void showupdatedialog(final String key, final modelRequest item) {

       final AlertDialog.Builder alertdialog=new AlertDialog.Builder(OrderStatus.this);
        alertdialog.setTitle("Update Order");
        alertdialog.setMessage("Please Choose Status");

        LayoutInflater inflator=this.getLayoutInflater();
        final View add_new=inflator.inflate(R.layout.updateshippingdialog,null);
        spinner= (MaterialSpinner) add_new.findViewById(R.id.spinner);
        spinner.setItems("Placed","On the way","Shipped");
        alertdialog.setView(add_new);
        final String localkey=key;
        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

               item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                myref.child(localkey).setValue(item);

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
}

