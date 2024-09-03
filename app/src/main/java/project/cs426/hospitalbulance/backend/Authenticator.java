package project.cs426.hospitalbulance.backend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
	private static final String TAG = "login";

	private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private Context mContext;
	private String mEmail, mPassword;

	public interface OnCompleteListener {
		void onSuccess();
		void onFailed();
	}
	private OnCompleteListener mOnCompleteListener;

	public Authenticator setContext(Context context) {
		mContext = context;
		return this;
	}

	public Authenticator setEmail(String email) {
		mEmail = email;
		return this;
	}

	public Authenticator setPassword(String password) {
		mPassword = password;
		return this;
	}

	public Authenticator setOnCompleteListener(OnCompleteListener listener) {
		mOnCompleteListener = listener;
		return this;
	}

	public boolean isUserSignedIn() {
		return mAuth.getCurrentUser() != null;
	}

	public void signUp(String role) {
		role = role.toLowerCase();
		if (!Arrays.asList("PATIENT", "HOSPITAL", "AMBULANCE_OWNER").contains(role.toUpperCase())) {
			Log.w(TAG, "signUp:invalid role " + role);
			return;
		}

		String finalRole = role;
		mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
				.addOnCompleteListener((Activity) mContext, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signUp:success");
						addUserToDatabase(finalRole);
						mOnCompleteListener.onSuccess();
					} else {
						Log.w(TAG, "signUp:failure");
						mOnCompleteListener.onFailed();
					}
				});
	}

	public void signIn() {
		mAuth.signInWithEmailAndPassword(mEmail, mPassword)
				.addOnCompleteListener((Activity) mContext, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signIn:success");
						mOnCompleteListener.onSuccess();
					} else {
						Log.w(TAG, "signIn:failure");
						mOnCompleteListener.onFailed();
					}
				});
	}

	private void addUserToDatabase(String role) {
		final Map<String, String> roleToCollection = new HashMap<>();
		roleToCollection.put("patient", Collections.PATIENTS);
		roleToCollection.put("hospital", Collections.HOSPITALS);
		roleToCollection.put("ambulance_owner", Collections.AMBULANCE_OWNERS);

		String currentUserUid = mAuth.getCurrentUser().getUid();
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		WriteBatch batch = db.batch();

		DocumentReference userRef = db.collection(Collections.USERS).document(currentUserUid);
		batch.set(userRef, new User(role));

		DocumentReference roleRef = db.collection(roleToCollection.get(role)).document(currentUserUid);
		switch (role) {
		case "patient":
			batch.set(roleRef, new Patient());
			break;
		case "hospital":
			batch.set(roleRef, new Hospital());
			break;
		case "ambulance_owner":
			batch.set(roleRef, new AmbulanceOwner());
			break;
		default:
			break;
		}

		batch.commit().addOnCompleteListener((Activity) mContext, task -> {
			if (task.isSuccessful()) {
				Log.d(TAG, "addUserToDatabase:success");
			} else {
				Log.w(TAG, "addUserToDatabase:failure");
			}
		});
	}
}
