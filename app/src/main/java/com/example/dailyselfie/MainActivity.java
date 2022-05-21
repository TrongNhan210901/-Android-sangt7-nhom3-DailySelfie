package com.example.dailyselfie;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar tb;
    ListView lv;
    List<String> imagePathList;
    ImageAdapter imageAdapter;
    String currentPhotoPath;

    private static final long INTERVAL_TWO_MINUTES = 1 * 60 * 1000L;
    private PendingIntent mNotificationReceiverPendingIntent;
    private Intent mNotificationReceiverIntent;

    private static final long INITIAL_ALARM_DELAY = 2 * 60 * 100L;
    private static final long REPEAT_ALARM_DELAY = 2 * 60 * 100L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        tb = findViewById(R.id.toolbar);
        lv = findViewById(R.id.image_list);
        setSupportActionBar(tb);
        imagePathList = new ArrayList<>();
        ReadAllFiles();
        imageAdapter = new ImageAdapter(this, imagePathList);
        lv.setAdapter(imageAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
                String entry = (String) parent.getItemAtPosition(position);
                intent.putExtra("image_path", entry);
                startActivity(intent);
            }
        });
        }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.mymenu, m);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.IconCamera:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 1);
                            imagePathList.add(currentPhotoPath);
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                }
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void ReadAllFiles() {
        File directoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {
            String filename = file.getAbsolutePath();
            imagePathList.add(filename);
        }
    }

    private void setupAlarm() {
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);

        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                REPEAT_ALARM_DELAY, mNotificationReceiverPendingIntent);
    }

    public void addIconToActionBar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);    //Icon muốn hiện thị
        actionBar.setDisplayUseLogoEnabled(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            Intent intent = new Intent(this, AlarmNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES,
                    INTERVAL_TWO_MINUTES,
                    pendingIntent);
        }
        catch (Exception exception) {
            Log.d("ALARM", exception.getMessage().toString());
        }


    }


}