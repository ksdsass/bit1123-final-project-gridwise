package service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CurrencyConverter - lets the user read the simulated generation cost
 * in different currencies.
 *
 * All costs inside the simulation are calculated in Malaysian Ringgit
 * (RM), because the tariffs used by the energy sources are Malaysian.
 * This class converts that base amount for display only, so switching
 * currency never changes the simulation itself.
 *
 * NOTE: the rates are fixed illustrative values (the application works
 * offline and does not call any exchange-rate service).
 */
public class CurrencyConverter {

    /** One supported currency: its symbol and how many units 1 RM buys. */
    public static class Currency {

        private String code;
        private String symbol;
        private double unitsPerRM;

        public Currency(String code, String symbol, double unitsPerRM) {
            this.code = code;
            this.symbol = symbol;
            this.unitsPerRM = unitsPerRM;
        }

        public String getCode()       { return code; }
        public String getSymbol()     { return symbol; }
        public double getUnitsPerRM() { return unitsPerRM; }
    }

    // LinkedHashMap keeps the dropdown in a stable, sensible order
    private Map<String, Currency> currencies = new LinkedHashMap<>();
    private Currency active;

    public CurrencyConverter() {
        add(new Currency("MYR", "RM", 1.00));
        add(new Currency("USD", "$", 0.21));
        add(new Currency("EUR", "€", 0.20));
        add(new Currency("GBP", "£", 0.17));
        add(new Currency("SGD", "S$", 0.29));
        add(new Currency("AUD", "A$", 0.33));
        add(new Currency("CNY", "¥", 1.55));
        add(new Currency("JPY", "¥", 33.00));
        add(new Currency("INR", "₹", 18.50));
        active = currencies.get("MYR");
    }

    private void add(Currency c) {
        currencies.put(c.getCode(), c);
    }

    public String[] getCodes() {
        return currencies.keySet().toArray(new String[0]);
    }

    public Currency getActive() {
        return active;
    }

    public String getActiveCode() {
        return active.getCode();
    }

    /** Switches the display currency; unknown codes are ignored. */
    public void setActive(String code) {
        Currency found = currencies.get(code);
        if (found != null) {
            active = found;
        }
    }

    /** Converts an amount in RM into the active currency. */
    public double convert(double amountRM) {
        return amountRM * active.getUnitsPerRM();
    }

    /** Formats an amount given in RM, e.g. "$ 76,742" or "RM 365,441". */
    public String format(double amountRM) {
        return String.format("%s %,.0f", active.getSymbol(), convert(amountRM));
    }

    /** Formats with two decimals, used in the exported text report. */
    public String formatExact(double amountRM) {
        return String.format("%s %,.2f", active.getSymbol(), convert(amountRM));
    }
}
