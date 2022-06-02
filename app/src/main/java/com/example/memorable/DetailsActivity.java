package com.example.memorable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    TextView dateDetail, titleDetail, emojiDetail, descriptionDetail, locationDetail;
    ImageView imageHolderDetail;
    Toolbar toolbar;
    String imgUri;
    ImageData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        List<Integer> emojiList= Arrays.asList(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F61A);
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
                    Toast toast = Toast.makeText(DetailsActivity.this,"PDF file created.", Toast.LENGTH_LONG);
                    toast.show();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(DetailsActivity.this,"An Error Occured.", Toast.LENGTH_LONG);
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
        }
        return true;
    }

    private void createPDF() throws IOException{
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(new PdfWriter(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(4) + dateDetail.getText().toString().charAt(5) + ".pdf"));
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
        Bitmap dspBmp = Bitmap.createScaledBitmap(bitmap, (int)bitmap.getWidth()/4, (int)bitmap.getHeight()/4, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dspBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
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

    private void sharePdf(){
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + titleDetail.getText().toString() + "_" + dateDetail.getText().toString().charAt(4) + dateDetail.getText().toString().charAt(5) + ".pdf");
        Uri uri = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + "." + getLocalClassName() + ".provider", file);
        if(file.exists()) {
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share PDF"));
        }
    }

}