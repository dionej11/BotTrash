package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class quinta_activity extends AppCompatActivity {
    private TextView txt;
    /****************************Declaración de los objetos bluetooth******************************/
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean activar=true;
    Handler bluetoothIn;
    final int handlerState = 0;
    private quinta_activity.ConnectedThread MyConexionBT;

    private String Dato = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quinta_activity);

        txt = (TextView)findViewById(R.id.coorde);

        Dato = getIntent().getStringExtra("recorrido");
        txt.setText(Dato);

        /*******Inicialización del adaptador BT********/
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        /*******Apareamineto del BT con el modulo ESP32********/
        Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();
        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("ESP32test")){
                address = pairedDevice.getAddress();
                Toast.makeText(getBaseContext(), "Apareados", Toast.LENGTH_LONG).show();
            }
        }
    }
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
            MyConexionBT = new quinta_activity.ConnectedThread(btSocket);//Se hace el proceso del conexiony envio de datos en el hilo
            MyConexionBT.start();
            MyConexionBT.write("h");
            MyConexionBT.write(Dato+".");
            System.out.println("envio coordenda");
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
    /*****************************Función para pasar a una activity********************************/
    public void Siguiente(View view){
        try{
            btSocket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        Intent Siguiente = new Intent(this, SegundoActivity.class);
        startActivity(Siguiente);
    }
}