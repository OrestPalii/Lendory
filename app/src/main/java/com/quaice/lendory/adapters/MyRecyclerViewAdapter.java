package com.quaice.lendory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.quaice.lendory.R;
import com.quaice.lendory.activities.AdvReview;
import com.quaice.lendory.activities.MainActivity;
import com.quaice.lendory.activities.YourAdverts;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public static Adv current;
    private ArrayList<Adv> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private DatabaseReference acc;
    private boolean yourlist;
    StorageReference mImageStorage, ref;
    Context context;

    public MyRecyclerViewAdapter(Context context, ArrayList<Adv> list, boolean yourlist) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.yourlist = yourlist;
        mImageStorage = FirebaseStorage.getInstance(Const.STORAGE_URL).getReference();
        ref = mImageStorage.child("images/");
        FirebaseDatabase database = FirebaseDatabase.getInstance(Const.DATABASE_URL);
        acc = database.getReference("profiles");
    }

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
        holder.location.setText(list.get(position).getLocation());

        if (yourlist){
            holder.settcard.setVisibility(View.VISIBLE);
            holder.delcard.setVisibility(View.VISIBLE);
            holder.likecard.setVisibility(View.INVISIBLE);
            holder.lockcard.setVisibility(View.INVISIBLE);
        }

        if (MainActivity.yourAccount.checkifconsist(list.get(position).getHashnumber()))
            holder.like.setImageResource(R.drawable.liked_heart);
        else
            holder.like.setImageResource(R.drawable.heart);

        try{
            if(0 < list.get(position).getImages().size()) {
                ref.child(list.get(position).getImages().get(0) + "/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            Glide.with(context).load(downUri.toString()).into(holder.image);
                            if(1 < list.get(position).getImages().size()) {
                                ref.child(list.get(position).getImages().get(1) + "/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downUri = task.getResult();
                                            Glide.with(context).load(downUri.toString()).into(holder.second_image);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
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
                    holder.like.setImageResource(R.drawable.heart);
                }else{
                    MainActivity.yourAccount.addnewliked(list.get(position).getHashnumber());
                    acc.child(MainActivity.yourAccount.getPhonenumber()).setValue( MainActivity.yourAccount);
                    holder.like.setImageResource(R.drawable.liked_heart);
                }
            }
        });
        holder.settcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YourAdverts.hashNumber = list.get(position).getHashnumber();
                YourAdverts.showEditDialog(list.get(position), context);
            }
        });
        holder.delcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YourAdverts.hashNumber = list.get(position).getHashnumber();
                YourAdverts.deleteAdv();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, description, count, location;
        private ImageView image, second_image, like;
        private CardView cardView, settcard, likecard, delcard, lockcard;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card);
            second_image = itemView.findViewById(R.id.image_more);
            count = itemView.findViewById(R.id.image_count);
            like = itemView.findViewById(R.id.heart);
            location = itemView.findViewById(R.id.location);
            settcard = itemView.findViewById(R.id.settings);
            likecard = itemView.findViewById(R.id.likecard);
            delcard = itemView.findViewById(R.id.delete);
            lockcard = itemView.findViewById(R.id.lockcard);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

