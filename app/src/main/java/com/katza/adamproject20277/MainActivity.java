
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText bookName, author, dateAdded;
    private TextView tvcontent;
    private Button btnInsert, btnSelect;
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

        fsDAO = new FirestoreDAO();

        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        dateAdded = findViewById(R.id.dateAdded);

        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);
        tvcontent = findViewById(R.id.tvcontent);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsDAO.createDocument(
                        bookName.getText().toString(),
                        author.getText().toString(),
                        dateAdded.getText().toString()
                );
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvcontent.setText("");

                fsDAO.readAllDocuments(new IFirestoreCallback() {
                    @Override
                    public void OnDataReceiver(ArrayList<Book> books) {
                        for (int i = 0; i < books.size(); i++) {
                            tvcontent.setText(tvcontent.getText().toString()
                                    + books.get(i).toString() + "\n");
                        }
                    }
                });
            }
        });
    }
}
