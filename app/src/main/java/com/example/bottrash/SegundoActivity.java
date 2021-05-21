package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SegundoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);

        //hacer metodo para verificar si hay datos en la base de datos
        //si hay, oculatar el txt y mover el boton de add para pintar la info
        //si no hay dejarlos donde estan
    }

    //metodo para el btn de volver

    public void siguiente(View view){
        Intent siguiente = new Intent(this, TercerActivity.class);
        startActivity(siguiente);
    }
}