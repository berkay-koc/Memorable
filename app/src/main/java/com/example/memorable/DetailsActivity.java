package com.example.memorable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    TextView dateDetail, titleDetail, emojiDetail, descriptionDetail, locationDetail;
    ImageView imageHolderDetail;
    Toolbar toolbar;
    String imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        List<Integer> emojiList= Arrays.asList(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F61A);
        dateDetail = findViewById(R.id.dateDetail);
        titleDetail = findViewById(R.id.titleDetail);
        emojiDetail = findViewById(R.id.emojiDetail);
        descriptionDetail = findViewById(R.id.descriptionDetail);
        imageHolderDetail = findViewById(R.id.imageHolderDetail);
        locationDetail = findViewById(R.id.locationDetail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String title = extras.getString("title");
            String description = extras.getString("description");
            imgUri = extras.getString("imgUri");
            String location = extras.getString("location");
            String emoji = extras.getString("emoji");
            String date = extras.getString("date");
            dateDetail.setText(date);
            descriptionDetail.setText(description);
            titleDetail.setText(title);
            imageHolderDetail.setImageURI(Uri.parse(imgUri));
            locationDetail.setText(location);
            emojiDetail.setText(new String(Character.toChars(emojiList.get(Integer.parseInt(emoji)))));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.convertToPDF:
                try {
                    createPDF();
                    System.out.println("AAAAAAAA");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    private void createPDF() throws IOException {
        int pageWidth = 420, pageHeight = 594;
        int newLine = 50;
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(4) + dateDetail.getText().toString().charAt(5) + ".pdf";
        File file = new File(filePath);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imgUri));
        Bitmap dspBmp = Bitmap.createScaledBitmap(bitmap, (int)bitmap.getWidth()/2, (int)bitmap.getHeight()/2, true);
        Paint titlePaint = new Paint();
        Paint myPaint = new Paint();
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(60f);
        canvas.drawText(titleDetail.getText().toString(), 100, 150, titlePaint);
        titlePaint.setTextSize(30f);
        String[] descriptions = descriptionDetail.getText().toString().split("\\.");
        for (int i = 0; i<descriptions.length; i++) {
            canvas.drawText(descriptions[i].trim() + ".", 100, 200+newLine, titlePaint);
            newLine += 50;
        }

        titlePaint.setTextSize(35f);
        canvas.drawText(locationDetail.getText().toString(), 100, 1800, titlePaint);
        canvas.drawText(dateDetail.getText().toString(), 900, 100, titlePaint);
        page.getCanvas().drawBitmap(dspBmp, 100, 600, myPaint);

        pdfDocument.finishPage(page);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast toast = Toast.makeText(DetailsActivity.this,"PDF file created.", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(DetailsActivity.this,"Error Occured", Toast.LENGTH_LONG);
            toast.show();
        }
        pdfDocument.close();

    }


    private void createPDF2() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imgUri));
        Rect rect = new Rect(0, 0, 100, 100);
        Bitmap dspBmp = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(420, 594, 1).create();
        System.out.println(pageInfo.getContentRect());

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        int x=20, y=25;
        page.getCanvas().drawText(titleDetail.getText().toString(), x, y, paint);
        page.getCanvas().drawText(descriptionDetail.getText().toString(), x, y+15, paint);
        page.getCanvas().drawBitmap(dspBmp, x, y+30, paint);
        pdfDocument.finishPage(page);

        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(4) + dateDetail.getText().toString().charAt(5) + ".pdf";
        File file = new File(filePath);

        try{
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast toast = Toast.makeText(DetailsActivity.this,"PDF file created.", Toast.LENGTH_LONG);
            toast.show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(DetailsActivity.this,"Error Occured", Toast.LENGTH_LONG);
            toast.show();
        }

        pdfDocument.close();
    }
}