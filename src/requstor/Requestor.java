package requstor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Requestor {
    private UrlBuilder urlBuilder;
    private boolean status;

    public Requestor(String domain) {
        urlBuilder = new UrlBuilder(domain);
        status = setStatus();
    }

    public boolean getStatus() {
        return this.status;
    }

    public String register(String diff, String name, double sec) {
        if (!status) return null;
        String urlString = urlBuilder.register(diff, name, sec);
        return request(urlString);
    }

    public String get() {
        if (!status) return null;
        String urlString = urlBuilder.get();
        return request(urlString);
    }

    public String get(String diff) {
        if (!status) return null;
        String urlString = urlBuilder.get(diff);
        return request(urlString);
    }

    public String reset() {
        if (!status) return null;
        String urlString = urlBuilder.reset();
        return request(urlString);
    }

    public boolean setStatus() {
        String urlString = urlBuilder.getBaseUrl();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();

            System.out.println("responseCode: " + responseCode);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String request(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] args) throws ParseException {
        Requestor requestor = new Requestor("http://localhost:3000"); // 위 경로로 서버와 통신하는 객체 생성


        String hardResponse = requestor.get("hard"); // hard난이도 기록 전부 불러오기, "false"면 실패한 것(접근금지)
//        Boolean boolResponse = requestor.register("hard", "jinseok", 30);// ("jinseok",30) 을 hard에 저장
        String resetResponse = requestor.reset(); // 모든 기록 초기화 (굳이 쓸필요 없음)


        JSONParser jsonParser = new JSONParser(); // JSONParser 사용법은 무조건 숙지하셔야 됩니다.

        JSONArray jsonArray = (JSONArray) jsonParser.parse(hardResponse); // String을 JSONArray로 변경하여 반복문으로 접근가능

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject rank = (JSONObject) jsonArray.get(i); // 각 객체는 JSONObject이므로 get(key)로 값 획득 가능

            System.out.println(rank.get("name") + " " + rank.get("sec"));
        }

    }
}
