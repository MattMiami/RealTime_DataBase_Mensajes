package com.example.firebase_java.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.Vistas.ChatActivity;
import com.example.firebase_java.R;
import com.example.firebase_java.Vistas.InfoContactoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.AdaptadorViewHolder> {

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private Context context;
    private ArrayList<User> listaContactos = new ArrayList<>();



    public AdaptadorContactos(ArrayList<User> listaUsuarios, Context context){
        this.context = context;
        this.listaContactos = listaUsuarios;

    }

    @NonNull
    @Override
    public AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contactos, parent, false);

        AdaptadorViewHolder adh = new AdaptadorViewHolder(itemView);


        return adh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        User users = listaContactos.get(position);

        holder.emailUser.setText(users.getNombre());

        Glide.with(context).load(users.getPhotoUrl()).into(holder.ivUsuario);


        //---------------ActionListeners de la imagen y el texto dentro de cada item ------------------------------------------

        //Si hacemos click en el texto del item iremos a el chat de ese contacto
        holder.emailUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.itemView.getContext(), ChatActivity.class);
                intent.putExtra("user", users);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        //Si clicamos en la foto nos aparecera la informacion del Contacto
        holder.ivUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.itemView.getContext(), InfoContactoActivity.class);
                intent.putExtra("contacto", users);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder{


        private TextView emailUser;
        private ImageView ivUsuario;


        public AdaptadorViewHolder(View itemView){
            super(itemView);

            emailUser = (TextView) itemView.findViewById(R.id.tvEmail_item);
            ivUsuario = (ImageView) itemView.findViewById(R.id.ivUsuario);

        }
    }
}
