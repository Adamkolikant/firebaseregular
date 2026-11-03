package com.katza.adamproject20277;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Firstpage extends AppCompatActivity {

    private Button btnAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        btnAuth = findViewById(R.id.btnAuth);

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Firstpage.this, AuthActivity.class);
                startActivity(intent);
            }
        });
    }
}