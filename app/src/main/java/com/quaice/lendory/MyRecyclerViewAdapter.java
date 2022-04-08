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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.activities.AdvReview;
import com.quaice.lendory.activities.MainActivity;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public static Adv current;
    private ArrayList<Adv> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    StorageReference mImageStorage, ref;
    private DatabaseReference acc;
    Context context;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<Adv> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        mImageStorage = FirebaseStorage.getInstance("gs://lendory-b5d8b.appspot.com/").getReference();
        ref = mImageStorage.child("images/");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lendory-b5d8b-default-rtdb.firebaseio.com/");
        acc = database.getReference("profiles");
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
        if (MainActivity.yourAccount.checkifconsist(list.get(position).getHashnumber()))
            holder.like.setBackgroundResource(R.drawable.liked_heart);
        else
            holder.like.setBackgroundResource(R.drawable.heart);

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

            ref.child(list.get(position).getImages().get(1)+"/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        Glide.with(context).load(downUri.toString()).into(holder.second_image);
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
            }
        });

        try {
            int counter = list.get(position).getImages().size() - 1;
            holder.count.setText("+" + counter);
        }catch (Exception e){};

        holder.like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                boolean liked = MainActivity.yourAccount.checkifconsist(list.get(position).getHashnumber());
                if (liked) {
                    MainActivity.yourAccount.removenewliked(list.get(position).getHashnumber());
                    acc.child(MainActivity.yourAccount.getPhonenumber()).setValue( MainActivity.yourAccount);
                    holder.like.setBackgroundResource(R.drawable.heart);
                }else{
                    MainActivity.yourAccount.addnewliked(list.get(position).getHashnumber());
                    acc.child(MainActivity.yourAccount.getPhonenumber()).setValue( MainActivity.yourAccount);
                    holder.like.setBackgroundResource(R.drawable.liked_heart);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, description, count;
        private ImageView image, second_image, like;
        private CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card);
            second_image = itemView.findViewById(R.id.image_more);
            count = itemView.findViewById(R.id.image_count);
            like = itemView.findViewById(R.id.heart);
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

