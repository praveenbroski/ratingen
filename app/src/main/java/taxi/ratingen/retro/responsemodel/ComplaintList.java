package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintList {

@SerializedName("id")
@Expose
public Integer id;
@SerializedName("title")
@Expose
public String title;

    @Override
    public String toString() {
        return title;
    }
}