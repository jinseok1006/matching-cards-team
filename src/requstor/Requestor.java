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
    // 각 메서드에 맞는 요청 경로 생성
    private UrlBuilder urlBuilder;
    // 서버의 온라인 상태
    private boolean status;
    private JSONParser jsonParser = new JSONParser();

    public Requestor() {
        String domain = getDomain();
        urlBuilder = new UrlBuilder(domain);

        System.out.println(domain);
        status = setStatus();
    }

    // 유동아이피 트릭
    public String getDomain() {
        return request("https://raw.githubusercontent.com/jinseok1006/matching-cards-team/master/.server");
    }

    // setStatus로 저장된 값
    public boolean getStatus() {
        return this.status;
    }

    // diff에 맞는 기록 저장하기
    public Boolean register(String diff, String name, double sec) {
        if (!status) return null;
        String urlString = urlBuilder.register(diff, name, sec);
        String response = request(urlString);
        return Boolean.parseBoolean(response);
    }

    // diff에 맞는 기록 불러오기
    public Object[][] get(String diff) {
        if (!status) return null;
        String urlString = urlBuilder.get(diff);

        String response = request(urlString);
        return jsonToObjectArray(response);
    }

    public Object[][] jsonToObjectArray(String response) {
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(response);
            Object[][] rankArray = new Object[jsonArray.size()][];

            for (int i = 0; i < jsonArray.size(); i++) {
                var player = (JSONObject) jsonArray.get(i);
                rankArray[i] = new Object[]{i + 1, player.get("name"), player.get("sec"), player.get("date")};
            }

            return rankArray;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 서버 상태를 확인하고 저장
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

    // 요청경로에 따라 요청하고 서버에서 받은 결과값 반환
    private String request(String urlString) {
        try {
            // String으로 구성된 경로를 URL객체로 생성하고 서버와 연결
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 서버에서 값을 받는 스트림을 생성하고 stringBuilder에 모두 저장
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }

            // 서버에서 받아온 값을 리턴
            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
