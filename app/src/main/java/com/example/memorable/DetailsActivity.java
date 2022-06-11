package com.example.memorable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DetailsActivity extends AppCompatActivity {
    TextView dateDetail, titleDetail, emojiDetail, descriptionDetail, locationDetail;
    ImageView imageHolderDetail;
    Toolbar toolbar;
    String imgUri;
    String id;
    String isDeleted;
    Dialog dialog;
    Button yesButton, noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        List<Integer> emojiList = Arrays.asList(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F61A);
        dateDetail = findViewById(R.id.dateDetail);
        titleDetail = findViewById(R.id.titleDetail);
        emojiDetail = findViewById(R.id.emojiDetail);
        descriptionDetail = findViewById(R.id.descriptionDetail);
        descriptionDetail.setScroller(new Scroller(this));
        descriptionDetail.setVerticalScrollBarEnabled(true);
        descriptionDetail.setMovementMethod(new ScrollingMovementMethod());
        imageHolderDetail = findViewById(R.id.imageHolderDetail);
        locationDetail = findViewById(R.id.locationDetail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String description = extras.getString("description");
            imgUri = extras.getString("imgUri");
            String location = extras.getString("location");
            String emoji = extras.getString("emoji");
            String date = extras.getString("date");
            id = extras.getString("id");
            isDeleted = extras.getString("isDeleted");
            dateDetail.setText(date);
            descriptionDetail.setText(description);
            titleDetail.setText(title);
            imageHolderDetail.setImageURI(Uri.parse(imgUri));
            locationDetail.setText(location);
            locationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                    intent.putExtra("locationName", locationDetail.getText().toString());
                    DetailsActivity.this.startActivity(intent);
                }
            });
            emojiDetail.setText(new String(Character.toChars(emojiList.get(Integer.parseInt(emoji)))));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_edit_delete_menu, menu);
        return true;
    }

    /*Menu item operations*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.convertToPDF:
                try {
                    createPDF();
                    Toast toast = Toast.makeText(DetailsActivity.this, "PDF file created.", Toast.LENGTH_LONG);
                    toast.show();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(DetailsActivity.this, "An Error Occured.", Toast.LENGTH_LONG);
                    toast.show();
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.shareWithOthers:
                try {
                    createPDF();
                    sharePdf();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.deleteItem:
                dialog = new Dialog(DetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.delete_dialog);
                yesButton = dialog.findViewById(R.id.yesButton);
                noButton = dialog.findViewById(R.id.noButton);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            deleteMemory();
                            dialog.dismiss();
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.editItem:
                try {
                    editMemory();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    /*Edit memory button operations*/
    private void editMemory() throws IOException, JSONException {
        List<Memory> memories = new ArrayList<>();
        Gson gson = new Gson();
        InputStream file = new FileInputStream(getApplicationContext().getFilesDir() + "/memories.json");
        Scanner sc = new Scanner(file);
        Memory memoryObj;
        while (sc.hasNextLine()) {
            memoryObj = gson.fromJson(sc.nextLine(), Memory.class);
            memories.add(memoryObj);
        }
        for (Memory memory : memories) {
            if (memory.id.equals(id)) {
                Intent intent = new Intent(DetailsActivity.this, DesignPageActivity.class);
                intent.putExtra("id", memory.id);
                intent.putExtra("title", titleDetail.getText().toString());
                intent.putExtra("emoji", memory.emoji);
                intent.putExtra("imgUri", imgUri);
                intent.putExtra("description", descriptionDetail.getText().toString());
                intent.putExtra("date", dateDetail.getText().toString());
                intent.putExtra("location", locationDetail.getText().toString());
                intent.putExtra("password", memory.password);
                intent.putExtra("isDeleted", "0");
                finish();
                DetailsActivity.this.startActivity(intent);
            }
        }
    }

    /*Delete memoryoperations*/
    private void deleteMemory() throws IOException, JSONException {
        List<Memory> memories = new ArrayList<>();
        Gson gson = new Gson();
        File memoryFile = new File(getApplicationContext().getFilesDir() + "/memories.json");
        InputStream file = new FileInputStream(getApplicationContext().getFilesDir() + "/memories.json");
        Scanner sc = new Scanner(file);
        Memory memoryObj;
        while (sc.hasNextLine()) {
            memoryObj = gson.fromJson(sc.nextLine(), Memory.class);
            memories.add(memoryObj);
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(memoryFile, false));
        JSONObject memoryJson = new JSONObject();
        for (Memory memory : memories) {
            memoryJson.put("id", memory.id);
            memoryJson.put("title", memory.title);
            memoryJson.put("emoji", memory.emoji);
            memoryJson.put("description", memory.description);
            memoryJson.put("date", memory.date);
            memoryJson.put("location", memory.location);
            memoryJson.put("imageUri", memory.imageUri);
            memoryJson.put("password", memory.password);
            if (memory.id.equals(id) || memory.isDeleted.equals("1")) {
                memoryJson.put("isDeleted", "1");
            } else {
                memoryJson.put("isDeleted", "0");
            }
            output.write(memoryJson.toString());
            output.write("\n");
        }
        output.close();
    }

    /*Create PDF operations*/
    private void createPDF() throws IOException {
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(0) + dateDetail.getText().toString().charAt(1) + + dateDetail.getText().toString().charAt(2) + dateDetail.getText().toString().charAt(3) + dateDetail.getText().toString().charAt(4) + ".pdf");
        f.getParentFile().mkdirs();
        f.createNewFile();
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(new PdfWriter(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(0) + dateDetail.getText().toString().charAt(1) + + dateDetail.getText().toString().charAt(2) + dateDetail.getText().toString().charAt(3) + dateDetail.getText().toString().charAt(4) + ".pdf"));
        Document doc = new Document(pdfDoc);
        Paragraph paragraph;

        paragraph = new Paragraph(this.dateDetail.getText().toString());
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.RIGHT);
        doc.add(paragraph);

        paragraph = new Paragraph("Title:").setBold();
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        doc.add(paragraph);

        paragraph = new Paragraph(this.titleDetail.getText().toString());
        paragraph.setWidth(580);
        paragraph.setBackgroundColor(ColorConstants.WHITE);
        doc.add(paragraph);

        paragraph = new Paragraph("Description:").setBold();
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        doc.add(paragraph);

        paragraph = new Paragraph(this.descriptionDetail.getText().toString());
        paragraph.setWidth(580);
        paragraph.setBackgroundColor(ColorConstants.WHITE);
        doc.add(paragraph);

        paragraph = new Paragraph("Image:").setBold();
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        doc.add(paragraph);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imgUri));
        Bitmap dspBmp = Bitmap.createScaledBitmap(bitmap, (int) bitmap.getWidth() / 4, (int) bitmap.getHeight() / 4, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dspBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        doc.add(image);

        paragraph = new Paragraph("Location:").setBold();
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        doc.add(paragraph);

        paragraph = new Paragraph(this.locationDetail.getText().toString());
        paragraph.setWidth(580);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        paragraph.setBackgroundColor(ColorConstants.WHITE);
        doc.add(paragraph);


        doc.close();
    }

    /*Share PDF operations*/
    private void sharePdf() throws IOException {
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(0) + dateDetail.getText().toString().charAt(1) + + dateDetail.getText().toString().charAt(2) + dateDetail.getText().toString().charAt(3) + dateDetail.getText().toString().charAt(4) + ".pdf");
        f.getParentFile().mkdirs();
        f.createNewFile();
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(0) + dateDetail.getText().toString().charAt(1) + + dateDetail.getText().toString().charAt(2) + dateDetail.getText().toString().charAt(3) + dateDetail.getText().toString().charAt(4) + ".pdf");
        Uri uri = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + "." + getLocalClassName() + ".provider", file);
        if (file.exists()) {
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share PDF"));
        }
    }

}