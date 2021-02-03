package com.example.firebase_java.Modelos;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String uid, email, nombre, telefono, provider;
    private String photoUrl;
    private List<Chat> mensajesRecibidos = new ArrayList<>();
    private List<Chat> mensajesEnviados = new ArrayList<Chat>();

    public User(String uid, String email, String nombre, String telefono, String provider,String photoUrl, List<Chat> mensajesRecibidos, List<Chat> mensajesEnviados) {
        this.uid = uid;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.provider = provider;
        this.photoUrl = photoUrl;
        this.mensajesRecibidos = mensajesRecibidos;
        this.mensajesEnviados = mensajesEnviados;
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public User(String uid, String email, List<Chat> mensajesRecibidos, List<Chat> mensajesEnviados) {
        this.uid = uid;
        this.email = email;
        this.mensajesRecibidos = mensajesRecibidos;
        this.mensajesEnviados = mensajesEnviados;
    }

    public User(String uid, String email, String nombre, String telefono, String provider, String photoUrl) {
        this.uid = uid;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.provider = provider;
        this.photoUrl = photoUrl;

    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Chat> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(List<Chat> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public List<Chat> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(List<Chat> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }
}

