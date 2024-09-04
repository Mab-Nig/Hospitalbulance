package project.cs426.hospitalbulance;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailRecordAdapter extends RecyclerView.Adapter<DetailRecordAdapter.MyViewHolder>{

    private List<String> dataList;

    public interface onClickDetailListener
    {
        void onclick(View v);
    }

    private static onClickDetailListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        EditText detail;

        public MyViewHolder(View v) {
            super(v);
            detail = v.findViewById(R.id.detail);
            //edit initialize a detail
        }
    }
    public DetailRecordAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public DetailRecordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.detail.setText(dataList.get(position));

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listener != null)
                {
                    listener.onclick(view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setListener(DetailRecordAdapter.onClickDetailListener listener)
    {
        this.listener = listener;
    }
}
