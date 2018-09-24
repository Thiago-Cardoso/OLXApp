package olx.tcardoso.olxapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.helper.ConfigurationFirebase;

public class AdvertisementActivity extends AppCompatActivity {

    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        //initial configuration
        authentication  = ConfigurationFirebase.getFirebaseAuthentication();
       // authentication.signOut(); //deslogged
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
}
