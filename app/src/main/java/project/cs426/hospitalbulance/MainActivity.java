package project.cs426.hospitalbulance;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_record);

        RecyclerView bodyDetail = findViewById(R.id.body_measure);
        bodyDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> body = new ArrayList<>();
        //read body measurement
        readBodyMeasure(body);
        DetailRecordAdapter adapter = new DetailRecordAdapter(body);
        bodyDetail.setAdapter(adapter);

    }

    private void readBodyMeasure(List<String> body) {
        //do querry to get data
        body.add("Weight - 65kg");
        body.add("Height - 171cm");
    }
}