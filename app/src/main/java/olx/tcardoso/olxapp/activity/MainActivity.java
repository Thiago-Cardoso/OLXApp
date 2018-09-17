package olx.tcardoso.olxapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.helper.ConfigurationFirebase;

public class MainActivity extends AppCompatActivity {

    private Button buttonAccess;
    private EditText fieldEmail, fieldPassword;
    private Switch typeAccess;

    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        authentication = ConfigurationFirebase.getFirebaseAuthentication();

        buttonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = fieldEmail.getText().toString();
                String password = fieldPassword.getText().toString();

                //validation
                if(!email.isEmpty()){

                    if(!password.isEmpty()){

                        //verificate state of switch -
                        if(typeAccess.isChecked()) //Register
                        {
                            authentication.createUserWithEmailAndPassword(
                                    email, password
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(MainActivity.this, "Cadastro realizado com sucesso",
                                                Toast.LENGTH_SHORT).show();

                                        //redirect user for principe screen app

                                    }else{

                                         String errorException = "";

                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            errorException = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            errorException = "Por favor, digite um e-mail válido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            errorException = "Este conta já foi cadastrada";
                                        } catch (Exception e) {
                                            errorException = "ao cadastrar usuário: "  + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(MainActivity.this,
                                                "Erro: " + errorException ,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{ //Login

                            authentication.signInWithEmailAndPassword(
                                    email,password
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(MainActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(getApplicationContext(), AdvertisementActivity.class));

                                    }else{

                                        Toast.makeText(MainActivity.this,
                                                "Erro ao fazer login" + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }else{

                        Toast.makeText(MainActivity.this, "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Toast.makeText(MainActivity.this, "Preencha o E-mail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeComponents(){

        fieldEmail = findViewById(R.id.editRegisterEmail);
        fieldPassword = findViewById(R.id.editRegisterPassword);
        buttonAccess = findViewById(R.id.buttonAccess);
        typeAccess = findViewById(R.id.switchAccess);
    }
}
