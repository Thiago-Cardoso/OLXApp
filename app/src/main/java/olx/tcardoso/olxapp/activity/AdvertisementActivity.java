package olx.tcardoso.olxapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.adapter.AdapterAdvertisement;
import olx.tcardoso.olxapp.helper.ConfigurationFirebase;
import olx.tcardoso.olxapp.helper.RecyclerItemClickListener;
import olx.tcardoso.olxapp.model.Advertisement;

public class AdvertisementActivity extends AppCompatActivity {

    private FirebaseAuth authentication;
    private RecyclerView recyclerAdvertisementPublics;
    private Button buttonRegion, buttonCategory;
    private AdapterAdvertisement adapterAdvertisement;
    private List<Advertisement> listAdvertisements = new ArrayList<>();
    private DatabaseReference advertisementPublicsRef;
    private AlertDialog dialog;
    private String filterState = "";
    private String filterCategory = "";
    private boolean filteringForState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        //initial configuration
        authentication  = ConfigurationFirebase.getFirebaseAuthentication();
       // authentication.signOut(); //deslogged
        initializeComponents();

        //Configurations initial
        authentication = ConfigurationFirebase.getFirebaseAuthentication();
        advertisementPublicsRef = ConfigurationFirebase.getFirebase()
                    .child("anuncios");

        //Configurate ReyclerView
        recyclerAdvertisementPublics.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdvertisementPublics.setHasFixedSize(true);
        //Configure Adapter
        adapterAdvertisement = new AdapterAdvertisement(listAdvertisements, this);
        recyclerAdvertisementPublics.setAdapter(adapterAdvertisement);

        //recovery advertisement for user public
        recoveryPublicAdvertisement();

        //apply event click
        recyclerAdvertisementPublics.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAdvertisementPublics,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Advertisement advertisementSelect = listAdvertisements.get(position);
                                Intent i = new Intent(AdvertisementActivity.this, ProductDetailActivity.class);
                                i.putExtra( "anuncioSelecionado", advertisementSelect);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));

    }


    public void filterForState(View view){

        AlertDialog.Builder dialogState = new AlertDialog.Builder(this);
        dialogState.setTitle("Selecione o estado desejado");

        //configurate spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //configurate spinner of state
        final Spinner spinnerState = viewSpinner.findViewById(R.id.spinnerFilter);
        String[] estate = getResources().getStringArray(R.array.estate);
        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estate
        );
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapterState);

        dialogState.setView(viewSpinner);

        dialogState.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterState = spinnerState.getSelectedItem().toString();
                Log.d("filtro", "filtro: " + filterState);
                recoveryAdvertisementForState();
                filteringForState = true;
            }
        });
        dialogState.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogState.create();
        dialog.show();
    }

    public void filterForCategory(View view){

        if(filteringForState == true){

            AlertDialog.Builder dialogCategory = new AlertDialog.Builder(this);
            dialogCategory.setTitle("Selecione a categoria desejada");

            //configurate spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //configurate spinner of category
            final Spinner spinnerCategory = viewSpinner.findViewById(R.id.spinnerFilter);
            String[] category = getResources().getStringArray(R.array.category);
            ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(
                    this,android.R.layout.simple_spinner_item,
                    category
            );

            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapterCategory);

            dialogCategory.setView(viewSpinner);

            dialogCategory.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filterCategory = spinnerCategory.getSelectedItem().toString();
                    Log.d("filtro", "filtro: " + filterState);
                    recoveryAdvertisementForCategory();
                }
            });
            dialogCategory.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategory.create();
            dialog.show();
        }else{
            Toast.makeText(this, "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }
    }

    private void recoveryAdvertisementForState(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configurate no for state
        advertisementPublicsRef = ConfigurationFirebase.getFirebase()
                .child("anuncios")
                .child(filterState);

        advertisementPublicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                        listAdvertisements.clear();
                    for(DataSnapshot categorys: dataSnapshot.getChildren()){
                        for(DataSnapshot advertisements: categorys.getChildren()){
                            Advertisement advertisement = advertisements.getValue(Advertisement.class);
                            listAdvertisements.add(advertisement);
                        }
                    }

                    Collections.reverse(listAdvertisements);
                    adapterAdvertisement.notifyDataSetChanged();
                    dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void recoveryAdvertisementForCategory(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configurate no for state
        advertisementPublicsRef = ConfigurationFirebase.getFirebase()
                .child("anuncios")
                .child(filterState)
                .child(filterCategory);

        advertisementPublicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listAdvertisements.clear();
                    for(DataSnapshot advertisements: dataSnapshot.getChildren()) {
                        Advertisement advertisement = advertisements.getValue(Advertisement.class);
                        listAdvertisements.add(advertisement);
                    }

                Collections.reverse(listAdvertisements);
                adapterAdvertisement.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recoveryPublicAdvertisement() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();


        listAdvertisements.clear();
        advertisementPublicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot states: dataSnapshot.getChildren()){
                    for(DataSnapshot categorys: states.getChildren()){
                        for(DataSnapshot advertisements: categorys.getChildren()){
                            Advertisement advertisement = advertisements.getValue(Advertisement.class);
                            listAdvertisements.add(advertisement);
                        }
                    }
                }
                Collections.reverse(listAdvertisements); //order list advertisement
                adapterAdvertisement.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //user logout

        if(authentication.getCurrentUser() == null){
            menu.setGroupVisible(R.id.group_logout,  true);
        }else{ //logged
            menu.setGroupVisible(R.id.group_logged,  true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_register:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.menu_sair:
                authentication.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_advertisement:
                startActivity(new Intent(getApplicationContext(), MyAdvertisementActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeComponents() {
        recyclerAdvertisementPublics = findViewById(R.id.recyclerAdvertisementPublics);
    }
}
