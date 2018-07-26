package com.example.dell.eatitserver.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.dell.eatitserver.Interface.ItemClickListner;
import com.example.dell.eatitserver.R;
import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Interface.ItemClickListner;

/**
 * Created by dell on 1/29/2018.
 */

public class orderviewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
    public TextView order_id,order_status,order_phone,order_address;
    private ItemClickListner itemclicklistner;

    public orderviewholder(View itemView) {
        super(itemView);
        order_id=(TextView)itemView.findViewById(R.id.order_id);
        order_status=(TextView)itemView.findViewById(R.id.order_status);
        order_phone=(TextView)itemView.findViewById(R.id.order_phone);
        order_address=(TextView)itemView.findViewById(R.id.order_address);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListner(ItemClickListner itemclicklistner) {
        this.itemclicklistner = itemclicklistner;
    }

    @Override
    public void onClick(View v) {
        itemclicklistner.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}
