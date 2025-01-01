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
    public Map<String, Double> loadRatesFromFile() {
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
        "AED, AFN, ALL, AMD, ANG, AOA, ARS, AUD, AWG, AZN, BAM, BBD, BDT, BGN, BHD, BIF, BMD, BND, BOB, BRL, BSD, "
        + "BTN, BWP, BYN, BZD, CAD, CDF, CHF, CLP, CNY, COP, CRC, CUP, CVE, CZK, DJF, DKK, DOP, DZD, EGP, ERN, ETB, EUR, FJD, FKP, "
        + "FOK, GBP, GEL, GGP, GHS, GIP, GMD, GNF, GTQ, GYD, HKD, HNL, HRK, HTG, HUF, IDR, ILS, IMP, INR, IQD, IRR, ISK, JEP, JMD, "
        + "JOD, JPY, KES, KGS, KHR, KID, KMF, KRW, KWD, KYD, KZT, LAK, LBP, LKR, LRD, LSL, LYD, MAD, MDL, MGA, MKD, MMK, MNT, MOP, "
        + "MRU, MUR, MVR, MWK, MXN, MYR, MZN, NAD, NGN, NIO, NOK, NPR, NZD, OMR, PAB, PEN, PGK, PHP, PKR, PLN, PYG, QAR, RON, RSD, "
        + "RUB, RWF, SAR, SBD, SCR, SDG, SEK, SGD, SHP, SLE, SOS, SRD, SSP, STN, SYP, SZL, THB, TJS, TMT, TND, TOP, TRY, TTD, TVD, "
        + "TWD, TZS, UAH, UGX, USD, UYU, UZS, VES, VND, VUV, WST, XAF, XCD, XDR, XOF, XPF, YER, ZAR, ZMW, ZWL"
    };
    
    for (int i = 0; i < supportedCurrencies.length; i++) {
        String currency = supportedCurrencies[i];
        String searchStr = "\"" + currency + "\":";
        int currencyIndex = response.indexOf(searchStr);
        if (currencyIndex != -1) {
            int valueStart = currencyIndex + searchStr.length();
            int valueEnd;
            
            // Untuk mata uang terakhir, cari akhir dari kurung kurawal
            if (i == supportedCurrencies.length - 1) {
                valueEnd = response.indexOf("}", valueStart);
            } else {
                valueEnd = response.indexOf(",", valueStart);
            }
            
            try {
                double rate = Double.parseDouble(
                    response.substring(valueStart, valueEnd).trim()
                );
                rates.put(currency, rate);
            } catch (NumberFormatException e) {
                // Lewati jika rate tidak dapat diurai
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
    
    // Daftar kode mata uang
    String[] currencyCodes = {
        "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", 
        "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", 
        "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", 
        "COP", "CRC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", 
        "ERN", "ETB", "EUR", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", 
        "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", 
        "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", 
        "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW", "KWD", "KYD", "KZT", 
        "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", 
        "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", 
        "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", 
        "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", 
        "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", 
        "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", 
        "TVD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", 
        "VUV", "WST", "XAF", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"
    };

    // Tambahkan default rates untuk mata uang tertentu
    rates.put("USD", 1.0);
    rates.put("IDR", 15925.78);
    rates.put("EUR", 0.95);
    rates.put("GBP", 0.79);
    rates.put("JPY", 149.72);
    rates.put("CNY", 7.28);
    rates.put("AUD", 1.54);
    rates.put("CAD", 1.40);
    rates.put("CHF", 0.89);
    rates.put("MXN", 20.42);
    rates.put("NGN", 1674.42);
    rates.put("INR", 84.75);
    rates.put("BRL", 6.02);
    rates.put("RUB", 106.29);
    rates.put("ZAR", 18.15);

    // Untuk mata uang lain yang tidak memiliki rate khusus, berikan rate default
    double defaultRate = 1.0;
    for (String currencyCode : currencyCodes) {
        if (!rates.containsKey(currencyCode)) {
            rates.put(currencyCode, defaultRate);
        }
    }

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