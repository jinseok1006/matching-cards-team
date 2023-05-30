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

    public static void main(String[] args) {
        String dest = "http://localhost:3000";
        UrlBuilder urlBuilder = new UrlBuilder(dest);

        String diff = "normal";
        String name = "John";
        int sec = 10;

        System.out.println(urlBuilder.register(diff, name, sec)); // "http://localhost:3000/register/normal?name=John&sec=10"

        System.out.println(urlBuilder.get(diff)); // "http://localhost:3000/get/normal/"

        System.out.println(urlBuilder.reset()); // "http://localhost:3000/"

        System.out.println(urlBuilder.get());


    }
}