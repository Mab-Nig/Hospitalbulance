package project.cs426.hospitalbulance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private OnItemClickListener listener;

    public NotificationAdapter(Context context, List<Notification> notificationList, OnItemClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.caseType.setText(notification.getCaseType() != null ? notification.getCaseType() : "N/A");
        holder.address.setText(notification.getAddress() != null ? notification.getAddress() : "N/A");

        // Set onClickListener to handle item click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(notification));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView caseType, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            caseType = itemView.findViewById(R.id.typeOfEmergencyTextView);
            address = itemView.findViewById(R.id.addressTextView);
        }
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }
}
