import axios from 'axios';
import { CalculationResult, CalculationHistory } from '../types/calculator';
import { NativeCalculatorBridge } from './NativeCalculatorBridge';

// Добавляем логирование
const log = (message: string, data?: any) => {
  console.log(`[CalculatorService] ${message}`, data || '');
};

export class CalculatorService {
  private baseURL = 'http://10.0.2.2:8000/api';
  private useNativeModule: boolean = true;

  constructor() {
    log('Constructor: Initializing CalculatorService');
    // Configure axios defaults
    axios.defaults.baseURL = this.baseURL;
    axios.defaults.timeout = 5000;
    log('Constructor: Axios configured with baseURL:', this.baseURL);
    
    // Native module temporarily disabled to prevent crashes
    this.useNativeModule = false;
    log('Constructor: CalculatorService initialized without native module');
  }

  // Local calculation for immediate response
  calculate(a: number, b: number, operation: string): number {
    switch (operation) {
      case '+':
        return a + b;
      case '-':
        return a - b;
      case '*':
        return a * b;
      case '/':
        if (b === 0) {
          throw new Error('Division by zero');
        }
        return a / b;
      default:
        throw new Error('Invalid operation');
    }
  }

  // Async calculation using API
  async calculateAsync(a: number, b: number, operation: string): Promise<number> {
    try {
      const response = await axios.post('/calculate', {
        a,
        b,
        operation,
      });

      if (response.data.success) {
        return response.data.result;
      } else {
        throw new Error(response.data.error || 'Calculation failed');
      }
    } catch (error: any) {
      if (error.response) {
        // Server responded with error status
        throw new Error(error.response.data.error || 'Server error');
      } else if (error.request) {
        // Network error - fallback to local calculation
        console.warn('API unavailable, using local calculation');
        return this.calculate(a, b, operation);
      } else {
        throw new Error('Network error');
      }
    }
  }

  // Get calculation history
  async getHistory(): Promise<any[]> {
    log('getHistory: Starting to fetch history');
    try {
      log('getHistory: Making GET request to /history');
      const response = await axios.get('/history');
      log('getHistory: Received response', response.data);
      return response.data.history || [];
    } catch (error) {
      log('getHistory: Error fetching history', error);
      console.warn('Failed to fetch history:', error);
      return [];
    }
  }

  // Clear calculation history
  async clearHistory(): Promise<void> {
    try {
      await axios.delete('/history');
    } catch (error) {
      console.warn('Failed to clear history:', error);
    }
  }

  // Native module calculation
  async calculateNative(expression: string): Promise<CalculationResult> {
    if (!this.useNativeModule) {
      throw new Error('Native module is not available');
    }

    try {
      // Валидируем выражение
      const validation = nativeCalculatorService.validateExpression(expression);
      if (!validation.isValid) {
        throw new Error(validation.error);
      }

      // Форматируем выражение
      const formattedExpression = nativeCalculatorService.formatExpression(expression);
      
      // Выполняем вычисление через нативный модуль
      const result = await nativeCalculatorService.performCalculation(formattedExpression);
      
      return result;
    } catch (error) {
      console.error('Native calculation error:', error);
      throw error;
    }
  }

  // Get native module info
  async getNativeModuleInfo() {
    if (!this.useNativeModule) {
      return null;
    }

    try {
      return await nativeCalculatorService.getModuleInfo();
    } catch (error) {
      console.error('Failed to get native module info:', error);
      return null;
    }
  }

  // Get native module constants
  getNativeModuleConstants() {
    if (!this.useNativeModule) {
      return null;
    }

    return nativeCalculatorService.getConstants();
  }

  // Check if native module is available
  isNativeModuleAvailable(): boolean {
    return this.useNativeModule;
  }

  // Advanced calculation with fallback
  async calculateAdvanced(expression: string): Promise<number> {
    try {
      // Сначала пробуем нативный модуль
      if (this.useNativeModule) {
        const result = await this.calculateNative(expression);
        return result.result;
      }
    } catch (error) {
      console.warn('Native calculation failed, falling back to local calculation:', error);
    }

    // Fallback к локальному вычислению
    return this.evaluateExpressionLocally(expression);
  }

  // Local expression evaluator (fallback)
  private evaluateExpressionLocally(expression: string): number {
    try {
      // Простая реализация для базовых операций
      // В реальном проекте лучше использовать библиотеку типа mathjs
      const cleanExpression = expression.replace(/\s+/g, '');
      
      // Проверяем на деление на ноль
      if (cleanExpression.includes('/0')) {
        throw new Error('Division by zero');
      }

      // Используем Function для безопасного вычисления
      // Внимание: В продакшене это может быть небезопасно
      const result = Function('"use strict"; return (' + cleanExpression + ')')();
      
      if (typeof result !== 'number' || !isFinite(result)) {
        throw new Error('Invalid calculation result');
      }

      return result;
    } catch (error) {
      throw new Error(`Local calculation failed: ${error.message}`);
    }
  }

  // Get current display value from native calculator
  async getNativeDisplayValue(): Promise<string> {
    // Temporarily disabled to prevent crashes
    return '0';
  }

  // Perform calculation using native module
  async performNativeCalculation(a: number, b: number, operation: string): Promise<number> {
    // Temporarily disabled to prevent crashes
    throw new Error('Native module not available');
  }

  // Show toast message using native module
  showNativeToast(message: string): void {
    // Temporarily disabled to prevent crashes
    console.log('Toast:', message);
  }
}
