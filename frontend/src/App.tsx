import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Alert,
} from 'react-native';
import { CalculatorService } from './services/CalculatorService';
import NativeCalculatorComponent from './components/NativeCalculator';

// Добавляем логирование
const log = (message: string, data?: any) => {
  console.log(`[App] ${message}`, data || '');
};

const App: React.FC = () => {
  log('App component initializing');
  
  const [history, setHistory] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showNativeCalculator, setShowNativeCalculator] = useState(false);
  
  log('Creating CalculatorService instance');
  const calculatorService = new CalculatorService();

  useEffect(() => {
    log('useEffect: App initialization started');
    const initializeApp = async () => {
      try {
        log('initializeApp: Starting app initialization');
        setIsLoading(true);
        log('initializeApp: Loading history');
        await loadHistory();
        log('initializeApp: History loaded successfully');
      } catch (error) {
        log('initializeApp: Error during initialization', error);
        console.error('Failed to initialize app:', error);
        // Set empty history to prevent crashes
        setHistory([]);
        Alert.alert('Error', 'Failed to load history');
      } finally {
        log('initializeApp: Setting loading to false');
        setIsLoading(false);
        log('initializeApp: App initialization completed');
      }
    };
    
    initializeApp();
  }, []);

  const loadHistory = async () => {
    log('loadHistory: Starting to load history');
    try {
      log('loadHistory: Calling calculatorService.getHistory()');
      const historyArray = await calculatorService.getHistory();
      log('loadHistory: Received history array from service', historyArray);
      
      if (Array.isArray(historyArray)) {
        log('loadHistory: Setting history from array', historyArray);
        setHistory(historyArray);
        log(`loadHistory: Loaded ${historyArray.length} calculations`);
      } else {
        log('loadHistory: Invalid history format, setting empty array');
        setHistory([]);
      }
    } catch (error) {
      log('loadHistory: Error loading history', error);
      console.error('Error loading history:', error);
      console.error('Error details:', (error as Error).message, (error as Error).stack);
      // Set empty history to prevent crashes
      setHistory([]);
    }
  };

  const clearHistory = async () => {
    try {
      await calculatorService.clearHistory();
      setHistory([]);
      Alert.alert('Success', 'History cleared successfully');
      log('History cleared successfully');
    } catch (error) {
      console.error('Error clearing history:', error);
      Alert.alert('Error', 'Failed to clear history');
      // Still clear local history to prevent UI issues
      setHistory([]);
    }
  };

  const handleCalculationResult = (result: any) => {
    log('Calculation result received:', result);
    
    // Добавляем результат в историю
    const newHistoryItem = {
      expression: result.expression || 'Unknown expression',
      result: result.result || 'Unknown result',
      timestamp: result.timestamp || new Date().toISOString(),
    };
    
    log('Adding to history:', newHistoryItem);
    setHistory(prev => {
      const newHistory = [newHistoryItem, ...prev];
      log('Updated history length:', newHistory.length);
      return newHistory;
    });
  };

  const handleCalculatorError = (error: string) => {
    log('Calculator error:', error);
    Alert.alert('Calculator Error', error);
  };

  const renderHistory = () => {
    log('Rendering history. Length:', history.length);
    log('isLoading:', isLoading);
    log('showNativeCalculator:', showNativeCalculator);
    log('History array:', history);
    
    return (
      <ScrollView style={styles.content}>
        <View style={styles.header}>
          <Text style={styles.title}>Calculation History</Text>
          <View style={styles.headerButtons}>
            <TouchableOpacity 
              style={styles.toggleButton} 
              onPress={() => setShowNativeCalculator(!showNativeCalculator)}
            >
              <Text style={styles.toggleButtonText}>
                {showNativeCalculator ? 'Hide' : 'Show'} Native Calculator
              </Text>
            </TouchableOpacity>
            {history.length > 0 && (
              <TouchableOpacity style={styles.clearButton} onPress={clearHistory}>
                <Text style={styles.clearButtonText}>Clear All</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>
        
        {showNativeCalculator && (
          <NativeCalculatorComponent
            onCalculationResult={handleCalculationResult}
            onError={handleCalculatorError}
          />
        )}
        
        
        {/* История всегда видна */}
        <View style={styles.historySection}>
          <Text style={styles.sectionTitle}>Calculation History ({history.length})</Text>
          
          {isLoading ? (
            <View style={styles.emptyState}>
              <Text style={styles.emptyText}>Loading history...</Text>
            </View>
          ) : history.length === 0 ? (
            <View style={styles.emptyState}>
              <Text style={styles.emptyText}>No calculations yet</Text>
              <Text style={styles.emptySubtext}>
                {showNativeCalculator 
                  ? 'Use the native calculator above to see history here'
                  : 'Click "Show Native Calculator" to start calculating'
                }
              </Text>
            </View>
          ) : (
            history.map((item, index) => (
              <View key={index} style={styles.historyItem}>
                <Text style={styles.expression}>{item.expression}</Text>
                <Text style={styles.result}>= {item.result}</Text>
                <Text style={styles.timestamp}>
                  {new Date(item.timestamp).toLocaleString()}
                </Text>
              </View>
            ))
          )}
        </View>
      </ScrollView>
    );
  };

  log('App: Rendering component');
  
  return (
    <View style={styles.container}>
      {renderHistory()}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
  content: {
    flex: 1,
    padding: 16,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 8,
  },
  historySection: {
    marginTop: 20,
    paddingTop: 16,
    borderTopWidth: 1,
    borderTopColor: '#38383A',
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 12,
  },
  header: {
    marginBottom: 16,
  },
  headerButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
  },
  toggleButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
  },
  toggleButtonText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '600',
  },
  clearButton: {
    backgroundColor: '#FF3B30',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
  },
  clearButtonText: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '600',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 40,
  },
  emptyText: {
    fontSize: 16,
    color: '#8e8e93',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#6d6d70',
    textAlign: 'center',
  },
  historyItem: {
    backgroundColor: '#2c2c2e',
    padding: 12,
    borderRadius: 8,
    marginBottom: 8,
  },
  expression: {
    fontSize: 16,
    color: '#ffffff',
    marginBottom: 4,
  },
  result: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#007AFF',
    marginBottom: 4,
  },
  timestamp: {
    fontSize: 12,
    color: '#8e8e93',
  },
});

export default App;