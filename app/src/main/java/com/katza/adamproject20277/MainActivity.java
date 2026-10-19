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
    private Button btnInsert, btnSelect, btnUpdate, btnDelete;
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
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
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
                String searchAuthor = author.getText().toString().trim();

                // אם הוזן מחבר בתיבה יבוצע סינון לפי מחבר
                if (!searchAuthor.isEmpty()) {
                    fsDAO.readQueryDocuments(searchAuthor, new IFirestoreCallback() {
                        @Override
                        public void OnDataReceiver(ArrayList<Book> books) {
                            for (int i = 0; i < books.size(); i++) {
                                tvcontent.append(books.get(i).toString() + "\n");
                            }
                        }
                    });
                }
                // אם התיבה ריקה יוצגו כל הספרים
                else {
                    fsDAO.readAllDocuments(new IFirestoreCallback() {
                        @Override
                        public void OnDataReceiver(ArrayList<Book> books) {
                            for (int i = 0; i < books.size(); i++) {
                                tvcontent.append(books.get(i).toString() + "\n");
                            }
                        }
                    });
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // כאן מעבירים בפרמטר הראשון את ה-ID של המסמך שרוצים לעדכן,
                // ובפרמטר השני את הערך החדש (למשל מה שכתוב בתיבת bookName).
                // ודא שאתה מכניס את ה-ID הנכון של המסמך (למשל לתוך תיבת author לצורך הבדיקה או שתשמור אותו מראש).
                String documentId = author.getText().toString().trim();
                String newName = bookName.getText().toString().trim();

                fsDAO.updateDocument(documentId, newName);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsDAO.deleteDocument(author.getText().toString().trim());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fsDAO.listenToAllDocumentsChange();
    }

    @Override
    protected void onStop()  {
        super.onStop();
        fsDAO.stopListening();
    }
}