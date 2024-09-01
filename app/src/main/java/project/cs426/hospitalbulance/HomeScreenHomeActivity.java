package project.cs426.hospitalbulance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenHomeActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_home);
        Button cancel = findViewById(R.id.cancle_button);
        cancel.setTextColor(Color.parseColor("#FFFFFF"));
        cancel.setBackgroundColor(Color.parseColor("#C53434"));

        ImageButton document = findViewById(R.id.document_button);
        document.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.document_button))
        {
            String username = "Ronaldo";
            Intent myIntent = new Intent(HomeScreenHomeActivity.this, HomeScreenRecordActivity.class);
            myIntent.putExtra("username", username); //Optional parameters
            this.startActivity(myIntent);
        }
    }
}
