package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<TaskPost> {

    /* This part for using array adapter to show content on a list reuses some code from "ArrayAdapter" (Android developer, 2022b). */
    private  int resourceID;
    public TaskAdapter(
            Context context, int textViewResourceID, List<TaskPost> object){
        super(context,textViewResourceID,object);
        resourceID = textViewResourceID;
    }

    public View getView(int position, View coverView, ViewGroup parent){
        TaskPost task = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        TextView TaskTitle = view.findViewById(R.id.Item_textView_title);
        TaskTitle.setText(task.getTitle());
        return view;
    }
}
/*
Android developers. (2022b). ArrayAdapter. https://developer.android.com/reference/android/widget/ArrayAdapter
 */
