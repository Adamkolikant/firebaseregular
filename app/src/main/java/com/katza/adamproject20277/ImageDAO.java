package com.katza.adamproject20277;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ImageDAO {
    private static final String COLLECTION_IMAGE_NAME = "images";

    public void save(
            String name,
            String type,
            String data,
            IFirestoreImageCallback<Void> callback
    ) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("type", type);
        item.put("data", data);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_IMAGE_NAME)
                .document(name);

        docRef.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }
}