package project.cs426.hospitalbulance.backend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.Ambulance;
import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.Hospital;
import project.cs426.hospitalbulance.backend.database.Patient;
import project.cs426.hospitalbulance.backend.database.User;

public class Authenticator {
    private static final long TIMEOUT_SECONDS = 120L;
    private static final String TAG = "authentication";

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private String email, password;

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure();
    }
    private OnCompleteListener onCompleteListener;

    public Authenticator() {
//        final String localIp = " 169.254.190.21";// CHINH CAI NAY HA
//        try {
//            this.auth.useEmulator(localIp, 9099);
//            this.db.useEmulator(localIp, 8080);
//        } catch (IllegalStateException e) {
//            Log.e(TAG, e.getMessage());
//        }
    }

    public Authenticator setContext(Context context) {
        this.context = context;
        return this;
    }

    public Authenticator setEmail(String email) {
        this.email = email;
        return this;
    }

    public Authenticator setPassword(String password) {
        this.password = password;
        return this;
    }

    public Authenticator setOnCompleteListener(OnCompleteListener listener) {
        this.onCompleteListener = listener;
        return this;
    }

    public boolean isUserSignedIn() {
        return this.auth.getCurrentUser() != null;
    }

    public void signUp(@NonNull String role) {
        if (!Arrays.asList("patient", "hospital", "ambulance_owner")
                .contains(role)) {
            Log.e(TAG, "signUp:invalid role " + role);
            return;
        }

        this.auth.createUserWithEmailAndPassword(this.email, this.password)
                .addOnCompleteListener((Activity)this.context, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signUp:success");
                        FirebaseUser currentUser = FirebaseAuth.getInstance()
                                .getCurrentUser();
                        currentUser.sendEmailVerification();
                        addUserToDatabase(currentUser.getEmail(), role);
                        this.onCompleteListener.onSuccess();
                        return;
                    }

                    Log.e(TAG, "signUp:failure");
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        emitError("signUp", "Weak password: " + e.getReason());
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emitError("signUp", "Email malformed.");
                    } catch (FirebaseAuthUserCollisionException e) {
                        emitError("signUp", "Email already in use.");
                    } catch (Exception ignored) { }
                    this.onCompleteListener.onFailure();
                });
    }

    public void signIn() {
        this.auth.signInWithEmailAndPassword(this.email, this.password)
                .addOnCompleteListener((Activity)this.context, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signIn:success");
                        this.onCompleteListener.onSuccess();
                        return;
                    }
                    Log.e(TAG, "signIn: " + this.email + " && " + this.password );
                    Log.e(TAG, "signIn:failure");
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emitError("signIn", "User does not exist.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emitError("signIn", "Wrong password.");
                    } catch (Exception ignored) { }
                    this.onCompleteListener.onFailure();
                });
    }

    public void signOut() {
        this.auth.signOut();
    }

    private void addUserToDatabase(@NonNull String email, @NonNull String role) {
        role = role.toLowerCase();
        final Map<String, String> roleToCollection = new HashMap<>();
        roleToCollection.put("patient", Collections.PATIENTS);
        roleToCollection.put("hospital", Collections.HOSPITALS);
        roleToCollection.put("ambulance_owner", Collections.AMBULANCE_OWNERS);

        String currentUserUid = this.auth.getCurrentUser().getUid();
        WriteBatch batch = this.db.batch();

        DocumentReference userRef = this.db.collection(Collections.USERS)
                .document(currentUserUid);
        batch.set(userRef, new User(email, role.toLowerCase()));

        DocumentReference roleRef = this.db.collection(roleToCollection.get(role))
                .document(currentUserUid);
        switch (role) {
        case "patient":
            batch.set(roleRef, new Patient(email));
            break;
        case "hospital":
            batch.set(roleRef, new Hospital(email));
            break;
        case "ambulance_owner":
            batch.set(roleRef, new Ambulance(email));
            break;
        default:
            break;
        }

        batch.commit().addOnCompleteListener((Activity)this.context, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "addUserToDatabase:success");
            } else {
                Log.e(TAG, "addUserToDatabase:failure");
                try {
                    throw task.getException();
                } catch (Exception e) {
                    Log.e(TAG, "addUserToDatabase:" + e.getMessage());
                }
            }
        });
    }

    private void emitError(String functionName, String msg) {
        Log.e(TAG, functionName + ":" + msg);
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT)
                .show();
    }
}
