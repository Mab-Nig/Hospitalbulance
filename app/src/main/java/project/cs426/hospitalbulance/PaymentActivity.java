package project.cs426.hospitalbulance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PaymentActivity extends AppCompatActivity {
    private Button bookingButton;
    public RadioButton btn1,btn2,btn3,btn4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btn1 = findViewById(R.id.radio_visa);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn1.setChecked(true);
                btn2.setChecked(false);
            }
        });
        btn2 = findViewById(R.id.radio_mastercard);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn1.setChecked(false);
                btn2.setChecked(true);
            }
        });
        btn3 = findViewById(R.id.radio_cash);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn3.setChecked(true);
                btn4.setChecked(false);
            }
        });
        btn4 = findViewById(R.id.radio_paypal);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn3.setChecked(false);
                btn4.setChecked(true);
            }
        });
        findViewById(R.id.back_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bookingButton = findViewById(R.id.booking_button);




    }

}