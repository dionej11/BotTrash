package com.example.bottrash;
/*******************************************Librerias**********************************************/
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;


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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
/*******************************************Clase**********************************************/
public class cuarta_activity extends AppCompatActivity {
    /****************************Declaración de los elementos xml*********************************/
    private TextView tv_cambia;
    private ImageButton btnAde, btnDere, btnIzq, btnAtr, btnParar;
    private Button btnConexion, btnDesconexion, btnFin;
    /****************************Declaración de los objetos bluetooth******************************/
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean activar=true;
    Handler bluetoothIn;
    final int handlerState = 0;
    private ConnectedThread MyConexionBT;
    /**********************************Ciclo de vida onCreate**************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuarta_activity);

        /*******Inicialización del adaptador BT********/
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();
        /*******Hilo donde se manejan los datos que entran via Bluetooth********/

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String coordenada = (String) msg.obj;
                    String lugar = (String)tv_cambia.getText();
                    tv_cambia.setText(lugar+"-"+coordenada);
                    //obtenerDatosVolley();
                    subirDatosVolley(lugar,coordenada);

                }
            }
        };
        /*******Inicializar variables relacionadas con los elm xml********/
        tv_cambia = (TextView)findViewById(R.id.tv_cambiante);
        btnAde = (ImageButton)findViewById(R.id.btnAdelante);
        btnDere = (ImageButton)findViewById(R.id.btnDerecha);
        btnIzq = (ImageButton)findViewById(R.id.btnIzquierda);
        btnAtr = (ImageButton)findViewById(R.id.btnAtras);
        btnParar = (ImageButton)findViewById(R.id.btnDetener);
        btnConexion = findViewById(R.id.btnConectarBlue);
        btnDesconexion = findViewById(R.id.btnDesco);
        btnFin = findViewById(R.id.btnFinalizar);
        /*******Pintar el subtitulo con el lugar ingresado********/
        String Dato = getIntent().getStringExtra("Envio");
        tv_cambia.setText(Dato);
        /*******Apareamineto del BT con el modulo ESP32********/
        Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();
        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("ESP32test")){
                address = pairedDevice.getAddress();
                Toast.makeText(getBaseContext(), "Apareados", Toast.LENGTH_LONG).show();
            }
        }
        /*******Eventos click de los botones del control remoto********/
        btnConexion.setOnClickListener(new View.OnClickListener() {//CONECTAR BT
            @Override
            public void onClick(View v) {
                activar = true;
                Toast.makeText(getBaseContext(), "Conectados", Toast.LENGTH_LONG).show();
                MyConexionBT.write("g");
            }
        });
        btnDesconexion.setOnClickListener(new View.OnClickListener() {//DESCONECTAR BT
            @Override
            public void onClick(View v) {
                try{
                    btSocket.close();
                    Siguiente(v);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnAde.setOnClickListener(new View.OnClickListener() {//Envia via BT el caracter 'a'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("a");
            }
        });
        btnIzq.setOnClickListener(new View.OnClickListener() {//Envia via BT el caracter 'b'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("b");
            }
        });
        btnDere.setOnClickListener(new View.OnClickListener() {////Envia via BT el caracter 'c'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("c");
            }
        });
        btnAtr.setOnClickListener(new View.OnClickListener() {////Envia via BT el caracter 'd'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("d");
            }
        });
        btnParar.setOnClickListener(new View.OnClickListener() {////Envia via BT el caracter 'e'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("e");
            }
        });
        btnFin.setOnClickListener(new View.OnClickListener() {////Envia via BT el caracter 'f'
            @Override
            public void onClick(View v) {
                MyConexionBT.write("f");
            }
        });
    }
    /*****************************Función para pasar a una activity********************************/
    public void Siguiente(View view){
        Intent Siguiente = new Intent(this, SegundoActivity.class);
        startActivity(Siguiente);
    }
    /****************************Petición PUT para la base de datos********************************/
    private void subirDatosVolley(String lugar, String coor){
        String url ="https://prueba-2912f-default-rtdb.firebaseio.com/Coordenadas/"+lugar+".json";
        RequestQueue queue = Volley.newRequestQueue(this);
        final JSONObject jsonObject = new JSONObject();//se crea le objeto Json a subir

        try {
            jsonObject.put("valor", coor);//se le añade la coordenada
        } catch (JSONException e) {
            System.out.println(e);
        }
        /****************************Se envia el objeto creado al request********************/
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        ) {
            /********************Se modifican los header de la petición****************************/
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {
                try {
                    Log.i("json", jsonObject.toString());
                    return jsonObject.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        queue.add(putRequest);
        // luego de guardar la info en la base de datos, aqui debe pasar a la activity segunda
    }
    /****************************Se crea el canal o socket del BT**********************************/
    private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException{
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
            MyConexionBT = new ConnectedThread(btSocket);//Se hace el proceso del conexiony envio de datos en el hilo
            MyConexionBT.start();
        }
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
