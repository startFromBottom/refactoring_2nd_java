import com.google.gson.*;

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
            int thisAmount = amountFor(perf, getPlay(plays, perf));
            // 포인트를 적립한다.
            volumeCredits += Math.max(getAudience(perf) - 30, 0);
            // 희극 관객 5명마다 추가 포인트를 제공한다.
            String type = getPlay(plays, perf).get("type").getAsString();
            if (type.equals("comedy")) {
                volumeCredits += Math.floor((double) getAudience(perf) / 5);
            }

            // 청구 내역을 출력한다.
            result += String.format("%s: %s (%s)\n", getPlay(plays, perf).get("name"), thisAmount / 100, getAudience(perf));
            totalAmount += thisAmount;
        }
        result += String.format("총액: %s\n", totalAmount / 100);
        result += String.format("적립 포인트 : %s점\n", volumeCredits);

        return result;

    }


    private static int amountFor(JsonObject aPerformance, JsonObject play) {

        int result = 0;
        int audience = getAudience(aPerformance);
        String type = play.get("type").getAsString();
        switch (type) {
            case "tragedy":
                if (audience > 30) {
                    result += 1000 * (audience - 30);
                }
                break;
            case "comedy":
                if (audience > 20) {
                    result += 10000 + 500 * (audience - 20);
                }
                result += 300 * audience;
                break;
            default:
                throw new RuntimeException(String.format("알 수 없는 장르 : %s", type));
        }

        return result;
    }

    private static JsonObject getPlay(JsonElement plays, JsonObject aPerformance) {
        String playID = aPerformance.getAsJsonObject().get("playID").getAsString();
        return plays.getAsJsonObject().get(playID).getAsJsonObject();
    }

    private static int getAudience(JsonObject aPerformance) {
        return aPerformance.getAsJsonObject().get("audience").getAsInt();
    }

}
