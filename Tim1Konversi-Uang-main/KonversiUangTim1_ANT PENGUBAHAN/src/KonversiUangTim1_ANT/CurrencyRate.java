
package KonversiUangTim1_ANT;

public class CurrencyRate {
    private String code;
    private String name;
    private double rate;

    public CurrencyRate(String code, String name, double rate) {
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getRate() { return rate; }
}