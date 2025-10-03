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

// Добавляем логирование
const log = (message: string, data?: any) => {
  console.log(`[App] ${message}`, data || '');
};

const App: React.FC = () => {
  log('App component initializing');
  
  const [history, setHistory] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
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
      const result = await calculatorService.getHistory();
      log('loadHistory: Received result from service', result);
      
      if (result.success && result.history) {
        log('loadHistory: Setting history from result', result.history);
        setHistory(result.history);
      } else {
        log('loadHistory: No valid history found, setting empty array');
        setHistory([]);
      }
      
      // Show toast when history is loaded
      log(`loadHistory: Loaded ${result.history?.length || 0} calculations`);
    } catch (error) {
      log('loadHistory: Error loading history', error);
      console.error('Error loading history:', error);
      // Set empty history to prevent crashes
      setHistory([]);
    }
  };

  const clearHistory = async () => {
    try {
      const result = await calculatorService.clearHistory();
      if (result.success) {
        setHistory([]);
        Alert.alert('Success', 'History cleared successfully');
      } else {
        Alert.alert('Error', 'Failed to clear history');
      }
    } catch (error) {
      console.error('Error clearing history:', error);
      Alert.alert('Error', 'Failed to clear history');
      // Still clear local history to prevent UI issues
      setHistory([]);
    }
  };

  const renderHistory = () => (
    <ScrollView style={styles.content}>
      <View style={styles.header}>
        <Text style={styles.title}>Calculation History</Text>
        {history.length > 0 && (
          <TouchableOpacity style={styles.clearButton} onPress={clearHistory}>
            <Text style={styles.clearButtonText}>Clear All</Text>
          </TouchableOpacity>
        )}
      </View>
      
      {isLoading ? (
        <View style={styles.emptyState}>
          <Text style={styles.emptyText}>Loading history...</Text>
        </View>
      ) : history.length === 0 ? (
        <View style={styles.emptyState}>
          <Text style={styles.emptyText}>No calculations yet</Text>
          <Text style={styles.emptySubtext}>Use the native calculator above to see history here</Text>
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
    </ScrollView>
  );

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
    marginBottom: 16,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
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