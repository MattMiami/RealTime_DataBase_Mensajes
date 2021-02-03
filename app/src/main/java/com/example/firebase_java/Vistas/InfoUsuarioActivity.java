package com.example.firebase_java.Vistas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InfoUsuarioActivity extends AppCompatActivity {

    //Variable que hace referencia al Storage de Firebase
    private StorageReference storage;

    //Variables constantes para las fotos del usuario
    private static final int GALLERY = 100;

    private DatabaseReference ref;
    private FirebaseAuth auth;

    private TextView currentName, currentPhone;
    private EditText userName, userPhone;
    private Button btGuardar, btGaleria;
    private ImageView ivUser;
    //Para mostrar una carga mientras recogemos la imagen del storage
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        currentName = findViewById(R.id.currentName);
        currentPhone = findViewById(R.id.currentPhone);
        btGuardar = findViewById(R.id.btGuardar);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);

        btGaleria = findViewById(R.id.btGaleria);
        ivUser = findViewById(R.id.ivUser);


        //Instancias de autentificacion y referencia de nuestro arbol de la base de datos
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("USERS").child(auth.getUid());


        //Creamos una instancia que hace referencia al almacenamiento de Firebase, nos servira para guradar las fotos y acceder a ellas mediante una uri
        storage = FirebaseStorage.getInstance().getReference();
        userPhoto();

        //Obtenemos la informacion del usuario de la BD y le mostramos la que tiene guardada actualmente
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nombre = snapshot.child("nombre").getValue(String.class);
                String telefono = snapshot.child("telefono").getValue(String.class);
                String foto = snapshot.child("photoUrl").getValue(String.class);

                Glide.with(getApplicationContext()).load(foto).into(ivUser);
                currentName.setText("Nombre guardado: " + nombre);
                currentPhone.setText("Nº Tlf guardado: " + telefono);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Para actualizar y cambiar los datos del usuario
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userName.getText().toString().isEmpty() || !userPhone.getText().toString().isEmpty()) {

                    ref.child("nombre").setValue(userName.getText().toString());
                    ref.child("telefono").setValue(userPhone.getText().toString());
                    userName.setText("");
                    userPhone.setText("");

                    Toast.makeText(getApplicationContext(), "Datos actualizados!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Para guardar debes rellenar los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    //------------------------------------------------------CAMARA-----------------------------------------------------------
    //Para elegir la imagen desde galeria o desde la camara
    private void userPhoto() {

        btGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);

            }
        });

    }

    //El resultado de la request del intent dial
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode == RESULT_OK) {

            //Dialogo de  carga mientras se procesa la foto
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Guardando datos");
            progressDialog.setMessage("Estamos guardando la imagen, espera porfavor");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //Asignamos la uri a la foto del usuario
            Uri uriaux = data.getData();


            //Creamos una carpeta llamada "fotos" dentro del Storage de Firebase
            StorageReference refFilepath = storage.child("fotos").child(uriaux.getLastPathSegment());

            /*
                A continuacion, procederemos a subir la foto al Storage de firebase y a cargar la imagen en un ImageView
             */

            refFilepath.putFile(uriaux).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return refFilepath.getDownloadUrl();
                }

                //En el caso de que se comlpete la tarea guardaremos la url de la foto en el campo photoUrl de la base de datos
                //Luego con Glide podremos la foto en la imagen view.
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        ref.child("photoUrl").setValue(task.getResult().toString());

                        Glide.with(InfoUsuarioActivity.this)
                                .load(task.getResult().toString())
                                .centerCrop()
                                .fitCenter()
                                .into(ivUser);

                        Toast.makeText(getApplicationContext(), "Foto actualizada!", Toast.LENGTH_SHORT).show();
                    }

                }

            });

        }


    }
}