package taxi.ratingen.utilz;

/**
 * Created by root on 11/16/17.
 */

public class SocketMessageModel {

    public String id/*, pickup_address*/;
    public String clientId, clientToken;
    public Double pick_lat, pick_lng,lat,lng;

    public SocketMessageModel build() {
        SocketMessageModel message = new SocketMessageModel();
        message.id = id;
        message.clientId = clientId;
        message.clientToken = clientToken;
        message.pick_lat = pick_lat;
        message.pick_lng = pick_lng;
        message.lat = lat;
        message.lng = lng;
/*
        message.pickup_address = pickup_address;
*/
        return message;
    }
}
