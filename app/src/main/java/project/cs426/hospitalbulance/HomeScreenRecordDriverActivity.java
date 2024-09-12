package project.cs426.hospitalbulance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenRecordDriverActivity extends AppCompatActivity implements View.OnClickListener  {

    private String carID = "";
    private String modelCar = "";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulance_record_screen);
        Intent getintent = getIntent();
        carID = getintent.getStringExtra("carID");

        ConstraintLayout screen = findViewById(R.id.screen);

        Button save = findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#808080"));
        save.setOnClickListener(this);

        ListView listcall = findViewById(R.id.list_call);
        List<String> data_call = new ArrayList<String>();

        EditText carIDtext = findViewById(R.id.carID);
        carIDtext.setText(carID);

        modelCar = "HYUNDAI STAIREX"; //Perform reading car information query here

        EditText carModel = findViewById(R.id.carModel);
        carModel.setText(modelCar);

        readCalldata(data_call);

        callAdapter calls = new callAdapter(this, data_call);
        listcall.setAdapter(calls);

        carModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(carModel.getText());
                    if (!content.isEmpty() && !content.equals(modelCar))
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#808080"));
                        modelCar = content;
                    }
                }
            }
        });

        carIDtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(carIDtext.getText());
                    if (!content.isEmpty() && !content.equals(carID))
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#808080"));
                        carID = content;
                    }
                }
            }
        });

        screen.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // Clear focus from the EditText
                stopEnter(carModel);
                stopEnter(carIDtext);
                return false;
            }

            private void stopEnter(EditText adultNum) {
                if (adultNum.hasFocus()) {
                    adultNum.clearFocus();
                    // Optionally, hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(adultNum.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private void readCalldata(List<String> dataCall) {
        //perform read data from the call that this ambulance hold (using carID)
        //The structure of dataCall:
        //Date1 email1 place1 Date2 email2 place2 ....
        dataCall.add("2/9/2024");
        dataCall.add("user1@gmail.com");
        dataCall.add("Nguyen Van Cu Street");

        dataCall.add("3/9/2024");
        dataCall.add("user2@gmail.com");
        dataCall.add("HCMUS");

    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.save_button))
        {
            Button save = findViewById(R.id.save_button);
            //ENTERING NEW DATA TO DATABASE
            save.setBackgroundColor(Color.parseColor("#00CF00"));
        }
    }
}
