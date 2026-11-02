
package com.katza.adamproject20277;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btnAddBook, btnAddPhoto;
    private ImageView ivBase64Image;

    private FirestoreImageDAO firestoreImageDAO;
    private Bitmap selectedBitmap;
    private String firestoreImageFileName = "my_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            firestoreImageFileName = savedInstanceState.getString(
                    "firestoreImageFileName", "my_image");
        }

        firestoreImageDAO = new FirestoreImageDAO();

        ivBase64Image = findViewById(R.id.ivBase64Image);

        btnAddBook = findViewById(R.id.btnAddBook);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);

        readFromFirestore();

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBook.class);
                startActivity(intent);
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add_Photo.class);
                startActivity(intent);
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
    protected void onSaveInstanceState(Bundle outState) {
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
