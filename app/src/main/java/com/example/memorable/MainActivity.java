package com.example.memorable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Memory memoryObj;
    RecyclerView recyclerView;
    Dialog dialog;
    String pword;
    MemoryAdapter memoryAdapter;
    boolean isEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get permission, if not, exit program here
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        InputStream is = null;
        try {
            is = new FileInputStream(getApplicationContext().getFilesDir() + "/password.txt");
            Scanner sc = new Scanner(is);
            pword = sc.nextLine();
            System.out.println(pword);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (pword != null){
            dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.password_dialog);

            final EditText passwordDialogText = dialog.findViewById(R.id.passwordDialogText);
            ImageView eyeImage = dialog.findViewById(R.id.eyeImage);
            eyeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled) {
                        passwordDialogText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isEnabled = true;
                    }
                    else{
                        passwordDialogText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isEnabled = false;
                    }
                }
            });
            Button setButton = dialog.findViewById(R.id.setButton);
            setButton.setText("Enter");

            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(passwordDialogText.getText().toString().equals(pword)){
                        dialog.dismiss();
                    }
                    else{
                        Toast toast = Toast.makeText(MainActivity.this,"Wrong password. Please try again.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
            dialog.show();
        }
        try {
            fetchMemories();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 160);
        toast.show();
    }

    private void fetchMemories() throws IOException {
        List<Memory> memories = new ArrayList<>();
        Gson gson = new Gson();
        InputStream is = new FileInputStream(getApplicationContext().getFilesDir() + "/memories.json");
        Scanner sc = new Scanner(is);
        while(sc.hasNextLine()){
            memoryObj = gson.fromJson(sc.nextLine(), Memory.class);
            if (memoryObj.isDeleted.equals("0")) {
                memories.add(memoryObj);
            }
        }
        is.close();
        sc.close();
        showMemories(memories);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fetchMemories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMemories(List<Memory> memories) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        memoryAdapter = new MemoryAdapter(memories, this);
        recyclerView.setAdapter(memoryAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else
                    closeNow();
                break;
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            finishAffinity();
        else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void showPasswordDialog(){

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.password_dialog);

        final EditText passwordDialogText = dialog.findViewById(R.id.passwordDialogText);
        Button setButton = dialog.findViewById(R.id.setButton);
        setButton.setText("Set Password");

        ImageView eyeImage = dialog.findViewById(R.id.eyeImage);
        eyeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    passwordDialogText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isEnabled = true;
                }
                else{
                    passwordDialogText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isEnabled = false;
                }
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Writer output;
                String passwordString = passwordDialogText.getText().toString();
                if(passwordString.length() >= 6){
                    File file = new File(getApplicationContext().getFilesDir() + "/password.txt");
                    try {
                        output = new BufferedWriter(new FileWriter(file, false));
                        output.write(passwordString);
                        output.close();
                        Toast toast = Toast.makeText(MainActivity.this,"New password set.", Toast.LENGTH_LONG);
                        toast.show();
                        dialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast toast = Toast.makeText(MainActivity.this,"Password must be at least 6 characters long.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                Intent activity2Intent = new Intent(getApplicationContext(), DesignPageActivity.class);
                startActivity(activity2Intent);
                break;
            case R.id.setPassword:
                showPasswordDialog();
                break;
            case R.id.convertToPDF:
                System.out.println("AAAAAAAA");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}