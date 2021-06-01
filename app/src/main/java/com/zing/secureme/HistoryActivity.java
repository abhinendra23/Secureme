package com.zing.secureme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zing.secureme.Adapter.HistoryAdapter;
import com.zing.secureme.Model.History;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    FirebaseUser user;
  HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.my_post_recyclerview);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase =  FirebaseDatabase.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        Query baseQuery = firebaseDatabase.getReference("Users").child(user.getUid()).child("myHistory");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        DatabasePagingOptions<History> options = new DatabasePagingOptions.Builder<History>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery,config,History.class)
                .build();

        historyAdapter = new HistoryAdapter(options,HistoryActivity.this);
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.startListening();
    }
}