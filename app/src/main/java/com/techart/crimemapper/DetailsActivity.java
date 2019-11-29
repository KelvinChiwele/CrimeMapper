package com.techart.crimemapper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.models.Profile;

/**
 *  Handles actions related to reporting cases
 */

public class DetailsActivity extends AppCompatActivity{
    private RecyclerView rvCategory;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        rvCategory = findViewById(R.id.rv_story);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.VERTICAL,
                false ) );
        Query query = FireBaseUtils.db.collection(Constants.SUSPECT_KEY);//.orderBy("createdAt",Query.Direction.ASCENDING);
        bindCategory(query);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu resource to be inflated
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * Binds view to the recycler view
     */
    private void bindCategory(Query query) {
        FirestoreRecyclerOptions<Profile> response = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(query, Profile.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Profile, SuspectViewHolder>(response) {
            @Override
            public void onBindViewHolder(SuspectViewHolder viewHolder, int position, Profile model) {
                viewHolder.tvName.setText(model.getFirstName());
                viewHolder.tvAge.setText(model.getAge());
                viewHolder.tvNrc.setText(model.getNrc());
                viewHolder.tvResidence.setText(model.getResidence());
            }

            @Override
            public SuspectViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_suspects, group, false);
                return new SuspectViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
            }
        };
        rvCategory.setAdapter(firestoreRecyclerAdapter);
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                Intent detailsIntent = new Intent(DetailsActivity.this, AddSuspectDialog.class);
                startActivity(detailsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}