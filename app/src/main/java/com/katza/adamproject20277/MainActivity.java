package com.katza.adamproject20277;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText bookName, author, dateAdded;
    private TextView tvcontent;
    private Button btnInsert, btnSelect, btnUpdate, btnDelete, btnTakePhoto, btnSavePhoto;
    private ImageView imgPreview;

    private FirestoreDAO fsDAO;
    private FirestoreImageDAO imageDAO;

    private Bitmap selectedBitmap;
    private ActivityResultLauncher<Void> cameraPreviewLauncher;

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
        imageDAO = new FirestoreImageDAO();

        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        dateAdded = findViewById(R.id.dateAdded);

        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSavePhoto = findViewById(R.id.btnSavePhoto);

        tvcontent = findViewById(R.id.tvcontent);
        imgPreview = findViewById(R.id.imgPreview);

        cameraPreviewLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap bitmap) {
                        if (bitmap == null) {
                            showToastMessage("הצילום בוטל");
                            return;
                        }
                        selectedBitmap = bitmap;
                        imgPreview.setImageBitmap(bitmap);
                        imgPreview.setVisibility(View.VISIBLE);
                    }
                });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraPreviewLauncher.launch(null);
            }
        });

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedBitmap == null) {
                    showToastMessage("לא צולמה תמונה");
                    return;
                }

                String base64Str = ImageUtils.convertBitmapToString(selectedBitmap);
                if (base64Str == null) {
                    showToastMessage("שגיאה בהמרת התמונה");
                    return;
                }

                String randomFileName = UUID.randomUUID().toString();

                imageDAO.saveToFirestore(randomFileName, base64Str, new IFirestoreImageCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        showToastMessage("התמונה נשמרה בהצלחה עם מזהה רנדומלי");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        showToastMessage("שגיאה בשמירה: " + e.getMessage());
                    }
                });
            }
        });

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

                if (!searchAuthor.isEmpty()) {
                    fsDAO.readQueryDocuments(searchAuthor, new IFirestoreCallback() {
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
                String documentId = author.getText().toString().trim();
                String newName = bookName.getText().toString().trim();
                String newDateadded = dateAdded.getText().toString().trim();

                fsDAO.updateDocument(documentId, newName, newDateadded);
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

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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