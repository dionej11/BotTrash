package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SegundoActivity extends AppCompatActivity {
    private TextView txt_visi;
    private ImageButton btn_visi;
    private ImageView circulo;
    private TextView txt_in;
    private ImageButton btn_in;
    private ListView lista;
    /**************************Declaración del objeto request para la BD***************************/
    private RequestQueue queue; //es una cola donde se ponen todos los request que se hagan

    private ArrayList<String> array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);

        boolean valor = getIntent().getBooleanExtra("valor",false);

        txt_visi = (TextView)findViewById(R.id.textView);
        btn_visi = (ImageButton)findViewById(R.id.imageButton);
        circulo = (ImageView)findViewById(R.id.imageView4);

        txt_in = (TextView)findViewById(R.id.textView5);
        btn_in = (ImageButton)findViewById(R.id.imageButton2);
        lista = (ListView)findViewById(R.id.lista) ;

        /*******Inicialización del obj request de la libreria Volley********/
        queue = Volley.newRequestQueue(this);


        try {
            verificar(valor);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        //si hay, oculatar el txt y mover el boton de add para pintar la info
        //si no hay dejarlos donde estan
    }

    private void verificar(boolean valor) throws AuthFailureError {
        if (valor) {
            txt_visi.setVisibility(View.GONE);
            btn_visi.setVisibility(View.GONE);
            circulo.setVisibility(View.GONE);
            txt_in.setVisibility(View.VISIBLE);
            btn_in.setVisibility(View.VISIBLE);

            obtenerDatosVolley();
            /*obtenerDatosVolley(new DatosResponseListener() {
                @Override
                public void datosResponse(ArrayList<String> datos) {
                    System.out.println("El tamaño del array es "+datos.size());
                    array.addAll(datos);
                    for (int i=0;i<array.size();i++){
                        System.out.println("dato: "+array.get(i));
                    }
                }
            });*/
            System.out.println("---------------");
            System.out.println("El tamaño del array es ..."+array.size());
            for (int i=0;i<array.size();i++){
                System.out.println(array.get(i));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    array
            );
            lista.setAdapter(arrayAdapter);

        }else{
            System.out.println("sin datos");
        }

    }

    //metodo para el btn de volver

    public void siguiente(View view){
        Intent siguiente = new Intent(this, TercerActivity.class);
        startActivity(siguiente);
    }
    /*public interface DatosResponseListener {
        void datosResponse(ArrayList<String> datos);
    }*/
    /****************************Petición GET para la base de datos********************************/
    private void obtenerDatosVolley() {
        String url ="https://prueba-2912f-default-rtdb.firebaseio.com/Coordenadas.json";//API
        /*************Objeto Json para hacer el request y obtener los datos************************/
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {//la respuesta del get es un objeto Json
                        System.out.println(response);
                        Iterator<String> iter = response.keys();//Iterador de strings para conocer las keys

                        while (iter.hasNext()) {
                            String ruta = "";
                            String key = iter.next();
                            try {
                                System.out.println(key);
                                ruta += key+": ";
                                JSONObject obj = response.getJSONObject(key);
                                String valor = obj.getString("valor");
                                System.out.println(valor);
                                ruta += valor;

                                System.out.println("La ruta es: "+ruta);
                                array.add(ruta);

                            } catch (JSONException e) {
                                // Something went wrong!
                                System.out.println(e);
                            }
                        }
                        txt_in.setText("tamaño araray: "+array.size());
                        lista.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                SegundoActivity.this,
                                android.R.layout.simple_list_item_1,
                                array
                        );
                        lista.setAdapter(arrayAdapter);
                        //listener.datosResponse(array);
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