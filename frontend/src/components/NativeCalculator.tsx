import React, { useRef, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  NativeModules,
  NativeEventEmitter,
  Alert,
  requireNativeComponent,
} from 'react-native';

// Импорт нативного компонента
const NativeCalculator = requireNativeComponent('NativeCalculator');

interface CalculationResult {
  expression: string;
  result: string;
  timestamp: string;
}

interface NativeCalculatorProps {
  style?: any;
  initialValue?: string;
  onCalculationResult?: (result: CalculationResult) => void;
  onError?: (error: string) => void;
}

const NativeCalculatorComponent: React.FC<NativeCalculatorProps> = ({
  style,
  initialValue = "0",
  onCalculationResult,
  onError,
}) => {
  const nativeCalculatorRef = useRef<any>(null);
  const eventEmitter = useRef<NativeEventEmitter | null>(null);

  useEffect(() => {
    // Создаем event emitter для прослушивания событий от нативного компонента
    eventEmitter.current = new NativeEventEmitter(NativeModules.CalculatorBridge || {});

    // Слушаем события результатов вычислений
    const calculationSubscription = eventEmitter.current.addListener(
      'onCalculationResult',
      (event: CalculationResult) => {
        console.log('Calculation result received:', event);
        if (onCalculationResult) {
          onCalculationResult(event);
        }
      }
    );

    // Слушаем события ошибок
    const errorSubscription = eventEmitter.current.addListener(
      'onError',
      (event: { error: string }) => {
        console.log('Error received:', event.error);
        if (onError) {
          onError(event.error);
        } else {
          Alert.alert('Calculator Error', event.error);
        }
      }
    );

    // Очистка подписок при размонтировании
    return () => {
      calculationSubscription.remove();
      errorSubscription.remove();
    };
  }, [onCalculationResult, onError]);

  // Методы для управления нативным компонентом
  const clearCalculator = () => {
    if (nativeCalculatorRef.current) {
      // NativeModules.NativeCalculatorManager.clear(nativeCalculatorRef.current);
    }
  };

  const setValue = (value: string) => {
    if (nativeCalculatorRef.current) {
      // NativeModules.NativeCalculatorManager.setValue(nativeCalculatorRef.current, value);
    }
  };

  return (
    <View style={[styles.container, style]}>
      <Text style={styles.title}>Native Calculator Component</Text>
      
      {/* Нативный калькулятор */}
      <NativeCalculator
        ref={nativeCalculatorRef}
        style={styles.calculator}
        initialValue={initialValue}
        onCalculationResult={(event: any) => {
          console.log('Direct calculation result:', event.nativeEvent);
          if (onCalculationResult) {
            onCalculationResult(event.nativeEvent);
          }
        }}
        onError={(event: any) => {
          console.log('Direct error:', event.nativeEvent);
          if (onError) {
            onError(event.nativeEvent.error);
          } else {
            Alert.alert('Calculator Error', event.nativeEvent.error);
          }
        }}
      />
      
      {/* Кнопки управления */}
      <View style={styles.controls}>
        <Text style={styles.controlButton} onPress={clearCalculator}>
          Clear Calculator
        </Text>
        <Text style={styles.controlButton} onPress={() => setValue("0")}>
          Reset to 0
        </Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#1a1a1a',
    borderRadius: 8,
    padding: 16,
    margin: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#ffffff',
    textAlign: 'center',
    marginBottom: 16,
  },
  calculator: {
    height: 400,
    width: '100%',
    backgroundColor: '#2c2c2e',
    borderRadius: 8,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 16,
  },
  controlButton: {
    color: '#007AFF',
    fontSize: 16,
    fontWeight: '600',
    padding: 8,
  },
});

export default NativeCalculatorComponent;
