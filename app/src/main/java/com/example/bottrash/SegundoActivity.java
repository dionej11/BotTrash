package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SegundoActivity extends AppCompatActivity {
    /**************************Declaración del objeto request para la BD***************************/
    private RequestQueue queue; //es una cola donde se ponen todos los request que se hagan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);

        /*******Inicialización del obj request de la libreria Volley********/
        queue = Volley.newRequestQueue(this);

        //hacer metodo para verificar si hay datos en la base de datos
        //si hay, oculatar el txt y mover el boton de add para pintar la info
        //si no hay dejarlos donde estan
    }

    //metodo para el btn de volver

    public void siguiente(View view){
        Intent siguiente = new Intent(this, TercerActivity.class);
        startActivity(siguiente);
    }
    /****************************Petición GET para la base de datos********************************/
    private void obtenerDatosVolley(){
        String url ="https://prueba-2912f-default-rtdb.firebaseio.com/Coordenadas.json";//API
        /*************Objeto Json para hacer el request y obtener los datos************************/
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {//la respuesta del get es un objeto Json
                        System.out.println(response);
                        Iterator<String> iter = response.keys();//Iterador de strings para conocer las keys
                        while (iter.hasNext()) {
                            String key = iter.next();//Key del objeto
                            try {
                                System.out.println(key);//key/ lugar
                                JSONObject obj = response.getJSONObject(key).getJSONObject("valor");//objeto de la key enviada
                                String valor = obj.toString();

                                System.out.println(valor);

                            } catch (JSONException e) {
                                System.out.println(e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }
        );
        queue.add(request);//Añadirle a la cola la petición
    }
}