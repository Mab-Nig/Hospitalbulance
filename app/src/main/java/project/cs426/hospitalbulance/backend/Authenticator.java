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

import project.cs426.hospitalbulance.backend.database.AmbulanceOwner;
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
		final String localIp = "192.168.1.5";
		this.auth.useEmulator(localIp, 9099);
		this.db.useEmulator(localIp, 8080);
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
		role = role.toUpperCase();
		if (!Arrays.asList("PATIENT", "HOSPITAL", "AMBULANCE_OWNER").contains(role)) {
			Log.e(TAG, "signUp:invalid role " + role);
			return;
		}

		String finalRole = role;
		this.auth.createUserWithEmailAndPassword(this.email, this.password)
				.addOnCompleteListener((Activity) this.context, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signUp:success");
						FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
						currentUser.sendEmailVerification();
						addUserToDatabase(finalRole);
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
					} catch (Exception ignored) {}
					this.onCompleteListener.onFailure();
				});
	}

	public void signIn() {
		this.auth.signInWithEmailAndPassword(this.email, this.password)
				.addOnCompleteListener((Activity) this.context, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signIn:success");
						this.onCompleteListener.onSuccess();
						return;
					}

					Log.e(TAG, "signIn:failure");
					try {
						throw task.getException();
					} catch (FirebaseAuthInvalidUserException e) {
						emitError("signIn", "User does not exist.");
					} catch (FirebaseAuthInvalidCredentialsException e) {
						emitError("signIn", "Wrong password.");
					} catch (Exception ignored) {}
					this.onCompleteListener.onFailure();
				});
	}

	public void signOut() {
		this.auth.signOut();
	}

	private void addUserToDatabase(@NonNull String role) {
		final Map<String, String> roleToCollection = new HashMap<>();
		roleToCollection.put("PATIENT", Collections.PATIENTS);
		roleToCollection.put("HOSPITAL", Collections.HOSPITALS);
		roleToCollection.put("AMBULANCE_OWNER", Collections.AMBULANCE_OWNERS);

		String currentUserUid = this.auth.getCurrentUser().getUid();
		WriteBatch batch = this.db.batch();

		DocumentReference userRef = this.db.collection(Collections.USERS).document(currentUserUid);
		batch.set(userRef, new User(role.toLowerCase()));

		DocumentReference roleRef = this.db.collection(roleToCollection.get(role)).document(currentUserUid);
		switch (role) {
		case "PATIENT":
			batch.set(roleRef, new Patient());
			break;
		case "HOSPITAL":
			batch.set(roleRef, new Hospital());
			break;
		case "AMBULANCE_OWNER":
			batch.set(roleRef, new AmbulanceOwner());
			break;
		default:
			break;
		}

		batch.commit().addOnCompleteListener((Activity) this.context, task -> {
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
