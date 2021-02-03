package com.example.firebase_java.Vistas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;

public class InfoContactoActivity extends AppCompatActivity {

    private ImageView ivContacto;
    private TextView name, email, phone;
    private ImageButton btLlamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contacto);

        name = findViewById(R.id.contactoName);
        email = findViewById(R.id.contactoEmail);
        phone = findViewById(R.id.contactoPhone);
        ivContacto = findViewById(R.id.ivContacto);
        btLlamar =  findViewById(R.id.btLlamar);

        Bundle bundle = getIntent().getExtras();
        User user = (User) bundle.getSerializable("contacto");

        //Rellenamos la informacion del contacto
        name.setText(user.getNombre());
        email.setText(user.getEmail());
        phone.setText(user.getTelefono());

        ivContacto.setImageURI(Uri.parse(user.getPhotoUrl()));

        Glide.with(this).load(user.getPhotoUrl()).into(ivContacto);




        //Intent Dial para llamar al contacto si hay numero de telefono
        btLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!phone.getText().toString().isEmpty()) {
                    dialPhoneNumber(phone.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Este contacto no tiene numero al que llamar", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
        public void dialPhoneNumber (String phoneNumber){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }