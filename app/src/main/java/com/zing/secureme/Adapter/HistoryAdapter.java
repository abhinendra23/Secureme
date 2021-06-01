package com.zing.secureme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zing.secureme.Model.History;
import com.zing.secureme.R;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;

public class HistoryAdapter extends FirebaseRecyclerPagingAdapter<History, HistoryAdapter.MyHolder> {


        Context c;

    private final int[] mColors = {R.color.list_color_2,R.color.list_color_3,R.color.list_color_4,R.color.list_color_5,
            R.color.list_color_6,R.color.list_color_7,R.color.list_color_8,R.color.list_color_9,R.color.list_color_10,R.color.list_color_11};


    public HistoryAdapter(@NonNull DatabasePagingOptions<History> options,Context c) {
        super(options);

        this.c = c;
    }







    @Override
    protected void onBindViewHolder(@NonNull MyHolder viewHolder, int position, @NonNull History model) {
        int bgColor = ContextCompat.getColor(c, mColors[position % 10]);
        viewHolder.cardView.setCardBackgroundColor(bgColor);
   viewHolder.bind(model);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card,parent,false);
        return new MyHolder(view);
    }
    public class MyHolder extends RecyclerView.ViewHolder {
    TextView place1,duration1,numofpeople1,score1;
        CardView cardView;
    public MyHolder(@NonNull View itemView) {
        super(itemView);
        place1 = itemView.findViewById(R.id.placename);
        duration1 = itemView.findViewById(R.id.duration);
        numofpeople1 = itemView.findViewById(R.id.numofpeople);
        score1 = itemView.findViewById(R.id.score);
        cardView = itemView.findViewById(R.id.historyCard);

    }
    public void bind(History model) {
        place1.setText(model.getPlaceName());
        duration1.setText(model.getDuration());
        numofpeople1.setText(model.getNumofpeople());
        score1.setText(model.getScore());



    }
}


}

