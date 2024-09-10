package project.cs426.hospitalbulance;

import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class callAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mData;
    public callAdapter(@NonNull Context context, List<String> data) {
        super(context, R.layout.ambulance_call_layout, data);
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        // Return the number of items (each item uses 3 elements in mData)
        return mData.size() / 3;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItemView = inflater.inflate(R.layout.ambulance_call_layout, parent, false);
        }

        // Get reference to views in list_item_layout.xml
        TextView date = listItemView.findViewById(R.id.date_call);
        TextView caller = listItemView.findViewById(R.id.call_email);
        TextView call_place = listItemView.findViewById(R.id.call_place);

        // Set data to views
        int baseIndex = position * 3;

        // Ensure that the indices are within the bounds of mData
        if (baseIndex < mData.size() && baseIndex + 2 < mData.size()) {
            // Set data to views
            date.setText(mData.get(baseIndex));
            caller.setText(mData.get(baseIndex + 1));
            call_place.setText(mData.get(baseIndex + 2));
        } else {
            date.setText("N/A");
            caller.setText("N/A");
            call_place.setText("N/A");
        }

        return listItemView;
    }
}
