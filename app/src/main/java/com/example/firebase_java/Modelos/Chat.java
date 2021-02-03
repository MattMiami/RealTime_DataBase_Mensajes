package com.example.firebase_java.Modelos;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Chat implements Serializable {

    private String mensaje;
    private String uid;
    private String nombre;
    private Date fecha;
    private String foto;
    //Añadir mas info, la foto del usuario etc

    public Chat() {
    }

    public Chat(String mensaje, String uid, String nombre, Date fecha, String foto) {
        this.mensaje = mensaje;
        this.uid = uid;
        this.nombre = nombre;
        this.fecha = fecha;
        this.foto = foto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    /*
        Para ordeenar los mensajes por orden de llegada
         */
    public static Comparator<Chat> ordenChat = new Comparator<Chat>() {
        @Override
        public int compare(Chat o1, Chat o2) {

            return new Integer(o1.getFecha().compareTo(o2.getFecha()));
        }
    };


}
