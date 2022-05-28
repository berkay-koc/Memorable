package com.example.memorable;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DesignPageActivity extends AppCompatActivity {

    EditText title, description, password, emoji;
    TextView emojiText;
    ImageView location, imageUri, imageHolder;
    int unicode;
    String locationString, emojiItem, date;
    DatePickerDialog datePickerDialog;
    Button createButton, datePickerButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayAdapter<String> adapterItems;
    AutoCompleteTextView autoCompleteTextView;

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
                autoCompleteTextView.setHint(emojiItem);
                System.out.println(emojiItem);
            }
        });
        Random rand = new Random();
        unicode = emojiList.get(rand.nextInt(emojiList.size()));
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        password = findViewById(R.id.password);
        location = findViewById(R.id.location);
        imageUri = findViewById(R.id.imageOrVideo);
        imageHolder = findViewById(R.id.imageHolder);
        createButton = (Button) findViewById(R.id.createButton);
        datePickerButton = (Button) findViewById(R.id.datePickerButton);
        datePickerButton.setText(getTodaysDate());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                                    locationString = Double.toString(addresses.get(0).getLatitude()) + " " + Double.toString(addresses.get(0).getLongitude());
                                    System.out.println(locationString);

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

        createButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Writer output;
                int lines = countLines(getApplicationContext().getFilesDir() + "/memories.json");
                File file = new File(getApplicationContext().getFilesDir() + "/memories.json");
                JSONObject memory = new JSONObject();
                try {
                    memory.put("ID", lines);
                    memory.put("Title", title.getText().toString());
                    memory.put("Description", description.getText().toString());
                    memory.put("Date", date);
                    memory.put("Location", locationString);
                    memory.put("Image", imageUri);
                    memory.put("Emoji", emojiItem);
                    memory.put("Password", password.getText().toString());
                    output = new BufferedWriter(new FileWriter(file, true));
                    output.write(memory.toString());
                    output.write("\n");
                    output.close();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static int countLines(String fileName) {

        int lines = 0;

        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            lines = count;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

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

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}
