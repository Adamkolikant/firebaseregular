package com.katza.adamproject20277;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDAO {

    private static final String COLLECTION_NAME = "books";
    private ListenerRegistration listenerRegistration;

    public void createDocument(String bookName, String author, String dateAdded) {
        Map<String, Object> bookRecord = new HashMap<>();
        bookRecord.put("bookName", bookName);
        bookRecord.put("author", author);
        bookRecord.put("dateAdded", dateAdded);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
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
                .collection(COLLECTION_NAME).document(docID);

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
                .collection(COLLECTION_NAME);

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

    public void updateDocument(String documentID, String newBookName) {
        if (documentID == null || documentID.trim().isEmpty()) {
            Log.d("DAO_FIREBASE", "UPDATE FAILED: Document ID is empty!");
            return;
        }

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(documentID);

        docRef.update("bookName", newBookName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAO_FIREBASE", "SUCCESSFUL UPDATE! ID: " + documentID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", "UPDATE FAILED: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            }
        });
    }

    public void deleteDocument(String documentID) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(documentID);

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAO_FIREBASE", "SUCCESSFUL DELETE!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", e.getMessage());
            }
        });
    }

    public void listenToADocumentChange(String docID) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(docID);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Book book = value.toObject(Book.class);
                    if (book != null) {
                        Log.d("CHANGED_DAO", "Document Changed: " + book.toString());
                    }
                }
            }
        });
    }

    public void listenToAllDocumentsChange() {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME);

        listenerRegistration = colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null) {
                    return;
                }

                for (DocumentChange docC : value.getDocumentChanges()) {
                    Book book = docC.getDocument().toObject(Book.class);
                    if (book != null) {
                        Log.d("DAO_MULTIPLE_CHANGES", docC.getType() + ": " + book.toString());
                    }
                }
            }
        });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    public void readQueryDocuments(String authorName, IFirestoreCallback iFirestoreCallback) {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME);

        colRef.whereEqualTo("author", authorName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Book> books = new ArrayList<>();
                for (DocumentSnapshot docSS : queryDocumentSnapshots.getDocuments()) {
                    Book book = docSS.toObject(Book.class);
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