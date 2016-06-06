package com.hackmobile.hackmobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity implements ImageSourceOptionListener {

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
//        mAdapter = new MyAdapter(this, getPhotoTakenCount());
//        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onImgurChoice() {
        Toast.makeText(this, "Imgur chosen", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCameraChoice() {
        Toast.makeText(this, "Camera chosen", Toast.LENGTH_LONG).show();
    }
}
