package com.example.joyli.fooddecisionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Joyli on 2017-05-31.
 */

public class ViewList extends AppCompatActivity {

    SQLdatabaseActivity myDB;
    ArrayList<User> userList;
    User user;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        ListView listview = (ListView) findViewById(R.id.listview);
        myDB = new SQLdatabaseActivity(this);


        //Header for ListView
        TextView textView = new TextView(this);
        textView.setText("Entry                  Restaurant Name"); //sets the header
        listview.addHeaderView(textView); //adds the header

        //populate an ArrayList<String> from the database and then view it
        userList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        int numRows = data.getCount();
        if (numRows==0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();

        }
        else {
            int i =0;
            while (data.moveToNext()) { //returns a boolean value

                user = new User(data.getString(0), data.getString(1));

                userList.add(i, user);

                System.out.println(data.getString(0)+ "" + data.getString(1));
                System.out.println(userList.get(i).getEntry());
                i++;

            }

            TwoColumn_ListAdapter adapter = new TwoColumn_ListAdapter(this,R.layout.activity_array_list_columns,userList);
            listview.setAdapter(adapter);



        }




    }







}
