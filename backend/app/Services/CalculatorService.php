<?php

namespace App\Services;

class CalculatorService
{
    /**
     * Perform mathematical calculation
     */
    public function calculate(float $a, float $b, string $operation): float
    {
        switch ($operation) {
            case '+':
                return $a + $b;
            
            case '-':
                return $a - $b;
            
            case '*':
                return $a * $b;
            
            case '/':
                if ($b == 0) {
                    throw new \InvalidArgumentException('Division by zero is not allowed');
                }
                return $a / $b;
            
            default:
                throw new \InvalidArgumentException('Invalid operation: ' . $operation);
        }
    }

    /**
     * Validate operation
     */
    public function isValidOperation(string $operation): bool
    {
        return in_array($operation, ['+', '-', '*', '/']);
    }

    /**
     * Get supported operations
     */
    public function getSupportedOperations(): array
    {
        return ['+', '-', '*', '/'];
    }
}
