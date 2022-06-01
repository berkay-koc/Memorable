package com.example.memorable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {
    Context context;
    private List<Memory> memoryList;
    Dialog dialog;
    Memory memoryObj;

    public MemoryAdapter(List<Memory> list, Context context) {
        memoryList = list;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public MemoryAdapter.MemoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MemoryAdapter.MemoryViewHolder holder, int position) {
        MemoryViewHolder viewHolder = (MemoryViewHolder) holder;

        viewHolder.title.setText(memoryList.get(position).getTitle());
        viewHolder.number.setText(Integer.parseInt(memoryList.get(position).id) + 1 + " ");
        viewHolder.todayDate.setText(memoryList.get(position).getDate());
        if (memoryList.get(position).getImageUri() != null)
            viewHolder.imageView.setImageResource(R.drawable.diary_img);
        else {
            viewHolder.imageView.setImageResource(R.drawable.diary_img);
        }

//        viewHolder.rowItemLayout.setOnClickListener((view) ->{
//            System.out.println(viewHolder.number.getText().toString());
//        });
        viewHolder.rowItemLayout.setOnClickListener((view) -> {
            Gson gson = new Gson();
            InputStream is = null;
            try {
                is = new FileInputStream(context.getApplicationContext().getFilesDir() + "/memories.json");
                Scanner sc = new Scanner(is);
                while (sc.hasNextLine()) {
                    //tıklandığında detay ekranına gider
                    memoryObj = gson.fromJson(sc.nextLine(), Memory.class);
                    if (Integer.toString(Integer.parseInt(memoryObj.id) + 1).equals(viewHolder.number.getText().toString().trim())) {
                        if (memoryObj.password.equals("")) {
                            Intent intent = new Intent(context, DetailsActivity.class);
                            intent.putExtra("title", memoryObj.title);
                            intent.putExtra("description", memoryObj.description);
                            intent.putExtra("date", memoryObj.date);
                            intent.putExtra("emoji", memoryObj.emoji); //number actually
                            intent.putExtra("imgUri", memoryObj.imageUri);
                            intent.putExtra("location", memoryObj.location);
                            context.startActivity(intent);
                        } else {
                            String memoryPassword = memoryObj.password;
                            dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.password_dialog);
                            final EditText passwordDialogText = dialog.findViewById(R.id.passwordDialogText);
                            Button setButton = dialog.findViewById(R.id.setButton);
                            setButton.setText("Enter");

                            setButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (memoryPassword.equals(passwordDialogText.getText().toString())) {
                                        Intent intent = new Intent(context, DetailsActivity.class);
                                        intent.putExtra("title", memoryObj.title);
                                        intent.putExtra("description", memoryObj.description);
                                        intent.putExtra("date", memoryObj.date);
                                        intent.putExtra("emoji", memoryObj.emoji); //number actually
                                        intent.putExtra("imgUri", memoryObj.imageUri);
                                        intent.putExtra("location", memoryObj.location);
                                        context.startActivity(intent);
                                        dialog.dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(context, "Wrong password. Please try again.", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                            dialog.show();
                        }
                    }
                }
                is.close();
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }


    public static class MemoryViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout rowItemLayout;
        public ImageView imageView;
        public TextView number, todayDate, title;

        public MemoryViewHolder(View itemView) {
            super(itemView);
            rowItemLayout = itemView.findViewById(R.id.rowItemLayout);
            imageView = itemView.findViewById(R.id.diary_cover);
            title = itemView.findViewById(R.id.title);
            number = itemView.findViewById(R.id.number);
            todayDate = itemView.findViewById(R.id.todayDate);
        }
    }
}
