package olx.tcardoso.olxapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.adapter.AdapterAdvertisement;
import olx.tcardoso.olxapp.helper.ConfigurationFirebase;
import olx.tcardoso.olxapp.helper.RecyclerItemClickListener;
import olx.tcardoso.olxapp.model.Advertisement;

public class MyAdvertisementActivity extends AppCompatActivity {

    //configurate recyclerView
    private RecyclerView recyclerAdvertisement;
    private List<Advertisement> advertisements = new ArrayList<>();
    private AdapterAdvertisement adapterAdvertisement;
    private DatabaseReference advertisementUserRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_advertisement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Configurations initial
        advertisementUserRef = ConfigurationFirebase.getFirebase()
                .child("meus_anuncios")
                .child(ConfigurationFirebase.getIdUser());

        initializeComponents();
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterAdvertisementActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurate ReyclerView
        recyclerAdvertisement.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdvertisement.setHasFixedSize(true);
        //Configure Adapter
        adapterAdvertisement = new AdapterAdvertisement(advertisements, this);
        recyclerAdvertisement.setAdapter(adapterAdvertisement);

        //recovery advertisement for user
        recoveryAdvertisement();

        //add event of click in recyclerview
        recyclerAdvertisement.addOnItemTouchListener(

                new RecyclerItemClickListener(
                        this, recyclerAdvertisement,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Advertisement advertisementSelected = advertisements.get(position);
                                advertisementSelected.remove();

                                adapterAdvertisement.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void recoveryAdvertisement() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        advertisementUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                advertisements.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    advertisements.add(ds.getValue(Advertisement.class));
                }

                Collections.reverse(advertisements);
                adapterAdvertisement.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeComponents() {

        recyclerAdvertisement = findViewById(R.id.recyclerAdvertisement);
    }

}
