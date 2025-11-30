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

    private double operand1 = 0;
    private String operator = "";
    private boolean isNewInput = true;
    private static final String KEY_OPERAND = "Operand1";
    private static final String KEY_OPERATOR = "Operator";
    private static final String KEY_NEW_INPUT = "IsNewInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        if (savedInstanceState != null) {
            currentInput.setLength(0);
            currentInput.append(savedInstanceState.getString(KEY_INPUT, "0"));

            operand1 = savedInstanceState.getDouble(KEY_OPERAND, 0);
            operator = savedInstanceState.getString(KEY_OPERATOR, "");
            isNewInput = savedInstanceState.getBoolean(KEY_NEW_INPUT, true);
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_INPUT, currentInput.toString());
        outState.putDouble(KEY_OPERAND, operand1);
        outState.putString(KEY_OPERATOR, operator);
        outState.putBoolean(KEY_NEW_INPUT, isNewInput);

        super.onSaveInstanceState(outState);
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9, R.id.btnDot
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
        if (isNewInput) {
            currentInput.setLength(0);
            currentInput.append("0");
            isNewInput = false;
        }
        if (digit.equals(".")) {
            if (!currentInput.toString().contains(".")) {
                currentInput.append(digit);
                tvResult.setText(currentInput.toString());
            }
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
                resetCalculator();
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(v -> performOperation("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> performOperation("-"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> performOperation("/"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> performOperation("*"));
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
    }
    private void resetCalculator() {
        currentInput.setLength(0);
        currentInput.append("0");
        operand1 = 0;
        operator = "";
        isNewInput = true;
        tvResult.setText("0");
    }
    private double calculate(double op1, double op2, String op) {
        switch (op) {
            case "+":
                return op1 + op2;
            case "-":
                return op1 - op2;
            case "*":
                return op1 * op2;
            case "/":
                if (op2 == 0) {
                    return Double.NaN;
                }
                return op1 / op2;
            default:
                return op2;
        }
    }
    private void performOperation(String nextOperator) {
        try {
            double currentValue = Double.parseDouble(currentInput.toString());

            if (operator.isEmpty()) {
                operand1 = currentValue;
            } else {
                operand1 = calculate(operand1, currentValue, operator);
                if (Double.isNaN(operand1)) {
                    tvResult.setText("Error: Div/0");
                    resetCalculator();
                    return;
                }
                tvResult.setText(String.valueOf(operand1));
            }

            operator = nextOperator;
            isNewInput = true;

        } catch (NumberFormatException e) {
            tvResult.setText("Error");
            resetCalculator();
        }
    }

    private void calculateResult() {
        try {
            if (operator.isEmpty()) {
                isNewInput = true;
                return;
            }

            double op2 = Double.parseDouble(currentInput.toString());
            operand1 = calculate(operand1, op2, operator);

            if (Double.isNaN(operand1)) {
                tvResult.setText("Error: Div/0");
            } else {
                tvResult.setText(String.valueOf(operand1));
            }

            operator = "";
            isNewInput = true;

        } catch (NumberFormatException e) {
            tvResult.setText("Error");
            resetCalculator();
        }
    }

}