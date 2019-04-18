package com.wisethan.testtrack.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wisethan.testtrack.model.StorageFileModel;
import com.wisethan.testtrack.model.UserModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();

    private static final String STORAGE_BASE_URL = "gs://my-wisethan-project.appspot.com";

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private UploadTask mUploadTask;

    private static RequestManager mInstance;
    public static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    public RequestManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance(STORAGE_BASE_URL);
    }

    public interface SuccessCallback {
        public void onResponse(boolean success);
    }

    public interface UserCallback {
        public void onResponse(UserModel response);
    }

    public interface StorageFileCallback {
        public void onResponse(StorageFileModel response);
    }

    public void requestGetUserInfo(String userid, final UserCallback callback) {
        DocumentReference docRef = mFirestore.collection("user").document(userid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                UserModel model = new UserModel(documentSnapshot.getData());
                callback.onResponse(model);
            }
        });
    }

    public void requestSetUserInfo(UserModel data, final SuccessCallback callback) {
        DocumentReference docRef;
        if (data.getUserId().isEmpty()) {
            docRef = mFirestore.collection("user").document();
            String docid = docRef.getId();
            data.setUserId(docid);

        } else {
            docRef = mFirestore.collection("user").document(data.getUserId());
        }

        docRef.set(data.getData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User data successfully written!");
                        callback.onResponse(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing user document", e);
                        callback.onResponse(false);
                    }
                });
    }

    public void requestDownloadFileFromStorage(String name, String path, final String suffix, final StorageFileCallback callback) {
        StorageReference downRef = mStorage.getReference(path);
        try {
            final File localFile = File.createTempFile(name, suffix);
            downRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file download success (" + taskSnapshot.getTotalByteCount() + ", " + localFile.getAbsolutePath() + ")");
                    Map<String, Object> values = new HashMap<>();
                    values.put("path", localFile.getAbsolutePath());
                    values.put("suffix", suffix);
                    values.put("size", taskSnapshot.getTotalByteCount());

                    StorageFileModel data = new StorageFileModel(values);
                    callback.onResponse(data);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "onFailure: file download failed (" + exception.getMessage() + ")");
                    Map<String, Object> values = new HashMap<>();
                    values.put("error", exception.getMessage());

                    StorageFileModel data = new StorageFileModel(values);
                    callback.onResponse(data);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
