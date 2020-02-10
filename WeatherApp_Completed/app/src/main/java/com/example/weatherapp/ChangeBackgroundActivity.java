package com.example.weatherapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ScrollView;

public class ChangeBackgroundActivity extends AppCompatActivity {

    GridLayout gridLayout;
    int[] nImage = {R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);

        ScrollView kayout=findViewById(R.id.ll);
        kayout.setBackgroundResource(BgImage.getInstance().getImageName());
        gridLayout = findViewById(R.id.gl);
        setSingleEvent(gridLayout);
    }

    private void setSingleEvent(GridLayout gl)
    {
        for (int i = 0; i < gl.getChildCount(); i++)
        {
            CardView cardView = (CardView) gl.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedItem(finalI);
                }
            });
        }
    }
    public void clickedItem(int n)
    {
        ScrollView linearLayout=findViewById(R.id.ll);
        BgImage.getInstance().setImageName(nImage[n]);
        linearLayout.setBackgroundResource(BgImage.getInstance().getImageName());
        Intent i = new Intent(ChangeBackgroundActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }
}
