package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import project.cs426.hospitalbulance.backend.Authenticator;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new Handler().postDelayed(() -> {
			Authenticator auth = new Authenticator().setContext(this);
			if (auth.isUserSignedIn()) {
				Log.d("SplashScreen", "isUserSignedIn:true");
				Intent intent = new Intent(this, HomeScreenHomeActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(SplashScreen.this, NavigationActivity.class);
				startActivity(intent);
			}
			finish();
		},3000);
    }
}