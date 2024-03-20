package com.tumerkankarabel.javacalculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tumerkankarabel.javacalculatorapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeButtons();
    }

    private void initializeButtons() {
        binding.button0.setOnClickListener(this);
        binding.button1.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        binding.button4.setOnClickListener(this);
        binding.button5.setOnClickListener(this);
        binding.button6.setOnClickListener(this);
        binding.button7.setOnClickListener(this);
        binding.button8.setOnClickListener(this);
        binding.button9.setOnClickListener(this);
        binding.buttonClear.setOnClickListener(this);
        binding.buttonClearAll.setOnClickListener(this);
        binding.buttonDivide.setOnClickListener(this);
        binding.buttonMultiply.setOnClickListener(this);
        binding.buttonMode.setOnClickListener(this);
        binding.buttonMinus.setOnClickListener(this);
        binding.buttonAdd.setOnClickListener(this);
        binding.buttonEqual.setOnClickListener(this);
        binding.buttonDot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        String buttonText = clickedButton.getText().toString();

        switch (buttonText) {
            case "AC":
                input = "";
                break;
            case "C":
                if (!input.isEmpty())
                    input = input.substring(0, input.length() - 1);
                break;
            case "=":
                try {
                    double result = evaluateExpression(input);
                    input = String.valueOf(result);
                } catch (ArithmeticException e) {
                    input = "Error";
                }
                break;
            default:
                input += buttonText;
                break;
        }

        binding.textViewResult.setText(input);
    }

    private double evaluateExpression(String expression) {
        // İfadeyi temizle
        expression = expression.replaceAll(" ", "");

        // İfadeyi parçala ve işlem yapılacak parçaları bul
        List<String> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    tokens.add(currentNumber.toString());
                    currentNumber = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (currentNumber.length() > 0) {
            tokens.add(currentNumber.toString());
        }

        // İşlem önceliği olan parantezlerin işlemi
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("(")) {
                int start = i;
                int end = start;
                for (int j = start + 1; j < tokens.size(); j++) {
                    if (tokens.get(j).equals(")")) {
                        end = j;
                        break;
                    }
                }
                List<String> subExpression = new ArrayList<>(tokens.subList(start + 1, end));
                double result = evaluateExpression(String.join("", subExpression));
                tokens.subList(start, end + 1).clear();
                tokens.add(start, String.valueOf(result));
                i = 0; // Yeni bir işlem bulunduğunda başa dönmek
            }
        }

        // Mod almak işleminin yapılması
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("%")) {
                double num1 = Double.parseDouble(tokens.get(i - 1));
                double num2 = Double.parseDouble(tokens.get(i + 1));
                double result = num1 % num2;
                tokens.subList(i - 1, i + 2).clear();
                tokens.add(i - 1, String.valueOf(result));
                i--; // Bir sonraki işlemi doğru hesaplamak için indeksi düşür
            }
        }

        // Çarpma ve bölme işlemlerinin yapılması
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("*") || tokens.get(i).equals("/")) {
                double num1 = Double.parseDouble(tokens.get(i - 1));
                double num2 = Double.parseDouble(tokens.get(i + 1));
                double result = tokens.get(i).equals("*") ? num1 * num2 : num1 / num2;
                tokens.subList(i - 1, i + 2).clear();
                tokens.add(i - 1, String.valueOf(result));
                i--;
            }
        }

        // Toplama ve çıkarma işlemlerinin yapılması
        double result = Double.parseDouble(tokens.get(0));
        for (int i = 1; i < tokens.size(); i += 2) {
            double num = Double.parseDouble(tokens.get(i + 1));
            if (tokens.get(i).equals("+")) {
                result += num;
            } else if (tokens.get(i).equals("-")) {
                result -= num;
            }
        }
        return result;
    }

}

