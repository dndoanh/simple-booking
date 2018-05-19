package simplebooking.greentech.vn.simplebooking.common;

public class URLs {
    private static final String BASE_URL = "http://192.168.1.3:8080/api/rest/v1/";

    public static final String URL_GET_COUNTRY = BASE_URL + "masterdata-country";
    public static final String URL_VALIDATE_PHONE = BASE_URL + "customer-validate-phone";
    public static final String URL_SUBMIT_LOCATIONS = BASE_URL + "trk-vehicle-location-log";
}
