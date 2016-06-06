package com.hackmobile.hackmobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ListActivity extends AppCompatActivity implements ImageSourceOptionListener {
    private static final String LOG_TAG = "ListActivity";
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton addImageButton;
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
        ImageListAdapter adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImgurChoice() {
        Toast.makeText(this, "Imgur chosen", Toast.LENGTH_LONG).show();
        fetchRandomImageId();

    }

    @Override
    public void onCameraChoice() {
        Toast.makeText(this, "Camera chosen", Toast.LENGTH_LONG).show();
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

                String imageId = convertResponseToImageId(response.body().string());
                ListActivity.this.receivedRandomImageId(imageId);
            }
        });
    }

    public String convertResponseToImageId(String response) {
        Log.d(LOG_TAG, response);
        String url = ""; //TODO
        return url;
    }

    public void receivedRandomImageId(String imageId) {
        //todo
    }
}