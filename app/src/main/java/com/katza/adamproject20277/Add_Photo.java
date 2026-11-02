
package com.katza.adamproject20277;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class Add_Photo extends AppCompatActivity {

    private Button btnTakePhoto, btnSelectGallery, btnSavePhoto;
    private ImageView imgPreview;

    private FirestoreImageDAO firestoreImageDAO;

    private Bitmap selectedBitmap;
    private ActivityResultLauncher<Void> cameraPreviewLauncher;
    private ActivityResultLauncher<String> galleryImageLauncher;

    private String firestoreImageFileName = "my_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            firestoreImageFileName = savedInstanceState.getString(
                    "firestoreImageFileName", "my_image");
        }
        // שומר את המידע שהטלפון מסתובב

        firestoreImageDAO = new FirestoreImageDAO();

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectGallery = findViewById(R.id.btnSelectGallery);
        btnSavePhoto = findViewById(R.id.btnSavePhoto);

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
                            Bitmap bitmap = ImageUtils.convertUriToBitmap(
                                    Add_Photo.this, uri);

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

        firestoreImageDAO.saveToFirestore(
                firestoreImageFileName,
                base64,
                new IFirestoreImageCallback<Void>() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.action_add_book) {
            startActivity(new Intent(this, AddBook.class));
            return true;
        } else if (id == R.id.action_add_photo) {
            startActivity(new Intent(this, Add_Photo.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
