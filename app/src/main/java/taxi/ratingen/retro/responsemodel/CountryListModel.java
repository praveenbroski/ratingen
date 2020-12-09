package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryListModel implements Parcelable {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("iso2")
    @Expose
    public String iso2;
    @SerializedName("iso3")
    @Expose
    public String iso3;
    @SerializedName("dial_code")
    @Expose
    public String dialCode;
    @SerializedName("flag")
    @Expose
    public String flag;
    @SerializedName("code")
    @Expose
    public String code;

    protected CountryListModel(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        name = in.readString();
        iso2 = in.readString();
        iso3 = in.readString();
        dialCode = in.readString();
        flag = in.readString();
        code = in.readString();
    }

    public static final Creator<CountryListModel> CREATOR = new Creator<CountryListModel>() {
        @Override
        public CountryListModel createFromParcel(Parcel in) {
            return new CountryListModel(in);
        }

        @Override
        public CountryListModel[] newArray(int size) {
            return new CountryListModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fullName);
        parcel.writeString(name);
        parcel.writeString(iso2);
        parcel.writeString(iso3);
        parcel.writeString(dialCode);
        parcel.writeString(flag);
        parcel.writeString(code);
    }
}
