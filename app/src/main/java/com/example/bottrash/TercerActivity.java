package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TercerActivity extends AppCompatActivity {

    private EditText IngresoTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercer);

        IngresoTexto = (EditText)findViewById(R.id.lugar);
    }

    public void Siguiente(View view){
        Intent Siguiente = new Intent(this, cuarta_activity.class);
       // String NombreLugar = IngresoTexto.getText().toString();
        Siguiente.putExtra("Envio", IngresoTexto.getText().toString());
        startActivity(Siguiente);
    }
}