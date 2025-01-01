package KonversiUangTim1_ANT;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CurrencyService {
    private static String API_KEY;
    private Map<String, Double> rateCache = new HashMap<>();
    private static final String CONFIG_FILE = "src/resources/config.properties";
    private static final String RATES_FILE = "src/resources/default_rates.json";

    public CurrencyService() {
        loadConfiguration();
        updateRatesOnStartup();
    }

    private void loadConfiguration() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            prop.load(input);
            API_KEY = prop.getProperty("api.key");
        } catch (IOException ex) {
            System.err.println("Could not load configuration: " + ex.getMessage());
            // Fallback to default API key if config file is not available
            API_KEY = "7646c1f680793787184f9f59";
        }
    }

    private void updateRatesOnStartup() {
        try {
            // Try to get fresh rates using USD as base currency
            Map<String, Double> freshRates = fetchExchangeRates("USD");
            if (!freshRates.isEmpty()) {
                // If successful, update cache and save to file
                rateCache = freshRates;
                saveRatesToFile(freshRates);
                System.out.println("Exchange rates updated successfully from API");
            } else {
                // If API call returns empty, load from file
                loadDefaultRates();
                System.out.println("Using cached exchange rates from file");
            }
        } catch (Exception e) {
            // If any error occurs, fall back to file/default rates
            loadDefaultRates();
            System.err.println("Failed to update rates from API: " + e.getMessage());
        }
    }

    private void loadDefaultRates() {
        rateCache = loadRatesFromFile();
        if (rateCache.isEmpty()) {
            rateCache = createDefaultRates();
            saveRatesToFile(rateCache);
        }
    }

    public static String getAPI_KEY() {
        return API_KEY;
    }

    // Save current rates to file
    private void saveRatesToFile(Map<String, Double> rates) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RATES_FILE))) {
            out.println("{");
            rates.forEach((currency, rate) -> 
                out.printf("  \"%s\": %.2f,%n", currency, rate)
            );
            out.println("}");
        } catch (IOException e) {
            System.err.println("Error saving rates: " + e.getMessage());
        }
    }

    // Load rates from file
    private Map<String, Double> loadRatesFromFile() {
        Map<String, Double> rates = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RATES_FILE))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            parseRatesFromResponse(content.toString(), rates);
        } catch (IOException e) {
            System.err.println("Error loading rates: " + e.getMessage());
        }
        return rates;
    }

    public Map<String, Double> fetchExchangeRates(String baseCurrency) {
        Map<String, Double> rates = new HashMap<>();
        
        try {
            String apiUrl = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", API_KEY, baseCurrency);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP error code : " + connection.getResponseCode());
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            parseRatesFromResponse(response.toString(), rates);
            if (!rates.isEmpty()) {
                saveRatesToFile(rates); // Save the latest rates only if successful
            }
            return rates;
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            return new HashMap<>(); // Return empty map to trigger fallback
        }
    }

    private void parseRatesFromResponse(String response, Map<String, Double> rates) {
        String[] supportedCurrencies = {
            "IDR", "USD", "EUR", "GBP", "JPY", "CNY", "INR", 
            "RUB", "BRL", "ZAR", "ZMK", "CAD", "NGN", "MXN", "CHF", "AUD"
        };
        
        for (String currency : supportedCurrencies) {
            String searchStr = "\"" + currency + "\":";
            int currencyIndex = response.indexOf(searchStr);
            if (currencyIndex != -1) {
                int valueStart = currencyIndex + searchStr.length();
                int valueEnd = response.indexOf(",", valueStart);
                if (valueEnd == -1) {
                    valueEnd = response.indexOf("}", valueStart);
                }
                
                try {
                    double rate = Double.parseDouble(
                        response.substring(valueStart, valueEnd).trim()
                    );
                    rates.put(currency, rate);
                } catch (NumberFormatException e) {
                    // Skip if rate can't be parsed
                }
            }
        }
    }

    public Map<String, Double> getDefaultRates() {
        Map<String, Double> rates = loadRatesFromFile();
        if (rates.isEmpty()) {
            rates = createDefaultRates();
            saveRatesToFile(rates);
        }
        return rates;
    }

    private Map<String, Double> createDefaultRates() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("IDR", 20160.50);
        rates.put("EUR", 1.20);
        rates.put("GBP", 1.15);
        rates.put("JPY", 190.60);
        // ... add other default rates ...
        return rates;
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        // Always try to fetch fresh rates from API first
        try {
            Map<String, Double> currentRates = fetchExchangeRates(fromCurrency);
            if (!currentRates.isEmpty()) {
                rateCache = currentRates;
            } else {
                // If API fetch returns empty, fall back to cached rates
                if (rateCache.isEmpty()) {
                    rateCache = loadRatesFromFile();
                }
            }
        } catch (Exception e) {
            // If API fails, use cached rates
            if (rateCache.isEmpty()) {
                rateCache = loadRatesFromFile();
            }
        }
        
        if (!rateCache.containsKey(fromCurrency) || !rateCache.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Invalid Currency");
        }
        
        double fromRate = rateCache.get(fromCurrency);
        double toRate = rateCache.get(toCurrency);
        
        return amount * (toRate / fromRate);
    }
}