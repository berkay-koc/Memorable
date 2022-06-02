package com.example.memorable;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DesignPageActivity extends AppCompatActivity {

    EditText title, description, password, emoji;
    TextView emojiText, locationText;
    ImageView location, imageButton, imageHolder;
    int emojiIndex = -1;
    Toolbar toolbar;
    Uri imageUri;
    String locationString, emojiItem, date;
    DatePickerDialog datePickerDialog;
    Button createButton, datePickerButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayAdapter<String> adapterItems;
    AutoCompleteTextView autoCompleteTextView;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_page_layout);
        initDatePicker();
        autoCompleteTextView = findViewById(R.id.emoji);
        List<Integer> emojiList= Arrays.asList(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F61A);
        adapterItems = new ArrayAdapter<String>(this, R.layout.dropdown_item, getEmojiListByUnicode(emojiList));
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emojiItem = parent.getItemAtPosition(position).toString();
                emojiIndex = position;
                autoCompleteTextView.setHint(emojiItem);
            }
        });
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        password = findViewById(R.id.password);
        location = findViewById(R.id.location);
        imageButton = findViewById(R.id.imageOrVideo);
        locationText = findViewById(R.id.locationText);
        imageHolder = findViewById(R.id.imageHolder);
        createButton = (Button) findViewById(R.id.createButton);
        datePickerButton = (Button) findViewById(R.id.datePickerButton);
        datePickerButton.setText(getTodaysDate());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        date = getTodaysDate();
        toolbar = findViewById(R.id.toolbarDesign);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Image operations*/
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imageHolder.setImageURI(imageUri);
                    }
                }
            }
        });
        /*Location operations*/
        location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(DesignPageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(DesignPageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location != null){
                                Geocoder geocoder = new Geocoder(DesignPageActivity.this, Locale.getDefault());
                                try{
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    locationString = addresses.get(0).getAddressLine(0);
                                    locationText.setText(locationString);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                else{
                    ActivityCompat.requestPermissions(DesignPageActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }
        });
        /*File write operations*/
        createButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Writer output;
                File file = new File(getApplicationContext().getFilesDir() + "/memories.json");
                int lines = countLines(getApplicationContext().getFilesDir() + "/memories.json");
                JSONObject memory = new JSONObject();
                try {
                    if(title == null || title.length() == 0){
                        Toast.makeText(DesignPageActivity.this, "Please Enter Title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(emojiIndex == -1){
                        Toast.makeText(DesignPageActivity.this, "Please Choose Your Feelings", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(description == null || description.length() == 0){
                        Toast.makeText(DesignPageActivity.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(locationString == null || locationString.length() == 0){
                        Toast.makeText(DesignPageActivity.this, "Please Enter Your Location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(date == null || date.length() == 0){
                        Toast.makeText(DesignPageActivity.this, "Please Enter Date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else if(imageUri == null){
                        Toast.makeText(DesignPageActivity.this, "Please Add an Image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        memory.put("id", Integer.toString(lines));
                        memory.put("title", title.getText().toString());
                        memory.put("emoji", emojiIndex);
                        memory.put("description", description.getText().toString());
                        memory.put("date", date);
                        memory.put("location", locationString);
                        memory.put("imageUri", imageUri);
                        System.out.println(imageUri);
                        memory.put("password", password.getText().toString());
                        output = new BufferedWriter(new FileWriter(file, true));
                        output.write(memory.toString());
                        output.write("\n");
                        output.close();
                        Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(activity2Intent);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* Menu initializer*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public static int countLines(String fileName) {
        int lines = 0;
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        lines++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /* DatePicker operations*/
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month++;
        return makeDateString(day, month, year);
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                date = makeDateString(dayOfMonth, month, year);
                datePickerButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "Jan";
        else if(month == 2)
            return "Feb";
        else if(month == 3)
            return "Mar";
        else if(month == 4)
            return "Apr";
        else if(month == 5)
            return "May";
        else if(month == 6)
            return "Jun";
        else if(month == 7)
            return "Jul";
        else if(month == 8)
            return "Aug";
        else if(month == 9)
            return "Sep";
        else if(month == 10)
            return "Oct";
        else if(month == 11)
            return "Nov";
        else if(month == 12)
            return "Dec";
        return "ERROR";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    /* Emoji operations*/
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public List<String> getEmojiListByUnicode(List<Integer> list){
        List<String> emojiList = new ArrayList<>();
        for (Integer element: list) {
            emojiList.add(getEmojiByUnicode(element));
        }
        return emojiList;
    }

}


