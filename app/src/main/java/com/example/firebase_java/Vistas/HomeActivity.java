package com.example.firebase_java.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase_java.Adaptadores.AdaptadorContactos;
import com.example.firebase_java.Modelos.Chat;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

//Enum para obetener el nombre del proveedor de servicios de usuario
enum ProviderType {
    BASIC,
    GOOGLE

}

public class HomeActivity extends AppCompatActivity {

    //Lista para cargar los contactos en nuestro RecyclerView, es pública y estática acesible desde cualquier parte del programa
    public static ArrayList<User> listaContactos = new ArrayList<User>();
    public List<Chat> recibidos = new ArrayList<Chat>();
    public List<Chat> enviados = new ArrayList<Chat>();

    //Variable de autentifcacion
    private FirebaseAuth auth;

    //Variables de referencia a nuestro archivo JSON
    private DatabaseReference dbRef, refListaUsuarios, refEntrada;
    //Variable para controlar los cambios sufridos en nuestro JSON
    private ValueEventListener eventListener;

    //Nos declaramos nuestro objeto usuario
    User user;


    // Varibales para inflar el recycler view
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Foto por defecto de los usuarios
        String uri = "https://firebasestorage.googleapis.com/v0/b/fir-java-2021.appspot.com/o/guest.png?alt=media&token=92ec12ac-5040-4618-a5a8-c7e2e6840f3f";

        //Toolbar de ajustes y opciones
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        toolbar.setSubtitle(R.string.subtitle_toolbar);

        //Asociamos nuestras variables a las views de nuestro layout
        recycler = findViewById(R.id.reycler);
        //Instanciamos el objeto de autentificacion de Firebase
        auth = FirebaseAuth.getInstance();

        //Info del usuario obtenida al inicio de sesion
        Bundle bundle = getIntent().getExtras();

        String email = bundle.getString("email");
        String prov = bundle.getString("provider");
        String uid = bundle.getString("uid");

        //Guardar datos en preferencias
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("provider", prov);
        editor.putString("uid", uid);
        editor.apply();


        //Pausamos la ejecucion durante menos de un segundo para cargar los datos del usuario y la lista de contactos
        try {
            Thread.sleep(500);
            //Añadir pantalla de carga HANDLER

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Para crear el nodo padre si no existe
        refEntrada = FirebaseDatabase.getInstance().getReference();

        //Para no machacar la base de datos, en el caso de que el usuario tenga datos guardados
        refEntrada.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = auth.getUid();
                if (!snapshot.hasChild(uid)) {

                    dbRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(auth.getUid());

                    user = new User(auth.getUid(), email, "", "", prov, uri, enviados, recibidos);

                    dbRef.setValue(user);
                    dialogoEntrada();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);


        //Para obtener la lista completa de usuarios, nos posicionamos en el nodo creado en el paso anterior
        refListaUsuarios = FirebaseDatabase.getInstance().getReference().child("USERS");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Limpiamos la lista y la recorremos obteniendo los users
                listaContactos.clear();
                for (DataSnapshot xUser : snapshot.getChildren()) {

                    String uid = xUser.child("uid").getValue(String.class);
                    String email = xUser.child("email").getValue(String.class);
                    String nombre = xUser.child("nombre").getValue(String.class);
                    String phone = xUser.child("telefono").getValue(String.class);
                    String foto = xUser.child("photoUrl").getValue(String.class);

                    User u = new User(uid, email, nombre, phone, prov, foto, enviados, recibidos);

                    listaContactos.add(u);
                }

                //-------------------------Rellenamos el recycler view--------------------------------------

                adapter = new AdaptadorContactos(listaContactos, HomeActivity.this);
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        refListaUsuarios.addValueEventListener(eventListener);


    }


    //------------------------------------INFLAR EL TOOLBAR---------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            //Para salir de sesion y limpiar los datos de las preferencias
            case R.id.itemLogout:


                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                auth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                break;

            //Para navegar hasta la pantalla donde editaremos el nuestro usuario
            case R.id.itemEditar:

                Intent i = new Intent(HomeActivity.this, InfoUsuarioActivity.class);
                startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    //Dialog que nos dara la opcion de navegar hasta las opciones de edicion de datos del usuario.
    private void dialogoEntrada() {
        AlertDialog ad = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Completa la información de tu perfil")
                .setMessage("¿Quieres añadir más datos a tu perfil?. " + "\n" +
                        "Podrás hacerlo luego en la opciones de la barra superior")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(HomeActivity.this, InfoUsuarioActivity.class);
                        startActivity(i);

                    }
                })
                .setNegativeButton("No", null)
                .show();

    }


}

