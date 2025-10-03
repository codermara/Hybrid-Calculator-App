package com.calculator;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;
import java.util.HashMap;

public class NativeCalculatorViewManager extends SimpleViewManager<LinearLayout> {
    private static final String TAG = "NativeCalculatorViewManager";
    private static final String REACT_CLASS = "NativeCalculator";
    
    // Команды для взаимодействия с RN
    private static final int COMMAND_CLEAR = 1;
    private static final int COMMAND_SET_VALUE = 2;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LinearLayout createViewInstance(ThemedReactContext context) {
        Log.d(TAG, "Creating native calculator view instance");
        return createCalculatorLayout(context);
    }

    private LinearLayout createCalculatorLayout(Context context) {
        // Создаем основной layout как в MainActivity
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);
        mainLayout.setBackgroundColor(0xFF1a1a1a);

        // Создаем заголовок
        TextView title = new TextView(context);
        title.setText("Native Calculator in RN");
        title.setTextSize(20);
        title.setTextColor(0xFFFFFFFF);
        title.setPadding(0, 0, 0, 20);

        // Создаем дисплей
        TextView display = new TextView(context);
        display.setText("0");
        display.setTextSize(28);
        display.setTextColor(0xFFFFFFFF);
        display.setPadding(20, 20, 20, 20);
        display.setBackgroundColor(0xFF000000);
        display.setGravity(android.view.Gravity.END);
        display.setTag("display"); // Для поиска в логике

        // Создаем кнопки
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);

        // Добавляем ряды кнопок
        buttonLayout.addView(createButtonRow(context, new String[]{"C", "±", "%", "÷"}));
        buttonLayout.addView(createButtonRow(context, new String[]{"7", "8", "9", "×"}));
        buttonLayout.addView(createButtonRow(context, new String[]{"4", "5", "6", "−"}));
        buttonLayout.addView(createButtonRow(context, new String[]{"1", "2", "3", "+"}));
        buttonLayout.addView(createButtonRow(context, new String[]{"0", ".", "⌫", "="}));

        // Добавляем все в основной layout
        mainLayout.addView(title);
        mainLayout.addView(display);
        mainLayout.addView(buttonLayout);

        // Создаем и привязываем логику калькулятора
        CalculatorLogic calculatorLogic = new CalculatorLogic(context, mainLayout, this);
        mainLayout.setTag(calculatorLogic); // Сохраняем логику в layout

        return mainLayout;
    }

    private LinearLayout createButtonRow(Context context, String[] buttons) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);

        for (String buttonText : buttons) {
            Button button = createButton(context, buttonText);
            row.addView(button);
        }

        return row;
    }

    private Button createButton(Context context, String text) {
        Button button = new Button(context);
        button.setText(text);
        button.setTextSize(18);
        button.setTextColor(0xFFFFFFFF);
        button.setBackgroundColor(0xFF333333);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);

        // Привязываем обработчик клика
        button.setOnClickListener(v -> {
            CalculatorLogic logic = (CalculatorLogic) ((LinearLayout) v.getParent().getParent().getParent()).getTag();
            if (logic != null) {
                logic.onButtonClick(text);
            }
        });

        return button;
    }

    // Методы для взаимодействия с React Native
    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> commands = new HashMap<>();
        commands.put("clear", COMMAND_CLEAR);
        commands.put("setValue", COMMAND_SET_VALUE);
        return commands;
    }

    @Override
    public void receiveCommand(LinearLayout view, int commandId, com.facebook.react.bridge.ReadableArray args) {
        CalculatorLogic logic = (CalculatorLogic) view.getTag();
        if (logic == null) return;

        switch (commandId) {
            case COMMAND_CLEAR:
                logic.clear();
                break;
            case COMMAND_SET_VALUE:
                if (args.size() > 0) {
                    String value = args.getString(0);
                    logic.setValue(value);
                }
                break;
        }
    }

    // Пропсы для React Native
    @ReactProp(name = "initialValue")
    public void setInitialValue(LinearLayout view, String value) {
        CalculatorLogic logic = (CalculatorLogic) view.getTag();
        if (logic != null) {
            logic.setValue(value);
        }
    }

    // Метод для отправки событий в React Native
    public void sendCalculationResult(LinearLayout view, String expression, String result) {
        WritableMap event = Arguments.createMap();
        event.putString("expression", expression);
        event.putString("result", result);
        event.putString("timestamp", String.valueOf(System.currentTimeMillis()));

        ReactContext reactContext = (ReactContext) view.getContext();
        reactContext
            .getJSModule(RCTEventEmitter.class)
            .receiveEvent(view.getId(), "onCalculationResult", event);
    }

    public void sendError(LinearLayout view, String error) {
        WritableMap event = Arguments.createMap();
        event.putString("error", error);

        ReactContext reactContext = (ReactContext) view.getContext();
        reactContext
            .getJSModule(RCTEventEmitter.class)
            .receiveEvent(view.getId(), "onError", event);
    }
}
