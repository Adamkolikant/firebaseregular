package com.katza.adamproject20277;

import android.graphics.Bitmap;
import android.net.Uri;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText bookName, author, dateAdded;
    private TextView tvcontent;
    private Button btnInsert, btnSelect, btnUpdate, btnDelete, btnTakePhoto, btnSelectGallery, btnSavePhoto;
    private ImageView imgPreview;

    private FirestoreDAO fsDAO;
    private FirestoreImageDAO firestoreImageDAO;

    private Bitmap selectedBitmap;
    private ActivityResultLauncher<Void> cameraPreviewLauncher;
    private ActivityResultLauncher<String> galleryImageLauncher;
    private String firestoreImageFileName = "my_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            firestoreImageFileName = savedInstanceState.getString("firestoreImageFileName", "my_image");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fsDAO = new FirestoreDAO();
        firestoreImageDAO = new FirestoreImageDAO();

        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        dateAdded = findViewById(R.id.dateAdded);

        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectGallery = findViewById(R.id.btnSelectGallery);
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

        galleryImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri == null) {
                            showToastMessage("לא נבחרה תמונה");
                            return;
                        }
                        try {
                            Bitmap bitmap = ImageUtils.convertUriToBitmap(MainActivity.this, uri);
                            if (bitmap == null) {
                                showToastMessage("לא ניתן לטעון את התמונה");
                                return;
                            }
                            selectedBitmap = bitmap;
                            imgPreview.setImageBitmap(bitmap);
                            imgPreview.setVisibility(View.VISIBLE);
                        } catch (IOException | RuntimeException error) {
                            showToastMessage("שגיאה: " + error.getMessage());
                        }
                    }
                });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraPreviewLauncher.launch(null);
            }
        });

        btnSelectGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryImageLauncher.launch("image/*");
            }
        });

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBitmapAsBase64ToFirestore(selectedBitmap);
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

    private void saveBitmapAsBase64ToFirestore(Bitmap bitmap) {
        if (bitmap == null) {
            showToastMessage("צלם או בחר תמונה תחילה");
            return;
        }
        String base64 = ImageUtils.convertBitmapToBase64(bitmap);
        if (base64 == null) {
            showToastMessage("לא ניתן להמיר את התמונה ל-Base64");
            return;
        }
        firestoreImageDAO.saveToFirestore(firestoreImageFileName, base64, new IFirestoreImageCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showToastMessage("התמונה נשמרה ב-Firestore");
            }

            @Override
            public void onFailure(Exception error) {
                showToastMessage("שמירה נכשלה: " + error.getMessage());
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
    protected void onStop()  {
        super.onStop();
        fsDAO.stopListening();
    }
}