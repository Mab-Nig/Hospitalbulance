package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class HospitalHomeFragment extends Fragment {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private ListenerRegistration notificationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        notificationRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        notificationList = new ArrayList<>();

        // Set up the RecyclerView and adapter
        adapter = new NotificationAdapter(getContext(), notificationList, notification -> {
            Intent intent = new Intent(getActivity(), EmergencyDetail.class);
            intent.putExtra("callId", notification.getCallId());
            intent.putExtra("isFromHome", true);
            startActivity(intent);
        });

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationRecyclerView.setAdapter(adapter);

        // Start listening to Firestore for notifications
        listenForNotifications();

        return view;
    }

    private void listenForNotifications() {
        notificationListener = db.collection("calls")
                .whereEqualTo("is_accepted", "false")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) return;
                    notificationList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = new Notification(
                                document.getString("case"),
                                document.getString("address"),
                                document.getString("car_id"),
                                document.getString("status"),
                                document.getTimestamp("timestamp").toDate().getTime(),
                                document.getId()
                        );
                        notificationList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notificationListener != null) {
            notificationListener.remove();
        }
    }
}