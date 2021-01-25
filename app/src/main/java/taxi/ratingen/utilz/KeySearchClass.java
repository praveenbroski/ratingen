package taxi.ratingen.utilz;

import java.util.List;
import java.util.Map;

class KeySearchClass {

    private static String searchValue;

    static String KeySearch(Map.Entry<String, List<String>> search) {
        if (search.getKey() != null)
            if (search.getKey().equalsIgnoreCase("mobile"))
                searchValue = search.getValue().get(0);
            else if (search.getKey().equalsIgnoreCase("email"))
                searchValue = search.getValue().get(0);
            else if (search.getKey().equalsIgnoreCase("terms_condition"))
                searchValue = search.getValue().get(0);
            else if (search.getKey().equalsIgnoreCase("uuid"))
                searchValue = search.getValue().get(0);
            else if (search.getKey().equalsIgnoreCase("otp"))
                searchValue = search.getValue().get(0);
            else if (search.getKey().equalsIgnoreCase("cart_items"))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase("coupon_code"))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase("order_id"))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.name))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.request_id))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.rating))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.comment))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.complaint_title_id))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.description))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.ride_type))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.vehicle_type))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.is_later))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.PICK_LAT))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.PICK_LNG))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.PICK_ADDRESS))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.promo_booked_id))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.DROP_LAT))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.DROP_LNG))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.DROP_ADDRESS))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.payment_opt))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.no_of_seats))
                searchValue = search.getValue().get(0);
            else if(search.getKey().equalsIgnoreCase(Constants.NetworkParameters.is_share))
                searchValue = search.getValue().get(0);

        return searchValue;
    }

}
