package org.izv.aad.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
    reglas de seguridad
    {
        "rules": {
            "data": {
                ".read" : "auth != null",
                ".write": "auth != null",
            },
            "users": {
                "$uid": {
                    ".read": "$uid === auth.uid",
                    ".write": "$uid === auth.uid",
                }
            }
        }
    }
    */

    public static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private Item i = new Item("1", "nombre", "mensaje");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseDatabase.setPersistenceEnabled(true);
        signIn("abc@abc.es", "abcdef");
        //saveItem(i);
        //saveItem(i, "/data/");
        //saveItem(i, "/users/" + firebaseUser.getUid() + "/");
    }

    public void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Log.v(TAG, "User: " + firebaseUser.getEmail());
                    firebaseUser = task.getResult().getUser();
                    Log.v(TAG, "User: " + firebaseUser.getEmail());
                    Log.v(TAG, "User: " + firebaseUser.getUid());
                    sendVerificationEmail(firebaseUser);
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    public void query() {
        Query query = databaseReference.child("item");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                     @Nullable String s) {
                DatabaseReference reference = dataSnapshot.getRef();
                Item item = dataSnapshot.getValue(Item.class);
                Log.v(TAG, item.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void removeReference(DatabaseReference reference) {
        reference.removeValue();
    }

    public void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Reset email sent.");
                }
            }
        });
    }

    private void saveItem(Item i, String route, String key) {
        Map<String, Object> saveItem = new HashMap<>();
        saveItem.put(route + key + "/", i.toMap());
        databaseReference.updateChildren(saveItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.v(TAG, "Resultado: " + task.isSuccessful());
            }
        });
    }

    public void saveItem(Item item) {
        saveItem(item, "/data/");
    }

    public void saveItem(Item item, FirebaseUser user) {
        String route = "/users/" + user.getUid() + "/";
        saveItem(item, route);
    }

    public void saveItem(Item item, String route) {
        Map<String, Object> saveItem = new HashMap<>();
        String key = databaseReference.child("item").push().getKey();
        Log.v(TAG, key);
        saveItem.put(route + key + "/", item.toMap());
        databaseReference.updateChildren(saveItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.v(TAG, "Resultado: " + task.isSuccessful());
            }
        });
    }

    public void saveUser(FirebaseUser user) {
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/users/" + user.getUid() + "/user/", user.getEmail());
        databaseReference.updateChildren(saveUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "User saved");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    public void sendVerificationEmail(final FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "Verification mail sended");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    public void setPassword(FirebaseUser user, String password) {
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User password changed.");
                }
            }
        });
    }

    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    saveUser(firebaseUser);
                    query();
                    Log.v(TAG, "Email verified: " + firebaseUser.isEmailVerified());
                    Log.v(TAG, "User: " + firebaseUser.getEmail());
                    Log.v(TAG, "User: " + firebaseUser.getUid());
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    public void updateReference(Item item, DatabaseReference reference) {
        reference.setValue(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

            }
        });
    }
}