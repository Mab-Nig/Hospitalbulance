package project.cs426.hospitalbulance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenRecordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_record);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        prepareContent(username);
        Button save = findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#C53434"));


    }

    private void prepareContent(String username) {

        RecyclerView bodyDetail = findViewById(R.id.body_measure);
        RecyclerView medicationDetail = findViewById(R.id.medications);
        RecyclerView symptonDetail = findViewById(R.id.symptons);
        RecyclerView otherDataDetail = findViewById(R.id.other_data);

        bodyDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        medicationDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        symptonDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherDataDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> details = new ArrayList<>();
        List<String> details1 = new ArrayList<>();
        List<String> details2 = new ArrayList<>();
        List<String> details3 = new ArrayList<>();

        //read body measurement
        readBodyMeasure(username, details);
        DetailRecordAdapter adapter = new DetailRecordAdapter(details);
        bodyDetail.setAdapter(adapter);

        //read medications
        readMedications(username, details1);
        DetailRecordAdapter adapter1 = new DetailRecordAdapter(details1);
        medicationDetail.setAdapter(adapter1);

        //read sympton
        readSympton(username, details2);
        DetailRecordAdapter adapter2 = new DetailRecordAdapter(details2);
        symptonDetail.setAdapter(adapter2);

        //read other data
        readotherData(username, details3);
        DetailRecordAdapter adapter3 = new DetailRecordAdapter(details3);
        otherDataDetail.setAdapter(adapter3);

    }

    private void readotherData(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        details.add("Alcohol Consumption - Light");
        details.add("Inhaler Usage - No");
        details.add("Number of times fallen - 2");
        details.add("Diabetic - No");
        details.add("Allergies - None");
        details.add("Special Abilities - None");

    }

    private void readSympton(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        details.add("Pain");
        details.add("Abdominal Cramps");
        details.add("Bloating");
    }

    private void readMedications(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        details.add("Combiflam Tablet");
        details.add("Duloxetine");
        details.add("Lubricating Injections");
    }

    private void readBodyMeasure(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        //do querry to get data
        details.add("Weight - 65kg");
        details.add("Height - 171cm");
    }


}
