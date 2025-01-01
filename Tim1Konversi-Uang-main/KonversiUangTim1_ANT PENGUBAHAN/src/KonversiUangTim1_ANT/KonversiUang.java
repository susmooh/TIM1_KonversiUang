/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package KonversiUangTim1_ANT;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pc
 */
public class KonversiUang extends javax.swing.JFrame {
    private final CurrencyService currencyService;
    
    public KonversiUang() {
        initComponents();
        currencyService = new CurrencyService();
    }

    // Method to fetch exchange rates from API
    private Map<String, Double> fetchExchangeRates(String baseCurrency) {
        Map<String, Double> rates = new HashMap<>();
        
        try {
            String apiUrl = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", 
                currencyService.getAPI_KEY(), baseCurrency);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Check for successful connection
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP error code : " + connection.getResponseCode());
            }
            
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Manually parse the JSON-like response
            String responseStr = response.toString();
            
            // Supported currencies
            String[] supportedCurrencies = {
                "IDR", "USD", "EUR", "GBP", "JPY", "CNY", "INR", 
                "RUB", "BRL", "ZAR", "ZMK", "CAD", "NGN", "MXN", "CHF", "AUD"
            };
            
            // Manually extract rates for supported currencies
            for (String currency : supportedCurrencies) {
                String searchStr = "\"" + currency + "\":";
                int currencyIndex = responseStr.indexOf(searchStr);
                if (currencyIndex != -1) {
                    // Extract the rate value
                    int valueStart = currencyIndex + searchStr.length();
                    int valueEnd = responseStr.indexOf(",", valueStart);
                    if (valueEnd == -1) {
                        valueEnd = responseStr.indexOf("}", valueStart);
                    }
                    
                    try {
                        double rate = Double.parseDouble(
                            responseStr.substring(valueStart, valueEnd).trim()
                        );
                        rates.put(currency, rate);
                    } catch (NumberFormatException e) {
                        // Skip if rate can't be parsed
                    }
                }
            }
            
            return rates;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error fetching exchange rates: " + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            return getDefaultRates(); // Fallback to default rates
        }
    }
    
    // Fallback method with default rates
    private Map<String, Double> getDefaultRates() {
        Map<String, Double> conversionRate = new HashMap<>();
        // Base currency is USD (1.0)
        conversionRate.put("USD", 1.0);
        
        // Conversion rates (as of November 2024, approximate values)
        conversionRate.put("IDR", 20160.50);  // Indonesian Rupiah
        conversionRate.put("USD", 1.27);      // US Dollar
        conversionRate.put("EUR", 1.20);      // Euro
        conversionRate.put("JPY", 190.60);    // Japanese Yen
        conversionRate.put("CNY", 9.22);      // Chinese Yuan
        conversionRate.put("INR", 107.50);    // Indian Rupee
        conversionRate.put("RUB", 135.75);    // Russian Ruble
        conversionRate.put("BRL", 7.60);      // Brazilian Real
        conversionRate.put("ZAR", 23.01);     // South African Rand
        conversionRate.put("ZMK", 34.20);     // Zambian Kwacha
        conversionRate.put("CAD", 1.76);      // Canadian Dollar
        conversionRate.put("NGN", 2144.20);   // Nigerian Naira
        conversionRate.put("MXN", 25.35);     // Mexican Peso
        conversionRate.put("CHF", 1.12);      // Swiss Franc
        conversionRate.put("AUD", 1.95);      // Australian Dollar
        
        return conversionRate;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tmblKeluar = new javax.swing.JButton();
        tmblKonversi = new javax.swing.JButton();
        tmblReset = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextJumlahUang = new javax.swing.JTextField();
        jComboBoxMenjadiUang = new javax.swing.JComboBox<>();
        jComboBoxDariUang = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextJumlahTerkonversi = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255), 20));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tmblKeluar.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        tmblKeluar.setText("Keluar");
        tmblKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmblKeluarActionPerformed(evt);
            }
        });
        jPanel1.add(tmblKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 330, 70));

        tmblKonversi.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        tmblKonversi.setText("Konversi");
        tmblKonversi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmblKonversiActionPerformed(evt);
            }
        });
        jPanel1.add(tmblKonversi, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 330, 70));

        tmblReset.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        tmblReset.setText("Reset");
        tmblReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tmblResetActionPerformed(evt);
            }
        });
        jPanel1.add(tmblReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 330, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 650, 1170, 150));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255), 20));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI Symbol", 1, 68)); // NOI18N
        jLabel2.setText("Konversi Mata Uang");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 1170, 150));

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255), 20));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel3.setText("Dari Mata Uang");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel4.setText("Menjadi Mata Uang");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setText("Jumlah Uang");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        jTextJumlahUang.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jPanel3.add(jTextJumlahUang, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 710, 60));

        jComboBoxMenjadiUang.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jComboBoxMenjadiUang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IDR - Indonesia", "USD - Dollar Amerika", "EUR - Euro", "GBP - Inggris", "JPY - Jepang", "CNY - China", "INR - India", "RUB - Rusia", "BRL - Brazil", "ZAR - Afrika Selatan", "ZMK - Zambia", "CAD - Canadian", "NGN - Nigerian", "MXN - Mexcan Peso", "CHF - Swiss Franc", "AUD - Australian" }));
        jPanel3.add(jComboBoxMenjadiUang, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 190, 710, 60));

        jComboBoxDariUang.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jComboBoxDariUang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IDR - Indonesia", "USD - Dollar Amerika", "EUR - Euro", "GBP - Inggris", "JPY - Jepang", "CNY - China", "INR - India", "RUB - Rusia", "BRL - Brazil", "ZAR - Afrika Selatan", "ZMK - Zambia", "CAD - Canadian", "NGN - Nigerian", "MXN - Mexcan Peso", "CHF - Swiss Franc", "AUD - Australian" }));
        jComboBoxDariUang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDariUangActionPerformed(evt);
            }
        });
        jPanel3.add(jComboBoxDariUang, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 120, 710, 60));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 1170, 300));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255), 20));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel5.setText("Jumlah Terkonversi");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        jTextJumlahTerkonversi.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        jPanel4.add(jTextJumlahTerkonversi, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 710, 70));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 490, 1170, 150));

        pack();
    }// </editor-fold>//GEN-END:initComponents
