package com.example.dailyselfie;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

public class ImageDetailsActivity extends AppCompatActivity {
    ImageView large_image;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        large_image = findViewById(R.id.large_image);
        tb = findViewById(R.id.toolbar2);
        setSupportActionBar(tb);
        String s = getIntent().getStringExtra("image_path");
        large_image.setImageBitmap(BitmapFactory.decodeFile(s));
    }
}