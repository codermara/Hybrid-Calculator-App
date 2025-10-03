<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\CalculatorController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

// Calculator API routes
Route::prefix('calculator')->group(function () {
    Route::post('/calculate', [CalculatorController::class, 'calculate']);
    Route::get('/history', [CalculatorController::class, 'getHistory']);
    Route::delete('/history', [CalculatorController::class, 'clearHistory']);
});

// Simplified routes for direct access
Route::post('/calculate', [CalculatorController::class, 'calculate']);
Route::get('/history', [CalculatorController::class, 'getHistory']);
Route::delete('/history', [CalculatorController::class, 'clearHistory']);
Route::get('/operations', [CalculatorController::class, 'getSupportedOperations']);
Route::get('/health', [CalculatorController::class, 'health']);
