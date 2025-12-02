package com.example.gorbunov_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.gorbunov_calculator.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.nav_open,
                R.string.nav_close
        );

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);


        if (savedInstanceState != null) {
            currentInput.setLength(0);
            currentInput.append(savedInstanceState.getString(KEY_INPUT, "0"));

            operand1 = savedInstanceState.getDouble(KEY_OPERAND, 0);
            operator = savedInstanceState.getString(KEY_OPERATOR, "");
            isNewInput = savedInstanceState.getBoolean(KEY_NEW_INPUT, true);
        }
        binding.tvResult.setText(currentInput.toString());

        setupNumberButtons();
        setupClearButton();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            binding.getRoot().findViewById(id).setOnClickListener(numberClickListener);
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
                binding.tvResult.setText(currentInput.toString());
            }
            return;
        }

        if (currentInput.toString().equals("0")) {
            currentInput.setLength(0);
        }

        if (currentInput.length() < 16) {
            currentInput.append(digit);
            binding.tvResult.setText(currentInput.toString());
        }
    }

    private void setupClearButton() {
        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearConfirmationDialog();
            }
        });

        binding.btnPlus.setOnClickListener(v -> performOperation("+"));
        binding.btnMinus.setOnClickListener(v -> performOperation("-"));
        binding.btnDivide.setOnClickListener(v -> performOperation("/"));
        binding.btnMultiply.setOnClickListener(v -> performOperation("*"));
        binding.btnEquals.setOnClickListener(v -> calculateResult());
    }
    private void resetCalculator() {
        currentInput.setLength(0);
        currentInput.append("0");
        operand1 = 0;
        operator = "";
        isNewInput = true;
        binding.tvResult.setText("0");
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
                    binding.tvResult.setText("Error: Div/0");
                    resetCalculator();
                    return;
                }
                binding.tvResult.setText(String.valueOf(operand1));
            }

            operator = nextOperator;
            isNewInput = true;

        } catch (NumberFormatException e) {
            binding.tvResult.setText("Error");
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
                binding.tvResult.setText("Error: Div/0");
            } else {
                binding.tvResult.setText(String.valueOf(operand1));
            }

            operator = "";
            isNewInput = true;

        } catch (NumberFormatException e) {
            binding.tvResult.setText("Error");
            resetCalculator();
        }

    }
    private void showClearConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Сбросить калькулятор?")
                .setMessage("Вы уверены, что хотите очистить текущий результат и операции?");

        builder.setPositiveButton("СБРОСИТЬ", (dialog, id) -> {
            resetCalculator();
        });

        builder.setNegativeButton("ОТМЕНА", (dialog, id) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}