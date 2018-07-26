package com.example.dell.eatitserver.Common;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.dell.eatitserver.Remote.IGeoCoordinates;
import com.example.dell.eatitserver.Remote.RetrofitClient;
import com.example.dell.eatitserver.modelRequest;

/**
 * Created by dell on 2/20/2018.
 */

public class Common {
    public static modelRequest currentrequest;

    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    public static final String baseUrl="https://maps.googleapis.com";
    public static String convertstatus(String code){
        if(code!=null && code.equals("0")){
            return "Shipped";
        }
        else if(code!=null && code.equals("1")){
            return "On my way";
        }
        else
            return "Placed";
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap,int newwidth,int newheight)
    {
        Bitmap scaledbitmap=Bitmap.createBitmap(newwidth,newheight,Bitmap.Config.ARGB_8888);

        float scaleX=newwidth/(float)bitmap.getWidth();
        float scaleY=newheight/(float)bitmap.getHeight();

        float pivotX=0,pivotY=0;

        Matrix scalematrix=new Matrix();
        scalematrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas=new Canvas(scaledbitmap);
        canvas.setMatrix(scalematrix);

        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledbitmap;
    }

}


