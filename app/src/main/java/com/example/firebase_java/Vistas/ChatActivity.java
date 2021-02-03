package com.example.firebase_java.Vistas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_java.Adaptadores.AdaptadorChat;
import com.example.firebase_java.Modelos.Chat;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    public static List<Chat> mRecibidos = new ArrayList<Chat>();
    public static List<Chat> mEnviados = new ArrayList<Chat>();


    private TextView tvContacto;
    private EditText etMensaje;
    private Button btEnviar;

    //Variables para la configuracion del RecyclerView del chat
    private RecyclerView rvChat;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorChat adaptadorChat;

    //Referencia a la base de datos
    private DatabaseReference ref;
    private FirebaseAuth auth;

    private User receptorUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tvContacto = findViewById(R.id.tvContacto);
        etMensaje = (EditText) findViewById(R.id.etMensaje);
        rvChat = findViewById(R.id.rvChat);
        btEnviar = findViewById(R.id.btEnviar);

        //Configuramos el recycler view
        adaptadorChat = new AdaptadorChat(this);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(adaptadorChat);

        //Instanciamos una referencia de utentificacion
        auth = FirebaseAuth.getInstance();
        //Instanciamos nuestra referencia al nodo padre de nuestro Arbol de la BD
        ref = FirebaseDatabase.getInstance().getReference();


        //Recogemos la informacion del usuario receptor
        Bundle bundle = getIntent().getExtras();
        receptorUser = (User) bundle.getSerializable("user");


        //Cargamos los mensajes existentes por fecha
        cargarEnviados();
        cargarRecibidos();

        //Cargamos el nombre del usuario receptor en el titulo
        tvContacto.setText(receptorUser.getNombre());


        //Con este método haremos que el recycler view se mueva de forma dinamica hasta la ultima posicion ensenñandonos el ultimo mensaje recibido o enviado
        adaptadorChat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                rvChat.scrollToPosition(adaptadorChat.getItemCount() - 1);
            }
        });


        btEnviar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                //Si no hay texto no envia el mensaje
                if (etMensaje.getText().toString().isEmpty()) {

                } else {
                    enviarMensaje();
                }


            }
        });


    }

    /*
        Para cargar los mensajes enviados nos posicionamos en el nodo de mensajes enviados por el usuario -> y en el nodo array de mensajes enviados a esa UID

     */
    private void cargarEnviados() {
        ref.child("USERS").child(auth.getUid()).child("mensajesEnviados").child(receptorUser.getUid()).addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Chat chat = snapshot.getValue(Chat.class);
                adaptadorChat.enviar(chat);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /*
        Para cargar los mensajes recibidos nos posicionamos dentro de nuestro usuario -> mensajes recibidos -> y en el array de mensajes recibidos por parte del receptor
     */
    private void cargarRecibidos() {
        ref.child("USERS").child(auth.getUid()).child("mensajesRecibidos").child(receptorUser.getUid()).addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Chat chat = snapshot.getValue(Chat.class);
                adaptadorChat.enviar(chat);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void enviarMensaje() {
        /*Para enviar los mensajes con la informacion del usuario, cuando el mensaje es enviado, se crea o se rellena un array de mensajes enviados (por parte del usuario)
        y otro array de mensajes recibidos  en el usuario receptor con la misma informacion.*/
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Obtenemos los datos que queremos enseñar del mensaje enviado
                String nombre = snapshot.child("USERS").child(auth.getUid()).child("nombre").getValue(String.class);
                String foto = snapshot.child("USERS").child(auth.getUid()).child("photoUrl").getValue(String.class);


                //obtenemos
                Chat chat = new Chat(etMensaje.getText().toString(), auth.getUid(), nombre, Calendar.getInstance().getTime(), foto);
                Chat chat_r = new Chat(etMensaje.getText().toString(), receptorUser.getUid(), nombre, Calendar.getInstance().getTime(), foto);


                mRecibidos.add(chat_r);
                mEnviados.add(chat);

                ref.child("USERS").child(receptorUser.getUid()).child("mensajesRecibidos").child(auth.getUid()).push().setValue(chat_r);
                ref.child("USERS").child(auth.getUid()).child("mensajesEnviados").child(receptorUser.getUid()).push().setValue(chat);


                etMensaje.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }


}





