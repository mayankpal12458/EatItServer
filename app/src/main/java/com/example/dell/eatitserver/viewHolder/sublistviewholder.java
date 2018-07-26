package com.example.dell.eatitserver.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.eatitserver.Common.Common;
import com.example.dell.eatitserver.Interface.ItemClickListner;
import com.example.dell.eatitserver.R;

/**
 * Created by dell on 2/4/2018.
 */

public class sublistviewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
    public ImageView cardimg;
    public TextView cardtext;
    private ItemClickListner itemclicklistner;

    public sublistviewholder(View itemView) {
        super(itemView);

        cardimg=(ImageView)itemView.findViewById(R.id.cardimg);
        cardtext= (TextView) itemView.findViewById(R.id.cardtext);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }
    public void setItemclicklistner(ItemClickListner itemclicklistner){
        this.itemclicklistner=itemclicklistner;
    }

    @Override
    public void onClick(View v) {
        itemclicklistner.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
