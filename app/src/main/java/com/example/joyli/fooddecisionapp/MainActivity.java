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
                if (i+1==1)
                {
                    selectedPositionText.setText("selected: Korean Food");
                }

                else if (i+1==2)
                {
                    selectedPositionText.setText("selected: Japanese Food");
                }

                else if (i+1==3)
                {
                    selectedPositionText.setText("selected: Western Food");

                }

                else if (i+1==4)
                {
                    selectedPositionText.setText("selected: Chinese Food");
                }

                else if (i+1==5)
                {
                    selectedPositionText.setText("selected: Italian Food");
                }

                else if (i+1==6)
                {
                    selectedPositionText.setText("selected: Thai Food");
                }

                else if (i+1==7)
                {
                    selectedPositionText.setText("selected: Vietnamese Food");
                }

                else if (i+1==8)
                {
                    selectedPositionText.setText("selected: Fast Food");
                }

                else if (i+1==9)
                {
                    selectedPositionText.setText("selected: Cafe");
                }

                else if (i+1==10)
                {
                    selectedPositionText.setText("selected: Buffet");
                }

                else if (i+1==11)
                {
                    selectedPositionText.setText("selected: Dessert");
                }

                else
                {
                    selectedPositionText.setText("selected: Greek Food");
                }


            }
        });


    }




}
