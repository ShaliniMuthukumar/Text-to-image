package com.firstapp.artiwrite;



import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputLayout promptLayout =  (TextInputLayout) ((Activity) MainActivity.this).findViewById(R.id.promptLayout);
        TextInputEditText promptET = (TextInputEditText) ((Activity) MainActivity.this).findViewById(R.id.promptET);

        SeekBar width = (SeekBar) ((Activity) MainActivity.this).findViewById(R.id.width);
        SeekBar height =  (SeekBar) ((Activity) MainActivity.this).findViewById(R.id.height);
        SeekBar imageCount = (SeekBar) ((Activity) MainActivity.this).findViewById(R.id.imageCount);

        Button generate = (Button) ((Activity) MainActivity.this).findViewById(R.id.generate);
        RecyclerView recyclerView = (RecyclerView) ((Activity) MainActivity.this).findViewById(R.id.recycler);

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Generating...");

        OnLoaded onLoaded = new OnLoaded() {
            @Override
            public void loaded(ArrayList<String> arrayList) {
                progressDialog.dismiss();
                ImageAdapter adapter = new ImageAdapter(MainActivity.this, arrayList);
                recyclerView.setAdapter(adapter);
            }
        };

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    if (Objects.requireNonNull(promptET.getText()).toString().isEmpty()) {
                        promptLayout.setError("This Field is Required");
                    } else {
                        progressDialog.show();
                        new ImageGenerator(MainActivity.this).generate(promptET.getText().toString(), width.getProgress(), height.getProgress(), imageCount.getProgress(), onLoaded);
                    }
                }
            }
        });
    }
}