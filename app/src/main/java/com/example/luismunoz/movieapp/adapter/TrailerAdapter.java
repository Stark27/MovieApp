package com.example.luismunoz.movieapp.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luismunoz.movieapp.R;
import com.example.luismunoz.movieapp.model.Trailer;

import java.net.URI;
import java.util.List;

/**
 * Created by LuisMunoz on 28-05-17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.mContext = context;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MyViewHolder holder, int position) {
        holder.title.setText(trailerList.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_trailer);
            thumbnail = (ImageView) view.findViewById(R.id.image_view_trailer);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        Trailer clickedDataItem = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+videoId));
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            appIntent.putExtra("VIDEO_ID", videoId);
                            mContext.startActivity(appIntent);


                        }catch (ActivityNotFoundException e) {
                            webIntent.putExtra("VIDEO_ID", videoId);
                            mContext.startActivity(webIntent);
                        }

                        Toast.makeText(view.getContext(), "You Clicked " + clickedDataItem.getName(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
