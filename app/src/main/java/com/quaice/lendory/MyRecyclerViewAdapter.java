package com.quaice.lendory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.activities.AdvReview;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public static Adv current;
    private ArrayList<Adv> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    StorageReference mImageStorage, ref;
    Context context;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<Adv> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        mImageStorage = FirebaseStorage.getInstance("gs://lendory-b5d8b.appspot.com/").getReference();
        ref = mImageStorage.child("images/");
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        try{
            ref.child(list.get(position).getImages().get(0)+"/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        Glide.with(context).load(downUri.toString()).into(holder.image);
                    }
                }
            });
        }catch (Exception e){}
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current = list.get(position);
                Intent intent = new Intent(context, AdvReview.class);
                context.startActivity(intent);
                //Toast.makeText(context, "" + list.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, description;
        ImageView image;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name); description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);cardView = itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

