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
import android.util.Log;
import android.view.View;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity implements ImageSourceOptionListener {
    private static final String LOG_TAG = "ListActivity";
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageListAdapter adapter;
    private FloatingActionButton addImageButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public String FILES = "FILES";
    private String imageFileName;
    private Random rand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rand = new Random();
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

        // specify an adapter
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private File createImageFile() throws IOException {
        int imageIndex = 0;

        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        List<String> images = new ArrayList<>(Arrays.asList(prefs.getString(FILES, "").split("\n")));

        if (images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                String imageUri = images.get(i);
                String[] absFileNameParts = imageUri.split("/");
                String imageRelativeFileName = absFileNameParts[absFileNameParts.length-1];
                String[] imageUriParts = imageRelativeFileName.split("_");
                if (imageUriParts.length > 1) {
                    String currentIndexStr = imageUriParts[0];
                    try {
                        int currentIndex = Integer.parseInt(currentIndexStr);
                        if (currentIndex >= imageIndex) {
                            imageIndex = currentIndex + 1;
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "PARSING ERROR - " + e.toString());
                    }
                }
            }
        }

        // Create an image filename
        String imageFile = Integer.valueOf(imageIndex).toString() + "_photo.jpg";
        // Create file
        File dir = new File(this.getExternalFilesDir(null) + "/" + "HackMobilePics");
        File image = new File(dir, imageFile);
        Log.d(LOG_TAG, dir.toString());
        image.getParentFile().mkdirs();
        if (!image.exists()) {
            image.createNewFile();
        }

        // Save file path so it can be added to shared preferences after the image has been taken
        imageFileName = "file:"+image.getAbsolutePath();

        return image;
    }

    private void addImageToSharedPrefs(String uri) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String files = prefs.getString(FILES, "");
        if (files.equals("")) {
            editor.putString(FILES, uri);
        }
        else {
            editor.putString(FILES, files + "\n" + uri);
        }
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d(LOG_TAG, imageFileName);
            // update photo list
            addImageToSharedPrefs(imageFileName);

            // notify adapter that new photo has been taken and list should update
            adapter.notifyDataSetChanged();
        }

        // update empty text appropriately
//        updateEmptyText();
    }

    @Override
    public void onImgurChoice() {
        fetchRandomImageId();
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
                Log.d(LOG_TAG, "done createImageFile");
            } catch (IOException e) {
                Log.d(LOG_TAG, e.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void fetchRandomImageId() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/random/random/0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(LOG_TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                String imageId = convertResponseToUrl(response);
                ListActivity.this.receivedRandomImageId(imageId);
            }
        });
    }

    public String convertResponseToUrl(Response response) {
        String url = null;
        try {
            JSONObject responseJson = new JSONObject(response.body().string());
            int ind = 0;
            JSONArray data = responseJson.getJSONArray("data");
            int length = data.length();
            while(ind < length) {
                JSONObject obj = data.getJSONObject(rand.nextInt(length));
                if(!obj.getBoolean("is_album") && !obj.getBoolean("nsfw") && (obj.getString("type").contains("jpeg") || obj.getString("type").contains("png"))) {
                    url = obj.getString("link");
                    break;
                }
                ind++;
            }
            Log.d(LOG_TAG, url);
        } catch(IOException e) {
            Log.d(LOG_TAG, e.toString());
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
        }
        return url;
    }

    public void receivedRandomImageId(final String url) {
        runOnUiThread(new Thread(new Runnable() {
            public void run() {
                addImageToSharedPrefs(url);
                adapter.notifyDataSetChanged();
            }
        }));
    }
}
