
package com.katza.adamproject20277;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class AddBook extends AppCompatActivity {

    private EditText bookName, author, dateAdded;
    private TextView tvcontent;

    private Button btnInsert, btnSelect, btnUpdate, btnDelete;

    private ImageView ivBase64Image;

    private FirestoreDAO fsDAO;
    private FirestoreImageDAO firestoreImageDAO;

    private Bitmap selectedBitmap;
    private String firestoreImageFileName = "my_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            firestoreImageFileName = savedInstanceState.getString(
                    "firestoreImageFileName", "my_image");
        }

        fsDAO = new FirestoreDAO();
        firestoreImageDAO = new FirestoreImageDAO();

        ivBase64Image = findViewById(R.id.ivBase64Image);

        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        dateAdded = findViewById(R.id.dateAdded);

        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        tvcontent = findViewById(R.id.tvcontent);

        readFromFirestore();

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

                String searchBookName = bookName.getText().toString().trim();

                if (!searchBookName.isEmpty()) {
                    fsDAO.readQueryDocuments(searchBookName, new IFirestoreCallback() {
                        @Override
                        public void OnDataReceiver(ArrayList<Book> books) {
                            for (int i = 0; i < books.size(); i++) {
                                tvcontent.append(books.get(i).toString() + "\n");
                            }
                        }
                    });
                } else {
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
                String documentId = bookName.getText().toString().trim();
                String newAuth = author.getText().toString().trim();
                String newDateadded = dateAdded.getText().toString().trim();

                fsDAO.updateDocument(documentId, newAuth, newDateadded);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = bookName.getText().toString().trim();
                String aut = author.getText().toString().trim();
                String date = dateAdded.getText().toString().trim();

                fsDAO.deleteDocumentByAllFields(name, aut, date);
            }
        });
    }

    private void readFromFirestore() {
        firestoreImageDAO.readFromFirestore(firestoreImageFileName, new IFirestoreImageCallback<String>() {
            @Override
            public void onSuccess(String base64) {
                Bitmap bitmap = ImageUtils.convertBase64ToBitmap(base64);

                if (bitmap == null) {
                    showToastMessage("לא ניתן לפענח את התמונה");
                    return;
                }

                selectedBitmap = bitmap;
                ivBase64Image.setImageBitmap(bitmap);
                showToastMessage("התמונה נטענה מ-Firestore");
            }

            @Override
            public void onFailure(Exception error) {
                showToastMessage("קריאה נכשלה: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("firestoreImageFileName", firestoreImageFileName);
        super.onSaveInstanceState(outState);
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fsDAO.listenToAllDocumentsChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsDAO.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_book) {
            Intent intent = new Intent(this, AddBook.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_photo) {
            Intent intent = new Intent(this, Add_Photo.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
