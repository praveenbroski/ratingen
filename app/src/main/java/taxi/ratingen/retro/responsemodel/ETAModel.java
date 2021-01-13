package taxi.ratingen.retro.responsemodel;

import java.io.Serializable;

public class ETAModel implements Serializable {

    public String name;
    public String currency_name;
    public String currency_code;
    public String currency_symbol;
    public String country;
    public int active;
    public String company_key;
    public int unit;
    public double distance;
    public int time;
    public double base_distance;
    public double base_price;
    public double price_per_distance;
    public double price_per_time;
    public double distance_price;
    public double time_price;
    public double ride_fare;
    public double tax_amount;
    public double tax;
    public double total;
    public double approximate_value;
    public double min_amount;
    public double max_amount;
    public String unit_in_words_without_lang;
    public String unit_in_words;
    public String driver_arival_estimation;
    public int extra_person_charge;
    public double extra_charge_per_person;

}
