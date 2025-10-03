package com.calculator;

import android.os.Bundle;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
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

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.facebook.react.ReactRootView;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.common.LifecycleState;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private TextView display;
    private String currentInput = "0";
    private String operator = "";
    private double firstNumber = 0;
    private boolean waitingForOperand = false;
    private boolean useAPI = true; // Флаг для использования API
    private static final String API_BASE_URL = "http://10.0.2.2:8000/api";
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    private boolean reactNativeInitialized = false;
    private LinearLayout mainLayout; // Сохраняем ссылку на основной layout


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting MainActivity creation");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Super.onCreate() completed");
        
        // Create main layout
        Log.d(TAG, "onCreate: Creating main layout");
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);
        mainLayout.setBackgroundColor(0xFF1a1a1a);
        Log.d(TAG, "onCreate: Main layout created");
        
        // Create title
        TextView title = new TextView(this);
        title.setText("Hybrid Calculator App");
        title.setTextSize(24);
        title.setTextColor(0xFFFFFFFF);
        title.setPadding(0, 0, 0, 30);
        
        // Create display
        display = new TextView(this);
        display.setText(currentInput);
        display.setTextSize(32);
        display.setTextColor(0xFFFFFFFF);
        display.setPadding(20, 20, 20, 20);
        display.setBackgroundColor(0xFF000000);
        display.setGravity(android.view.Gravity.END);
        
        // Create buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Row 1: C, ±, %, ÷
        buttonLayout.addView(createButtonRow(new String[]{"C", "±", "%", "÷"}));
        
        // Row 2: 7, 8, 9, ×
        buttonLayout.addView(createButtonRow(new String[]{"7", "8", "9", "×"}));
        
        // Row 3: 4, 5, 6, −
        buttonLayout.addView(createButtonRow(new String[]{"4", "5", "6", "−"}));
        
        // Row 4: 1, 2, 3, +
        buttonLayout.addView(createButtonRow(new String[]{"1", "2", "3", "+"}));
        
        // Row 5: 0, ., ⌫, =
        buttonLayout.addView(createButtonRow(new String[]{"0", ".", "⌫", "="}));
        
        // Row 6: RN button for React Native
        buttonLayout.addView(createButtonRow(new String[]{"RN"}));
        
        // React Native component will be created later if needed
        Log.d(TAG, "onCreate: React Native component creation deferred");
        mReactRootView = null;
        
        Log.d(TAG, "onCreate: Adding views to layout");
        mainLayout.addView(title);
        mainLayout.addView(display);
        mainLayout.addView(buttonLayout);
        
        // React Native component will be added later when RN button is pressed
        Log.d(TAG, "onCreate: React Native component will be added on demand");
        
        Log.d(TAG, "onCreate: All views added to layout");
        
        Log.d(TAG, "onCreate: Setting content view");
        setContentView(mainLayout);
        Log.d(TAG, "onCreate: MainActivity creation completed successfully");
    }

    private void initializeReactNative() {
        if (reactNativeInitialized) {
            Log.d(TAG, "initializeReactNative: Already initialized, returning");
            return;
        }
        
        Log.d(TAG, "initializeReactNative: Starting React Native initialization");
        
        // Use fallback view due to React Native 0.72.6 bug with Hermes
        // Even when Hermes is disabled, React Native still tries to load it
        Log.d(TAG, "initializeReactNative: Using fallback view due to React Native Hermes bug");
        createFallbackView();
        return;
        
        /* Original React Native initialization - commented out due to Hermes bug in RN 0.72.6
        try {
            // Check if application is ReactApplication
            if (!(getApplication() instanceof ReactApplication)) {
                Log.w(TAG, "initializeReactNative: Application is not ReactApplication");
                // Create fallback view
                createFallbackView();
                return;
            }
            
            Log.d(TAG, "initializeReactNative: Creating ReactRootView");
            mReactRootView = new ReactRootView(this);
            Log.d(TAG, "initializeReactNative: ReactRootView created successfully");
            
            Log.d(TAG, "initializeReactNative: Getting ReactInstanceManager");
            ReactApplication reactApp = (ReactApplication) getApplication();
            Log.d(TAG, "initializeReactNative: ReactApplication cast successful");
            
            ReactNativeHost reactNativeHost = reactApp.getReactNativeHost();
            Log.d(TAG, "initializeReactNative: ReactNativeHost obtained");
            
            Log.d(TAG, "initializeReactNative: Getting ReactInstanceManager from ReactNativeHost");
            mReactInstanceManager = reactNativeHost.getReactInstanceManager();
            Log.d(TAG, "initializeReactNative: ReactInstanceManager obtained successfully");
            
            if (mReactInstanceManager == null) {
                Log.e(TAG, "initializeReactNative: ReactInstanceManager is null");
                throw new RuntimeException("ReactInstanceManager is null");
            }
            
            Log.d(TAG, "initializeReactNative: Starting React Native application");
            // Start React Native component
            mReactRootView.startReactApplication(mReactInstanceManager, "CalculatorFrontend", null);
            Log.d(TAG, "initializeReactNative: React Native application started successfully");
            
            reactNativeInitialized = true;
            Log.d(TAG, "initializeReactNative: React Native initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "initializeReactNative: Error initializing React Native", e);
            Log.e(TAG, "initializeReactNative: Exception details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "React Native недоступен: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Create fallback view instead of crashing
            createFallbackView();
        }
        */
    }
    
    private void createFallbackView() {
        Log.d(TAG, "createFallbackView: Starting fallback view creation");
        try {
            Log.d(TAG, "createFallbackView: Creating ReactRootView for fallback");
            mReactRootView = new ReactRootView(this);
            Log.d(TAG, "createFallbackView: ReactRootView created for fallback");
            
            Log.d(TAG, "createFallbackView: Creating TextView placeholder");
            TextView reactPlaceholder = new TextView(this);
            reactPlaceholder.setText("React Native компонент\n(История вычислений)\n\nИспользуйте нативный калькулятор выше");
            reactPlaceholder.setTextColor(0xFFFFFFFF);
            reactPlaceholder.setTextSize(16);
            reactPlaceholder.setPadding(20, 20, 20, 20);
            reactPlaceholder.setBackgroundColor(0xFF2c2c2e);
            reactPlaceholder.setGravity(android.view.Gravity.CENTER);
            Log.d(TAG, "createFallbackView: TextView placeholder created");
            
            Log.d(TAG, "createFallbackView: Adding TextView to ReactRootView");
            mReactRootView.addView(reactPlaceholder);
            Log.d(TAG, "createFallbackView: TextView added to ReactRootView");
            
            reactNativeInitialized = true;
            Log.d(TAG, "createFallbackView: Fallback view created successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "createFallbackView: Error creating fallback view", e);
            Log.e(TAG, "createFallbackView: Exception details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            mReactRootView = null;
        }
    }

    private void toggleReactNative() {
        Log.d(TAG, "toggleReactNative: Toggling React Native component");
        Log.d(TAG, "toggleReactNative: mReactRootView is null: " + (mReactRootView == null));
        Log.d(TAG, "toggleReactNative: mainLayout is null: " + (mainLayout == null));
        Log.d(TAG, "toggleReactNative: reactNativeInitialized: " + reactNativeInitialized);
        
        try {
            if (mReactRootView == null) {
                Log.d(TAG, "toggleReactNative: Initializing React Native");
                // Initialize React Native
                initializeReactNative();
                Log.d(TAG, "toggleReactNative: After initialization - mReactRootView is null: " + (mReactRootView == null));
                
                if (mReactRootView != null && mainLayout != null) {
                    Log.d(TAG, "toggleReactNative: Adding React Native view to main layout");
                    // Add React Native view to main layout
                    LinearLayout.LayoutParams reactParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 
                        400 // Fixed height for React Native component
                    );
                    reactParams.setMargins(0, 20, 0, 0);
                    mReactRootView.setLayoutParams(reactParams);
                    mainLayout.addView(mReactRootView);
                    Log.d(TAG, "toggleReactNative: React Native view added to main layout successfully");
                    Toast.makeText(this, "React Native компонент активирован", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "toggleReactNative: Cannot add React Native view - mReactRootView: " + (mReactRootView != null) + ", mainLayout: " + (mainLayout != null));
                    Toast.makeText(this, "Не удалось добавить React Native компонент", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "toggleReactNative: Removing React Native view");
                // Remove React Native view
                if (mainLayout != null) {
                    mainLayout.removeView(mReactRootView);
                    mReactRootView = null;
                    reactNativeInitialized = false;
                    Log.d(TAG, "toggleReactNative: React Native view removed from main layout");
                    Toast.makeText(this, "React Native компонент деактивирован", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "toggleReactNative: Error toggling React Native", e);
            Log.e(TAG, "toggleReactNative: Exception details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private LinearLayout createButtonRow(String[] buttons) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        
        for (String buttonText : buttons) {
            Button button = createButton(buttonText);
            row.addView(button);
        }
        
        return row;
    }

    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(20);
        button.setTextColor(0xFFFFFFFF);
        button.setBackgroundColor(0xFF333333);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);
        
        button.setOnClickListener(v -> onButtonClick(text));
        
        return button;
    }

    private void onButtonClick(String buttonText) {
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
            case "RN":
                toggleReactNative();
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

            if (useAPI) {
                // Конвертируем символы для API
                String apiOperator = convertOperatorForAPI(operator);
                // Используем API для вычисления
                new CalculateTask().execute(firstNumber, secondNumber, apiOperator);
            } else {
                // Локальное вычисление
                calculateLocally(secondNumber);
            }
        }
    }

    // Конвертация символов для API
    private String convertOperatorForAPI(String displayOperator) {
        switch (displayOperator) {
            case "×":
                return "*";
            case "÷":
                return "/";
            case "+":
                return "+";
            case "−":
                return "-";
            default:
                return displayOperator;
        }
    }

    private void calculateLocally(double secondNumber) {
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
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }

        currentInput = formatResult(result);
        operator = "";
        waitingForOperand = true;
        updateDisplay();
    }

    // AsyncTask для работы с API
    private class CalculateTask extends AsyncTask<Object, Void, String> {
        private double firstNum;
        private double secondNum;
        private String operation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            display.setText("Calculating...");
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
                Toast.makeText(MainActivity.this, "API недоступен, используем локальные вычисления", Toast.LENGTH_SHORT).show();
                useAPI = false;
                calculateLocally(secondNum);
            } else {
                currentInput = formatResult(Double.parseDouble(result));
                operator = "";
                waitingForOperand = true;
                updateDisplay();
                Toast.makeText(MainActivity.this, "Вычислено через Laravel API", Toast.LENGTH_SHORT).show();
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

    private void clear() {
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
        display.setText(currentInput);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: Back button pressed");
        super.onBackPressed();
    }
}