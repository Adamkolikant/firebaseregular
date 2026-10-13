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

    public void createDocument(String name, String id, int grade) {
        Map<String, Object> studentRecord = new HashMap<>();
        studentRecord.put("fullName", name);
        studentRecord.put("id", id);
        studentRecord.put("grade", grade);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("ddd")
                .document(id);

        docRef.set(studentRecord).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                .collection("ddd").document(docID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("DAO_FIREBASE", "onSuccess");
                Student student = documentSnapshot.toObject(Student.class);
                Log.d("DAO_FIREBASE", "Student:" + student);

                ArrayList<Student> students = new ArrayList<>();
                students.add(student);
                iFirestoreCallback.OnDataReceiver(students);
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
                .collection("ddd");

        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Student> students = new ArrayList<>();
                for (DocumentSnapshot docSS : queryDocumentSnapshots.getDocuments()) {
                    Student student = docSS.toObject(Student.class);
                    Log.d("DAO_FIREBASE", "Student:" + student);
                    students.add(student);
                }
                iFirestoreCallback.OnDataReceiver(students);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        });
    }
}