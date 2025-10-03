package com.calculator;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.widget.Toast;

public class CalculatorBridgeModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    CalculatorBridgeModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "CalculatorBridge";
    }

    @ReactMethod
    public void showToast(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    public void getNativeDisplayValue(Promise promise) {
        try {
            // This would get the current display value from MainActivity
            // For now, we'll return a placeholder
            WritableMap result = Arguments.createMap();
            result.putString("value", "0");
            result.putBoolean("success", true);
            promise.resolve(result);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void performNativeCalculation(double a, double b, String operation, Promise promise) {
        try {
            double result = 0;
            switch (operation) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b != 0) {
                        result = a / b;
                    } else {
                        promise.reject("ERROR", "Division by zero");
                        return;
                    }
                    break;
                default:
                    promise.reject("ERROR", "Invalid operation");
                    return;
            }

            WritableMap resultMap = Arguments.createMap();
            resultMap.putDouble("result", result);
            resultMap.putBoolean("success", true);
            promise.resolve(resultMap);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    // Method to send events to React Native
    public static void sendEvent(String eventName, WritableMap params) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }
}
