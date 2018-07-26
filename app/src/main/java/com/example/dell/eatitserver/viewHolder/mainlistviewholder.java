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

public class mainlistviewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
    public ImageView cardimgmain;
    public TextView cardtextmain;
    private ItemClickListner itemclicklistner;

    public mainlistviewholder(View itemView) {
        super(itemView);
        cardimgmain=(ImageView)itemView.findViewById(R.id.cardimgmain);
        cardtextmain= (TextView) itemView.findViewById(R.id.cardtextmain);
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
