package com.example.firebase_java.Adaptadores;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase_java.Modelos.Chat;
import com.example.firebase_java.Modelos.User;
import com.example.firebase_java.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdaptadorChat extends RecyclerView.Adapter<AdaptadorChat.HolderChat> {

    private List<Chat> listaChat = new ArrayList<Chat>();
    private Context c;

    public AdaptadorChat(Context c) {
        this.c = c;
    }



    /*
        Con este metodo llenamos la lista del recycler view y ordenamos los mensajes por fecha esacta de llegada,
         con el metodo ordenarChat de la Clase Modelo Chat.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void enviar(Chat c){

        listaChat.add(c);
        listaChat.sort(Chat.ordenChat);
        notifyItemInserted(listaChat.size());
    }




    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_chat, parent, false);


        return new HolderChat(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {

        //Setteamos los componentes que se mostraran en el mensaje, con la informacion que se envía
        Chat chat = listaChat.get(position);

        holder.tvNombreCont.setText(chat.getNombre());

        holder.tvMensajeEnviado.setText(chat.getMensaje());

        //Asi extraigo la hora de la fecha completa
        holder.tvHora.setText(chat.getFecha().toString().substring(11,16));

        Glide.with(c).load(chat.getFoto()).into(holder.ivMensaje);


    }

    @Override
    public int getItemCount() {
        return listaChat.size();
    }

    public class HolderChat extends RecyclerView.ViewHolder{

        private TextView tvMensajeEnviado, tvNombreCont, tvHora;
        private ImageView ivMensaje;


        public HolderChat(View itemView){
            super(itemView);

            tvMensajeEnviado = (TextView) itemView.findViewById(R.id.tvMensajeEnviado);
            tvNombreCont = (TextView) itemView.findViewById(R.id.tvNombreCont);
            ivMensaje = (ImageView) itemView.findViewById(R.id.ivMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);

        }


    }
}
