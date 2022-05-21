package com.example.dailyselfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<String> {
    List<String> photoPath;
    Context c;

    public ImageAdapter(Context c, List<String> photoPath) {
        super(c, 0, photoPath);
        this.photoPath = photoPath;
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.image_list_item, parent, false);
        String path = photoPath.get(position);
        TextView date = v.findViewById(R.id.date);
        ImageView image = v.findViewById(R.id.photo);
        String path_text = path.split("/")[path.split("/").length - 1];
        String[] final_text = path_text.split("_");
        date.setText(final_text[0] + "_" + final_text[1]);
        image.setImageDrawable((c.getResources().getDrawable(R.drawable.icon_camera)));
        Drawable d = image.getDrawable();
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        int targetW = b.getWidth();
        int targetH = b.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
//         Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));
//        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        image.setImageBitmap(bitmap);
        return v;
    }
}