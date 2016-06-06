package com.hackmobile.hackmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity implements ImageSourceOptionListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton addImageButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public String FILES = "FILES";
    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        // init recycler view
        recyclerView = (RecyclerView)findViewById(R.id.image_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        addImageButton = (FloatingActionButton)findViewById(R.id.add_image_button);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageSrcOptionDialog().show(getSupportFragmentManager(), "img_src_option");
            }
        });

//        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(this, getPhotoTakenCount());
//        recyclerView.setAdapter(mAdapter);
    }

    private File createImageFile() throws IOException {
        int imageIndex = 0;

        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        List<String> images = new ArrayList<>(Arrays.asList(prefs.getString(FILES, "").split("\n")));

        if (images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                String imageUri = images.get(i);
                String[] imageUriParts = imageUri.split("_");
                if (imageUriParts.length > 1) {
                    String currentIndexStr = imageUriParts[0];
                    try {
                        int currentIndex = Integer.parseInt(currentIndexStr);
                        if (currentIndex >= imageIndex) {
                            imageIndex = currentIndex + 1;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        // Create an image filename
        String imageFile = Integer.valueOf(imageIndex).toString() + "_photo.jpg";

        // Create file
        File dir = new File(this.getExternalFilesDir(null) + "/" + "HackMobilePics");
        File image = new File(dir, imageFile);

        // Save file path so it can be added to shared preferences after the image has been taken
        imageFileName = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // update photo list
            SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(FILES, prefs.getString(FILES, "") + "\n" + imageFileName);
            editor.commit();

            // notify adapter that new photo has been taken and list should update
//            mAdapter.updatePhotoCount(getPhotoTakenCount());
//            mAdapter.notifyDataSetChanged();
        }

        // update empty text appropriately
//        updateEmptyText();
    }


    @Override
    public void onImgurChoice() {
        Toast.makeText(this, "Imgur chosen", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCameraChoice() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
