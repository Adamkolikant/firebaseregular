
package com.katza.adamproject20277;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDAO {

    public void createDocument(String bookName, String author, String dateAdded) {
        Map<String, Object> bookRecord = new HashMap<>();
        bookRecord.put("bookName", bookName);
        bookRecord.put("author", author);
        bookRecord.put("dateAdded", dateAdded);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("books")
                .document();

        docRef.set(bookRecord).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAO_FIREBASE", "SUCCESSFUL WRITE!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        });
    }

    public void readDocumentById(String docID, IFirestoreCallback iFirestoreCallback) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("books").document(docID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("DAO_FIREBASE", "onSuccess");
                Book book = documentSnapshot.toObject(Book.class);
                Log.d("DAO_FIREBASE", "Book:" + book);

                ArrayList<Book> books = new ArrayList<>();
                if (book != null) {
                    books.add(book);
                }
                iFirestoreCallback.OnDataReceiver(books);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", "onFailure");
            }
        });
    }

    public void readAllDocuments(IFirestoreCallback iFirestoreCallback) {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection("books");

        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Book> books = new ArrayList<>();
                for (DocumentSnapshot docSS : queryDocumentSnapshots.getDocuments()) {
                    Book book = docSS.toObject(Book.class);
                    Log.d("DAO_FIREBASE", "Book:" + book);
                    if (book != null) {
                        books.add(book);
                    }
                }
                iFirestoreCallback.OnDataReceiver(books);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        });
    }
}
