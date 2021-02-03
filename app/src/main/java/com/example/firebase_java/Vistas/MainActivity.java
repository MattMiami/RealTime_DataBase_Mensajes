package com.example.firebase_java.Vistas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Nos creamos una variable para manejar las autenticaciones
    private FirebaseAuth auth;

    private DatabaseReference refListaUsuarios;
    private ValueEventListener eventListener;
    public static List<User> listaContactos = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //obtenermos la instancia de esta
        auth = FirebaseAuth.getInstance();

        //Aqui estoy lanzando la pantalla de presentacion o splash screen, que permitira a su vez cargar los datos en HomeActivity
        try {
            Thread.sleep(2000);
            setTheme(R.style.SplashTheme);
            goHome();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Con este método verifico si tenemos ya un usuario logeado, si no lo hay irá  a la activity de logueo, si lo hay entrará directamente en la activity Home
    private void goHome() {

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);

            //Obtenemos la informacion del ususario desde las preferencias
            SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
            String email = prefs.getString("email", null);
            String provider = prefs.getString("provider", null);

            //Le pasamos los valoes de las preferencias
            intent.putExtra("email", email);
            intent.putExtra("provider", provider);


            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


}