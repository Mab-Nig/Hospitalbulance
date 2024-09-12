package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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

public class HospitalRecordFragment extends Fragment {

    private RecyclerView recordRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> recordList;
    private FirebaseFirestore db;
    private ListenerRegistration recordListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_record, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        recordRecyclerView = view.findViewById(R.id.recordRecyclerView);
        recordList = new ArrayList<>();

        // Set up the RecyclerView and adapter
        adapter = new NotificationAdapter(getContext(), recordList, notification -> {
            Intent intent = new Intent(getActivity(), EmergencyDetail.class);
            intent.putExtra("callId", notification.getCallId());
            intent.putExtra("isFromRecord", true);
            startActivity(intent);
        });

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recordRecyclerView.setAdapter(adapter);

        // Listen for records from Firestore
        listenForAcceptedNotifications();

        return view;
    }

    private void listenForAcceptedNotifications() {
        recordListener = db.collection("calls")
                .whereEqualTo("is_accepted", "true")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching records.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = new Notification(
                                document.getString("case"),
                                document.getString("address"),
                                document.getString("car_id"),
                                document.getString("status"),
                                document.getTimestamp("timestamp").toDate().getTime(),
                                document.getId()
                        );
                        recordList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recordListener != null) {
            recordListener.remove();
        }
    }
}