private JFrame frame;
    private void tmblKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tmblKeluarActionPerformed
        // TODO add your handling code here:
        frame = new JFrame("Exit");
        
        if(JOptionPane.showConfirmDialog(frame,"Apakah anda ingin keluar?", "Currency Converter",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
            System.exit(0);
        }
        
    }//GEN-LAST:event_tmblKeluarActionPerformed

    private void tmblKonversiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tmblKonversiActionPerformed
        double amount;
        try {
            amount = Double.parseDouble(jTextJumlahUang.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Masukkan angka yang valid", 
                "Eksklamasi", 
                JOptionPane.ERROR_MESSAGE);
            jTextJumlahUang.setText("");
            jTextJumlahUang.requestFocus();
            return;
        }

        String fromCurrency = jComboBoxDariUang.getSelectedItem().toString().substring(0, 3);
        String toCurrency = jComboBoxMenjadiUang.getSelectedItem().toString().substring(0, 3);

        try {
            double convertAmount = currencyService.convertCurrency(amount, fromCurrency, toCurrency);
            jTextJumlahTerkonversi.setText(String.format("%.2f %s = %.2f %s", 
                amount, fromCurrency, convertAmount, toCurrency));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                "Konversi mata uang tidak valid!", 
                "Kesalahan", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_tmblKonversiActionPerformed

    private void tmblResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tmblResetActionPerformed
        // TODO add your handling code here:
        jTextJumlahUang.setText("");
        jTextJumlahTerkonversi.setText("");
        jComboBoxDariUang.setSelectedIndex(-1);
        jComboBoxMenjadiUang.setSelectedIndex(-1);
    }//GEN-LAST:event_tmblResetActionPerformed

    private void jComboBoxDariUangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDariUangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxDariUangActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KonversiUang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KonversiUang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KonversiUang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KonversiUang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KonversiUang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBoxDariUang;
    private javax.swing.JComboBox<String> jComboBoxMenjadiUang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextJumlahTerkonversi;
    private javax.swing.JTextField jTextJumlahUang;
    private javax.swing.JButton tmblKeluar;
    private javax.swing.JButton tmblKonversi;
    private javax.swing.JButton tmblReset;
    // End of variables declaration//GEN-END:variables
}
