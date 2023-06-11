package requstor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class UrlBuilder {
    private String baseUrl;
    private String endpoint;

    public UrlBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
        this.endpoint = "";
    }

    public String register(String diff, String name, double sec) {
        this.endpoint = "/register/" + diff + "?name=" + urlEncode(name) + "&sec=" + sec;
        return toString();
    }

    public String get() {
        this.endpoint="/get";
        return toString();
    }

    public String get(String diff) {
        this.endpoint = "/get/" + diff + "/";
        return toString();
    }

    public String reset() {
        this.endpoint = "/reset";
        return toString();
    }

    public String getBaseUrl() {
        this.endpoint="";
        return toString();
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value;
        }
    }

    @Override
    public String toString() {
        return baseUrl + endpoint;
    }

}