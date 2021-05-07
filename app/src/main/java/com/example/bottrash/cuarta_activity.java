package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class cuarta_activity extends AppCompatActivity {

    private TextView tv_cambia;
    private ImageButton btnAde, btnDere, btnIzq, btnAtr, btnParar;
    private Button btnConexion, btnInicio, btnFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuarta_activity);

        //recibirDatos();

        tv_cambia = (TextView)findViewById(R.id.tv_cambiante);
        btnAde = (ImageButton)findViewById(R.id.btnAdelante);
        btnDere = (ImageButton)findViewById(R.id.btnDerecha);
        btnIzq = (ImageButton)findViewById(R.id.btnIzquierda);
        btnAtr = (ImageButton)findViewById(R.id.btnAtras);
        btnParar = (ImageButton)findViewById(R.id.btnDetener);
        btnConexion = findViewById(R.id.btnConectarBlue);
        btnInicio = findViewById(R.id.btnIniciar);
        btnFin = findViewById(R.id.btnFinalizar);

        String Dato = getIntent().getStringExtra("Envio");
        tv_cambia.setText(Dato);
        } //Fin del onCreate

       public void Siguiente(View view){
        Intent Siguiente = new Intent(this, quinta_activity.class);
        startActivity(Siguiente);
       }
    }
