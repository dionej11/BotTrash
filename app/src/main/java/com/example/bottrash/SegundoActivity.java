package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    /****************************Declaración de los objetos bluetooth******************************/
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean activar=true;
    Handler bluetoothIn;
    final int handlerState = 0;
    private SegundoActivity.ConnectedThread MyConexionBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);

        /*******Inicialización del adaptador BT********/
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        txt_visi = (TextView)findViewById(R.id.textView);
        btn_visi = (ImageButton)findViewById(R.id.imageButton);
        circulo = (ImageView)findViewById(R.id.imageView4);

        txt_in = (TextView)findViewById(R.id.textView5);
        btn_in = (ImageButton)findViewById(R.id.imageButton2);
        lista = (ListView)findViewById(R.id.lista) ;

        /*******Apareamineto del BT con el modulo ESP32********/
        Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();
        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("ESP32test")){
                address = pairedDevice.getAddress();
                Toast.makeText(getBaseContext(), "Apareados", Toast.LENGTH_LONG).show();
            }
        }

        /*******Inicialización del obj request de la libreria Volley********/
        queue = Volley.newRequestQueue(this);

        obtenerDatosVolley();
    }

    //metodo para el btn de volver
    public void siguiente(View view){
        try{
            btSocket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        Intent siguiente = new Intent(this, TercerActivity.class);
        startActivity(siguiente);
    }

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
                                ruta += key +":";
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
                        if (array.size() > 0) {
                            txt_visi.setVisibility(View.INVISIBLE);
                            btn_visi.setVisibility(View.INVISIBLE);
                            circulo.setVisibility(View.INVISIBLE);

                            txt_in.setVisibility(View.VISIBLE);
                            btn_in.setVisibility(View.VISIBLE);
                            lista.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    SegundoActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    array
                            );
                            lista.setAdapter(arrayAdapter);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                System.out.println("sin datos");
            }
        }
        );
        if (btn_visi.getVisibility() == View.INVISIBLE) {
            txt_visi.setVisibility(View.VISIBLE);
            btn_visi.setVisibility(View.VISIBLE);
            circulo.setVisibility(View.VISIBLE);
        }
        queue.add(request);//Añadirle a la cola la petición
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                System.out.println("se pulsó: "+position);
                String contenido = lista.getItemAtPosition(position).toString();
                String recorrido =contenido.split(":")[1];
                MyConexionBT.write(recorrido);
                System.out.println(recorrido);
            }
        });
    }
    /****************************Se crea el canal o socket del BT**********************************/
    private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    /****************************Función para verificar el estado del BT***************************/
    private void VerificarEstadoBT() {
        if(btAdapter.isEnabled()){

        }else{
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        }
    }
    /*********************************Ciclo de vida onResume***************************************/
    public void onResume() {
        super.onResume();
        if (activar) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
            }
            // Establece la conexión con el socket Bluetooth.
            try {
                btSocket.connect();
                Toast.makeText(getBaseContext(), "Conect Socket", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                }
            }
            MyConexionBT = new SegundoActivity.ConnectedThread(btSocket);//Se hace el proceso del conexiony envio de datos en el hilo
            MyConexionBT.start();
        }
        MyConexionBT.write("s");
    }
    /****************************Función del uso del hilo********************************/
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                System.out.println(e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        /***********************Metodo run donde se reciben los datos******************************/
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    System.out.println("la coordenada es "+readMessage);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        /***********************Metodo write donde se envian los datos*****************************/
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}