package com.example.myapplication.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    /* The parts below for the camera and photo reuses some code from an answer pm stack overflow by Irshad Khan (2014).  */
    public static String compressPhoto(Bitmap bmp){
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        bmp.recycle();
        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        return imageB64;
    }
    public static Bitmap decompressPhoto(String s){
        try{
            byte [] encodeByte = Base64.decode(s,Base64.URL_SAFE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }


}

/*
Reference list:
Irshad Khan. (2014). Convert String to Bitmap. stack overflow. https://stackoverflow.com/questions/23005948/convert-string-to-bitmap
 */