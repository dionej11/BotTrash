package com.example.bottrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class quinta_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quinta_activity);
    }
    public void Siguiente(View view){
        Intent Siguiente = new Intent(this, Sexta_activity.class);
        startActivity(Siguiente);
    }
}