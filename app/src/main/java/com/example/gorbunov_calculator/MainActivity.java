package com.example.gorbunov_calculator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView tvResult;
    private StringBuilder currentInput = new StringBuilder("0");
    private static final String KEY_INPUT = "CurrentInput";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_INPUT, currentInput.toString());

        super.onSaveInstanceState(outState);
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                appendDigit(b.getText().toString());
            }
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }
    }

    private void appendDigit(String digit) {
        if (currentInput.toString().equals("0") && digit.equals("0")) {
            return;
        }

        if (currentInput.toString().equals("0")) {
            currentInput.setLength(0);
        }

        if (currentInput.length() < 16) {
            currentInput.append(digit);
            tvResult.setText(currentInput.toString());
        }
    }

    private void setupClearButton() {
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput.setLength(0);
                currentInput.append("0");
                tvResult.setText(currentInput.toString());
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(v -> appendDigit("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> appendDigit("-"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> appendDigit("/"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> appendDigit("*"));
        findViewById(R.id.btnEquals).setOnClickListener(v -> appendDigit("="));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        if (savedInstanceState != null) {
            currentInput.setLength(0);
            currentInput.append(savedInstanceState.getString(KEY_INPUT, "0"));
        }
        tvResult.setText(currentInput.toString());

        setupNumberButtons();
        setupClearButton();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }
}