package com.calculator;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONException;

public class CalculatorLogic {
    private static final String TAG = "CalculatorLogic";
    private static final String API_BASE_URL = "http://10.0.2.2:8000/api";
    
    private Context context;
    private LinearLayout mainLayout;
    private NativeCalculatorViewManager viewManager;
    
    // Состояние калькулятора
    private String currentInput = "0";
    private String operator = "";
    private double firstNumber = 0;
    private boolean waitingForOperand = false;
    private boolean useAPI = true;

    public CalculatorLogic(Context context, LinearLayout mainLayout, NativeCalculatorViewManager viewManager) {
        this.context = context;
        this.mainLayout = mainLayout;
        this.viewManager = viewManager;
        Log.d(TAG, "CalculatorLogic initialized");
    }

    public void onButtonClick(String buttonText) {
        Log.d(TAG, "Button clicked: " + buttonText);
        
        switch (buttonText) {
            case "C":
                clear();
                break;
            case "±":
                toggleSign();
                break;
            case "⌫":
                backspace();
                break;
            case ".":
                inputDecimal();
                break;
            case "=":
                calculate();
                break;
            case "+":
            case "−":
            case "×":
            case "÷":
                inputOperator(buttonText);
                break;
            default:
                if (buttonText.matches("[0-9]")) {
                    inputNumber(buttonText);
                }
                break;
        }
    }

    private void inputNumber(String num) {
        if (waitingForOperand) {
            currentInput = num;
            waitingForOperand = false;
        } else {
            currentInput = currentInput.equals("0") ? num : currentInput + num;
        }
        updateDisplay();
    }

    private void inputDecimal() {
        if (waitingForOperand) {
            currentInput = "0.";
            waitingForOperand = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    private void inputOperator(String op) {
        if (!operator.isEmpty()) {
            calculate();
        }
        
        firstNumber = Double.parseDouble(currentInput);
        operator = op;
        waitingForOperand = true;
    }

    private void calculate() {
        if (!operator.isEmpty()) {
            double secondNumber = Double.parseDouble(currentInput);
            String expression = firstNumber + " " + operator + " " + secondNumber;

            if (useAPI) {
                String apiOperator = convertOperatorForAPI(operator);
                new CalculateTask(expression).execute(firstNumber, secondNumber, apiOperator);
            } else {
                calculateLocally(secondNumber, expression);
            }
        }
    }

    private String convertOperatorForAPI(String displayOperator) {
        switch (displayOperator) {
            case "×": return "*";
            case "÷": return "/";
            case "+": return "+";
            case "−": return "-";
            default: return displayOperator;
        }
    }

    private void calculateLocally(double secondNumber, String expression) {
        double result = 0;

        switch (operator) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "−":
                result = firstNumber - secondNumber;
                break;
            case "×":
                result = firstNumber * secondNumber;
                break;
            case "÷":
                if (secondNumber != 0) {
                    result = firstNumber / secondNumber;
                } else {
                    viewManager.sendError(mainLayout, "Cannot divide by zero");
                    return;
                }
                break;
        }

        currentInput = formatResult(result);
        operator = "";
        waitingForOperand = true;
        updateDisplay();

        // Отправляем результат в React Native
        viewManager.sendCalculationResult(mainLayout, expression, currentInput);
    }

    // AsyncTask для работы с API
    private class CalculateTask extends AsyncTask<Object, Void, String> {
        private double firstNum;
        private double secondNum;
        private String operation;
        private String expression;

        CalculateTask(String expression) {
            this.expression = expression;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateDisplay("Calculating...");
        }

        @Override
        protected String doInBackground(Object... params) {
            firstNum = (Double) params[0];
            secondNum = (Double) params[1];
            operation = (String) params[2];

            try {
                JSONObject requestData = new JSONObject();
                requestData.put("a", firstNum);
                requestData.put("b", secondNum);
                requestData.put("operation", operation);

                URL url = new URL(API_BASE_URL + "/calculate");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode >= 200 && responseCode < 300) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        return String.valueOf(jsonResponse.getDouble("result"));
                    } else {
                        return "Error: " + jsonResponse.getString("error");
                    }
                } else {
                    return "API Error: " + responseCode;
                }

            } catch (Exception e) {
                return "Network Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.startsWith("Error:") || result.startsWith("API Error:") || result.startsWith("Network Error:")) {
                useAPI = false;
                calculateLocally(secondNum, expression);
            } else {
                currentInput = formatResult(Double.parseDouble(result));
                operator = "";
                waitingForOperand = true;
                updateDisplay();

                // Отправляем результат в React Native
                viewManager.sendCalculationResult(mainLayout, expression, currentInput);
            }
        }
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }

    public void clear() {
        currentInput = "0";
        operator = "";
        waitingForOperand = false;
        updateDisplay();
    }

    private void toggleSign() {
        if (!currentInput.equals("0")) {
            currentInput = currentInput.startsWith("-") ? 
                currentInput.substring(1) : "-" + currentInput;
            updateDisplay();
        }
    }

    private void backspace() {
        if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            currentInput = "0";
        }
        updateDisplay();
    }

    private void updateDisplay() {
        updateDisplay(currentInput);
    }

    private void updateDisplay(String text) {
        // Находим TextView дисплея в layout
        TextView display = findDisplayView();
        if (display != null) {
            display.setText(text);
        }
    }

    private TextView findDisplayView() {
        // Ищем TextView с тегом "display"
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View child = mainLayout.getChildAt(i);
            if (child instanceof TextView && "display".equals(child.getTag())) {
                return (TextView) child;
            }
        }
        return null;
    }

    // Публичные методы для взаимодействия с React Native
    public String getCurrentValue() {
        return currentInput;
    }

    public void setValue(String value) {
        currentInput = value;
        updateDisplay();
    }

    public void setUseAPI(boolean useAPI) {
        this.useAPI = useAPI;
    }
}
