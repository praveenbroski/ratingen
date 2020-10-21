package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

public class So extends BaseResponse {

@SerializedName("id")
@Expose
public Integer id;
@SerializedName("name")
@Expose
public String name;
@SerializedName("number")
@Expose
public String number;

}