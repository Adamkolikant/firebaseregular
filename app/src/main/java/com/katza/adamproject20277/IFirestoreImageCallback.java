package com.katza.adamproject20277;

public interface IFirestoreImageCallback<T> {
    void onSuccess (T result);

    void onFailure (Exception e);
}
