package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import io.socket.client.IO;
import io.socket.client.Socket;


public class MainScreen extends AppCompatActivity {

    private ImageView imageView;
    private Socket socket;
    private ImageButton screenshotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_screen);

        Intent intent = getIntent();
        String IP = intent.getStringExtra("IpAddress");

        final JoystickView joystickRight = (JoystickView) findViewById(R.id.joystickView_right);
        imageView = (ImageView) findViewById(R.id.imageView);
        screenshotButton = (ImageButton) findViewById(R.id.screenshotButton);

        try {
            // Connect to the Socket.IO server
            socket = IO.socket("http://" + IP + ":5000");
            socket.connect();
        } catch (URISyntaxException e) {
            Log.v("Error", e.getMessage());
        }

        // Listen for the 'frame' event
        socket.on("frame", args -> {
            String encodedImage = (String) args[0];

            // Convert the base64 encoded image to a Bitmap
            byte[] decodedString = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decodedString = Base64.getDecoder().decode(encodedImage);
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // Update the ImageView with the received image
            new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));

            screenshotButton.setOnClickListener(arg -> {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String formattedDateTime = sdf.format(calendar.getTime());
                saveBitmapAsImage(getApplicationContext(), bitmap, formattedDateTime);
            });

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the Socket.IO server
        socket.disconnect();
    }

    private void saveBitmapAsImage(Context context, Bitmap bitmap, String fileName) {
        // Get the directory where the image will be saved
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Controller");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a file with the given filename in the directory
        File file = new File(directory, "PIC_" + fileName +  ".png");

        // Save the bitmap to the file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(context,
                    "Image saved successfully on " + directory.getPath() + "PIC_" + fileName,
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
}