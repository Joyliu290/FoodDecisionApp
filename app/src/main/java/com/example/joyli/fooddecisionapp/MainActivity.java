package com.example.joyli.fooddecisionapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.anupcowkur.wheelmenu.WheelMenu;
import static com.example.joyli.fooddecisionapp.R.id.wheelMenu;


public class MainActivity extends AppCompatActivity {

    private WheelMenu wheelMenu;
    private TextView selectedPositionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelMenu = (WheelMenu) findViewById(R.id.wheelMenu);
        wheelMenu.setDivCount(12);
        wheelMenu.setWheelImage(R.drawable.wheel);
        selectedPositionText =(TextView) findViewById (R.id.selected_position_text);
        selectedPositionText.setText("selected: " + (wheelMenu.getSelectedPosition()+1));

        wheelMenu.setWheelChangeListener(new WheelMenu.WheelChangeListener() {
            @Override
            public void onSelectionChange(int i) {
                selectedPositionText.setText("selected: " +(i + 1));
            }
        });


    }




}
