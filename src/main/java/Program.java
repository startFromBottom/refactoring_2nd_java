import com.google.gson.*;

import java.util.List;
import java.util.Map;

public class Program {


    private static final String playsString = "{\"hamlet\":{\"name\":\"Hamlet\", \"type\": \"tragedy\"}," +
            "\"as-like\":{\"name\":\"As you like it\", \"type\": \"comedy\"}," +
            "\"othello\":{\"name\":\"Othello\", \"type\": \"tragedy\"}}";

    private static final String invoicesString = "[{\"customer\":\"BigCo\",\"performances\":" +
            "[{\"playID\":\"hamlet\",\"audience\":55}," +
            "{\"playID\":\"as-like\",\"audience\":35}," +
            "{\"playID\":\"othello\",\"audience\":40}]}]";

    private static final JsonParser parser = new JsonParser();

    public static void main(String[] args) {

        System.out.println(statement(parseStringToJsonElement(playsString),
                parseStringToJsonElement(invoicesString)));


    }

    private static JsonElement parseStringToJsonElement(String jsonString) {
        return parser.parse(jsonString);
    }


    private static String statement(JsonElement plays, JsonElement invoices) {

        int totalAmount = 0;
        int volumeCredits = 0;

        JsonObject invoice = invoices.getAsJsonArray().get(0).getAsJsonObject();
        JsonArray performances = invoice.get("performances").getAsJsonArray();
        String result = String.format("청구 내역 (고객명 : %s)\n", invoice.get("customer").getAsString());

        for (JsonElement perfJsonElement : performances) {
            JsonObject perf = perfJsonElement.getAsJsonObject();
            String playID = perf.getAsJsonObject().get("playID").getAsString();
            JsonObject play = plays.getAsJsonObject().get(playID).getAsJsonObject();

            int thisAmount = 0;
            int audience = perf.getAsJsonObject().get("audience").getAsInt();
            String type = play.get("type").getAsString();
            switch (type) {
                case "tragedy":
                    if (audience > 30) {
                        thisAmount += 1000 * (audience - 30);
                    }
                    break;
                case "comedy":
                    if (audience > 20) {
                        thisAmount += 10000 + 500 * (audience - 20);
                    }
                    thisAmount += 300 * audience;
                    break;
                default:
                    throw new RuntimeException(String.format("알 수 없는 장르 : %s", type));
            }

            // 포인트를 적립한다.
            volumeCredits += Math.max(audience - 30, 0);
            // 희극 관객 5명마다 추가 포인트를 제공한다.
            if (type.equals("comedy")) {
                volumeCredits += Math.floor((double) audience / 5);
            }

            // 청구 내역을 출력한다.
            result += String.format("%s: %s (%s)\n", play.get("name"), thisAmount / 100, audience);
            totalAmount += thisAmount;
        }
        result += String.format("총액: %s\n", totalAmount / 100);
        result += String.format("적립 포인트 : %s점\n", volumeCredits);

        return result;

    }

}
