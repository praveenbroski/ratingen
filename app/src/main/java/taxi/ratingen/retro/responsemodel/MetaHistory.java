package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaHistory {

    @SerializedName("pagination")
    @Expose
    public Pagination pagination;

    public class Pagination {

        @SerializedName("total")
        @Expose
        public Integer total;

        @SerializedName("count")
        @Expose
        public Integer count;

        @SerializedName("per_page")
        @Expose
        public Integer per_page;

        @SerializedName("current_page")
        @Expose
        public Integer current_page;

        @SerializedName("total_pages")
        @Expose
        public Integer total_pages;

        @SerializedName("links")
        @Expose
        public Links links;

    }

    public class Links {

        @SerializedName("next")
        @Expose
        public String next;

    }

}
