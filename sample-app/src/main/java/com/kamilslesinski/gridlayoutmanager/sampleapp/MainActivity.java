package com.kamilslesinski.gridlayoutmanager.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kamilslesinski.gridlayoutmanager.sampleapp.generic.HorizontalGridActivity;
import com.kamilslesinski.gridlayoutmanager.sampleapp.generic.VerticalGridActivity;
import com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide.TVGuideGridActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button horizontalGrid = (Button) findViewById(R.id.button_grid_horizontal);
        assert horizontalGrid != null;
        horizontalGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HorizontalGridActivity.class));
            }
        });
        Button verticalGrid = (Button) findViewById(R.id.button_grid_vertical);
        assert verticalGrid != null;
        verticalGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerticalGridActivity.class));
            }
        });
        Button tvGuideGrid = (Button) findViewById(R.id.button_grid_tv_guide);
        assert tvGuideGrid != null;
        tvGuideGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TVGuideGridActivity.class));
            }
        });
    }
}
