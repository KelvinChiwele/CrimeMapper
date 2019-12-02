package com.techart.crimemapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.models.Report;
import com.techart.crimemapper.setup.LoginActivity;

/**
    Main activity, first activity to be launched
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    Query query;

    private RecyclerView rvCategory;
    private FloatingActionButton fab;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    query = FireBaseUtils.db.collection(Constants.OCCURANCES_KEY).whereEqualTo(Constants.USER_URL, FireBaseUtils.getUiD());//.orderBy("createdAt",Query.Direction.ASCENDING);
                    bindCategory(query);
                }
            }
        };

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iInformation = new Intent(MainActivity.this,ReportActivity.class);
                startActivity(iInformation);
            }
        });

        rvCategory = findViewById(R.id.rv_story);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.VERTICAL,
                false ) );
       /* Query query = FireBaseUtils.db.collection(Constants.OCCURANCES_KEY).whereEqualTo(Constants.USER_URL,FireBaseUtils.getUiD());//.orderBy("createdAt",Query.Direction.ASCENDING);
        bindCategory(query);*/

        setupDrawer(toolbar);
    }

    /**
     * Initiates nav drawer
     */
    private void setupDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Binds view to the recycler view
     */
    private void bindCategory(Query query) {

        FirestoreRecyclerOptions<Report> response = new FirestoreRecyclerOptions.Builder<Report>()
                .setQuery(query, Report.class)
                .build();

         firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Report, ReportViewHolder>(response) {
            @Override
            public void onBindViewHolder(ReportViewHolder viewHolder, int position, Report model) {
                viewHolder.tvSubject.setText(model.getSubject());
                viewHolder.date.setText(model.getDate());
                viewHolder.tvParticularOfOffence.setText(model.getParticularOfOffence());
                viewHolder.tvStation.setText(getString(R.string.station,model.getStation()));
                viewHolder.tvStatus.setText(model.getStatus());
                viewHolder.tvParticularOfOffence.setText(model.getParticularOfOffence());
                viewHolder.tvDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                        startActivity(detailsIntent);
                    }
                });
            }

            @Override
            public ReportViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_reportrow, group, false);
                return new ReportViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
            }
        };
        rvCategory.setAdapter(firestoreRecyclerAdapter);
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            logOut();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Logs out user
     */
    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }
}
