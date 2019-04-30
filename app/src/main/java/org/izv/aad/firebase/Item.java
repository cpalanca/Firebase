package org.izv.aad.firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Item {

    public String id;
    public String nombre;
    public String mensaje;

    public Item() {

    }

    public Item(String id, String nombre, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.mensaje = mensaje;
    }

    public String getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nombre", nombre);
        result.put("mensaje", mensaje);
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}