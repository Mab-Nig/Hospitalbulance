package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstAidCaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_aid_detail);

        // Get the data passed from the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int imageResId = intent.getIntExtra("imageResId", -1);  // Use -1 as a flag for no image

        // Set the title, content, and image
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(title);

        TextView caseContent = findViewById(R.id.caseContent);
        caseContent.setText(content);

        ImageView caseImage = findViewById(R.id.caseImage);
        if (imageResId != -1) {
            caseImage.setImageResource(imageResId);
        }

        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        backArrowButton.setOnClickListener(v -> onBackPressed());
    }
}
