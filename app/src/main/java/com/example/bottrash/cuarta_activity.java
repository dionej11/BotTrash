package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class cuarta_activity extends AppCompatActivity {
    //elementos
    private TextView tv_cambia;
    private ImageButton btnAde, btnDere, btnIzq, btnAtr, btnParar;
    private Button btnConexion, btnDesconexion, btnFin;

    //Bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean activar;
    Handler bluetoothIn;
    final int handlerState = 0;
    private ConnectedThread MyConexionBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuarta_activity);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        tv_cambia = (TextView)findViewById(R.id.tv_cambiante);
        btnAde = (ImageButton)findViewById(R.id.btnAdelante);
        btnDere = (ImageButton)findViewById(R.id.btnDerecha);
        btnIzq = (ImageButton)findViewById(R.id.btnIzquierda);
        btnAtr = (ImageButton)findViewById(R.id.btnAtras);
        btnParar = (ImageButton)findViewById(R.id.btnDetener);
        btnConexion = findViewById(R.id.btnConectarBlue);
        btnDesconexion = findViewById(R.id.btnDesco);
        btnFin = findViewById(R.id.btnFinalizar);

        String Dato = getIntent().getStringExtra("Envio");
        tv_cambia.setText(Dato);

        //bluetooth
        Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();

        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("ESP32test")){//!!!!!!!!!!!!!!!!
                address = pairedDevice.getAddress();
                Toast.makeText(getBaseContext(), "Apareados", Toast.LENGTH_LONG).show();
            }
        }

        //eventos de los btns
        btnConexion.setOnClickListener(new View.OnClickListener() {//CONECTAR
            @Override
            public void onClick(View v) {
                activar = true;
                onResume();
                Toast.makeText(getBaseContext(), "Conectados", Toast.LENGTH_LONG).show();
            }
        });
        btnDesconexion.setOnClickListener(new View.OnClickListener() {//DESCONECTAR
            @Override
            public void onClick(View v) {
                try{

                    btSocket.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnAde.setOnClickListener(new View.OnClickListener() {//Btn adelante
            @Override
            public void onClick(View v) {
                MyConexionBT.write("a");
            }
        });
        btnIzq.setOnClickListener(new View.OnClickListener() {//Btn izq
            @Override
            public void onClick(View v) {
                MyConexionBT.write("b");
            }
        });
        btnDere.setOnClickListener(new View.OnClickListener() {//Btn derecha
            @Override
            public void onClick(View v) {
                MyConexionBT.write("c");
            }
        });
        btnAtr.setOnClickListener(new View.OnClickListener() {//Btn atras
            @Override
            public void onClick(View v) {
                MyConexionBT.write("d");
            }
        });
        btnParar.setOnClickListener(new View.OnClickListener() {//Btn stop
            @Override
            public void onClick(View v) {
                MyConexionBT.write("e");
            }
        });
        btnFin.setOnClickListener(new View.OnClickListener() {//Btn fin recorrido
            @Override
            public void onClick(View v) {
                MyConexionBT.write("f");
            }
        });


    } //Fin del onCreate

    public void Siguiente(View view){
        Intent Siguiente = new Intent(this, quinta_activity.class);
        startActivity(Siguiente);
    }

    private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException{
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private void VerificarEstadoBT() {
        if(btAdapter.isEnabled()){

        }else{

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        }
    }

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
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();
        }
    }

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

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {

                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
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
