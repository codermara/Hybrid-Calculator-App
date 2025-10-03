export interface CalculationResult {
  success: boolean;
  result?: number;
  error?: string;
  timestamp?: string;
}

export interface CalculationHistory {
  id: string;
  expression: string;
  result: number;
  timestamp: string;
  operation: string;
}

export interface CalculatorState {
  display: string;
  previousValue: number | null;
  operation: string | null;
  waitingForOperand: boolean;
  history: CalculationHistory[];
}

export type Operation = '+' | '-' | '*' | '/';

export interface CalculatorButton {
  label: string;
  value: string;
  type: 'number' | 'operation' | 'function';
  style?: 'primary' | 'secondary' | 'accent';
}
