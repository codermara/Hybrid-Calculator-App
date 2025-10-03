<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Calculation extends Model
{
    use HasFactory;

    protected $fillable = [
        'a',
        'b',
        'operation',
        'result',
        'ip_address',
        'user_agent',
    ];

    protected $casts = [
        'a' => 'float',
        'b' => 'float',
        'result' => 'float',
    ];

    /**
     * Get the calculation as a formatted string
     */
    public function getCalculationAttribute(): string
    {
        return $this->a . ' ' . $this->operation . ' ' . $this->b . ' = ' . $this->result;
    }
}
