package olx.tcardoso.olxapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.widget.MaskEditText;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.helper.ConfigurationFirebase;
import olx.tcardoso.olxapp.helper.Permissoes;
import olx.tcardoso.olxapp.model.Advertisement;

public class RegisterAdvertisementActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText fieldTitle, fieldDescription;
    private ImageView image1, image2, image3;
    private Spinner fieldState, fieldCategory;
    private CurrencyEditText fieldValue;
    private MaskEditText fieldPhone;
    private Advertisement advertisement;
    private StorageReference storage;

    private String[] permission = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE
    };

   private List<String> listPhotoRecovery = new ArrayList<>();
   private List<String> listUrlPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_advertisement);

        //configurate initial
        storage = ConfigurationFirebase.getFirebaseStorage();

        //validate permission
        Permissoes.validarPermissoes(permission, this, 1);

        initializeComponents();
        loadingDataSpinner();
    }


    public void saveAdvertisement(){
        //save image in storage
       for(int i=0; i < listPhotoRecovery.size(); i++){
            String urlImage = listPhotoRecovery.get(i);
            int sizeList = listPhotoRecovery.size();
            savePhotoStorage(urlImage, sizeList, i);
        }
    }

    private void savePhotoStorage(String urlString, final int totalPhotos, int cont){
        //Create nó in firebase
        StorageReference imageAdvertisement = storage.child("imagens")
                .child("anuncios")
                .child(advertisement.getIdAdvertisement())
                .child("imagem"+cont);

        // make upload of file
        UploadTask uploadTask = imageAdvertisement.putFile( Uri.parse(urlString) );

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvert = firebaseUrl.toString();

                listUrlPhotos.add(urlConvert);

                if(totalPhotos == listUrlPhotos.size()){
                    advertisement.setPhoto(listUrlPhotos);
                    advertisement.save();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessageError("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    private Advertisement configurateAdvertisement(){
        String state = fieldState.getSelectedItem().toString();
        String category = fieldCategory.getSelectedItem().toString();
        String title = fieldTitle.getText().toString();
        String value = String.valueOf(fieldValue.getRawValue());
        String phone = fieldPhone.getText().toString();
        String description = fieldDescription.getText().toString();

        Advertisement advertisement = new Advertisement();
        advertisement.setState(state);
        advertisement.setCategory(category);
        advertisement.setTitle(title);
        advertisement.setValue(value);
        advertisement.setPhone(phone);
        advertisement.setDescription(description);

        return advertisement;
    }

    public void validarDadosAnuncio(View view){
        String phoneAux = "";
        advertisement = configurateAdvertisement();
        if(fieldPhone.getRawText() != null){
            phoneAux = fieldPhone.getRawText().toString();
        }

        if(listPhotoRecovery.size() != 0){
            if(!advertisement.getState().isEmpty()){
                if(!advertisement.getCategory().isEmpty()){
                    if(!advertisement.getTitle().isEmpty()){
                        if(!advertisement.getValue().isEmpty() && !advertisement.getValue().equals("0")){
                            if(!advertisement.getPhone().isEmpty() && phoneAux.length() >= 10){
                                if(!advertisement.getDescription().isEmpty()){

                                    saveAdvertisement();

                                }else{
                                    showMessageError("Preencha o campo descrição!");
                                }
                            }else{
                                showMessageError("Preencha o campo telefone, digite ao menos 10 números!");
                            }
                        }else{
                            showMessageError("Preencha o campo valor!");
                        }
                    }else{
                        showMessageError("Preencha o campo título!");
                    }
                }else{
                    showMessageError("Preencha o campo categoia!");
                }
            }else{
                showMessageError("Preencha o campo estado!");
            }
        }else{
            showMessageError("Selecione ao menos uma foto");
        }
    }

    private void showMessageError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageRegister1:
                choiceImage(1);
                break;
            case R.id.imageRegister2:
                choiceImage(2);
                break;
            case R.id.imageRegister3:
                choiceImage(3);
                break;
        }
    }

    public void choiceImage(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //recovery image
            Uri imageChoice = data.getData();
            String pathImage = imageChoice.toString();

            //Configure image in ImageView
            if(requestCode == 1){
                image1.setImageURI(imageChoice);
            }else if(requestCode == 2){
                image2.setImageURI(imageChoice);
            }else if(requestCode == 3){
                image3.setImageURI(imageChoice);
            }
            listPhotoRecovery.add(pathImage);
        }

    }

    private void initializeComponents(){
        fieldTitle = findViewById(R.id.editTitle);
        fieldDescription = findViewById(R.id.editDescription);
        fieldValue = findViewById(R.id.editValue);
        fieldPhone = findViewById(R.id.editPhone);
        fieldState = findViewById(R.id.spinnerState);
        fieldCategory = findViewById(R.id.spinnerCategory);

        image1 = findViewById(R.id.imageRegister1);
        image2 = findViewById(R.id.imageRegister2);
        image3 = findViewById(R.id.imageRegister3);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

        //configurate local fot pt - portuguese BR - Brazil
        Locale locale = new Locale("pt", "BR");
        fieldValue.setLocale(locale);
    }

    private void loadingDataSpinner(){
       /* String[] state = new String[]{
                "SP", "RS"
        };*/
       //configurate spinner de state
        String[] estate = getResources().getStringArray(R.array.estate);
        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estate
        );
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldState.setAdapter(adapterState);

        //configurate spinner de category
        String[] category = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(
                    this,android.R.layout.simple_spinner_item,
                category
        );
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldCategory.setAdapter(adapterCategory);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissionResult : grantResults){

            if (permissionResult == PackageManager.PERMISSION_DENIED){
                AlertValidationPermission();
            }
        }
    }

    private void AlertValidationPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é encessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
