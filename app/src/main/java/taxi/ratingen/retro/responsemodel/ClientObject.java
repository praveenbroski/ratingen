package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ClientObject implements Serializable {
    @Expose
    public String client_id;
    @Expose
    public String client_token;
}
