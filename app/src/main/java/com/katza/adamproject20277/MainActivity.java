package com.katza.adamproject20277;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // הגדרת רכיבי ה-UI בהתאם ל-IDs ב-XML
    private EditText fullname, tz, grade;
    private Button btnInsert;


    private FirestoreDAO fsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול ה-DAO
        fsDAO = new FirestoreDAO();

        // קישור רכיבים מה-XML
        fullname = findViewById(R.id.fullname);
        tz = findViewById(R.id.tz);
        grade = findViewById(R.id.grade);

        btnInsert = findViewById(R.id.btnInsert);



        // מימוש כפתור ההוספה בדיוק לפי המבנה שביקשת
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsDAO.createDocument(
                        fullname.getText().toString(),
                        tz.getText().toString(),
                        Integer.parseInt(grade.getText().toString())
                );
            }
        });
    }
}