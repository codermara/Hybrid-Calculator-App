<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use App\Models\Calculation;
use App\Services\CalculatorService;
use Illuminate\Support\Facades\Validator;

class CalculatorController extends Controller
{
    protected $calculatorService;

    public function __construct(CalculatorService $calculatorService)
    {
        $this->calculatorService = $calculatorService;
    }

    /**
     * Perform calculation
     */
    public function calculate(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'a' => 'required|numeric',
            'b' => 'required|numeric',
            'operation' => 'required|in:+,-,*,/',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'error' => 'Invalid input parameters',
                'details' => $validator->errors()
            ], 400);
        }

        try {
            $a = $request->input('a');
            $b = $request->input('b');
            $operation = $request->input('operation');

            $result = $this->calculatorService->calculate($a, $b, $operation);

            // Save calculation to history
            Calculation::create([
                'a' => $a,
                'b' => $b,
                'operation' => $operation,
                'result' => $result,
                'ip_address' => $request->ip(),
                'user_agent' => $request->userAgent(),
            ]);

            return response()->json([
                'success' => true,
                'result' => $result,
                'calculation' => [
                    'a' => $a,
                    'b' => $b,
                    'operation' => $operation,
                    'result' => $result
                ]
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'error' => $e->getMessage()
            ], 400);
        }
    }

    /**
     * Get calculation history
     */
    public function getHistory(Request $request): JsonResponse
    {
        $limit = $request->input('limit', 50);
        $limit = min($limit, 100); // Max 100 records

        $history = Calculation::orderBy('created_at', 'desc')
            ->limit($limit)
            ->get()
            ->map(function ($calculation) {
                return [
                    'id' => $calculation->id,
                    'calculation' => $calculation->a . ' ' . $calculation->operation . ' ' . $calculation->b . ' = ' . $calculation->result,
                    'result' => $calculation->result,
                    'timestamp' => $calculation->created_at->toISOString(),
                ];
            });

        return response()->json([
            'success' => true,
            'history' => $history,
            'count' => $history->count()
        ]);
    }

    /**
     * Clear calculation history
     */
    public function clearHistory(): JsonResponse
    {
        Calculation::truncate();

        return response()->json([
            'success' => true,
            'message' => 'History cleared successfully'
        ]);
    }

    /**
     * Get supported operations
     */
    public function getSupportedOperations(): JsonResponse
    {
        return response()->json([
            'success' => true,
            'operations' => $this->calculatorService->getSupportedOperations(),
            'description' => [
                '+' => 'Addition',
                '-' => 'Subtraction',
                '*' => 'Multiplication',
                '/' => 'Division'
            ]
        ]);
    }

    /**
     * Health check endpoint
     */
    public function health(): JsonResponse
    {
        return response()->json([
            'success' => true,
            'status' => 'healthy',
            'timestamp' => now()->toISOString(),
            'version' => '1.0.0'
        ]);
    }
}
