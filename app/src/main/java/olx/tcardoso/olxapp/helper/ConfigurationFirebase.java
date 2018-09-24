package olx.tcardoso.olxapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigurationFirebase {

    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth referenceAuthentication;
    private static StorageReference referenceStorage;

    public static String getIdUser(){
        FirebaseAuth autentication = getFirebaseAuthentication();
        return autentication.getCurrentUser().getUid();
    }


    //return a reference of database
    public static DatabaseReference getFirebase(){

        if(referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceFirebase;
    }

    //return a reference of authentication
    public static FirebaseAuth getFirebaseAuthentication(){
        if(referenceAuthentication == null){
            referenceAuthentication = FirebaseAuth.getInstance();
        }
        return referenceAuthentication;
    }

    ///return a reference of authentication
    public static StorageReference getFirebaseStorage(){
        if(referenceStorage == null){
            referenceStorage = FirebaseStorage.getInstance().getReference();
         }
        return referenceStorage;
    }

}
