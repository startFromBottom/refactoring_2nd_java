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

        JsonObject invoice = invoices.getAsJsonArray().get(0).getAsJsonObject();
        String result = String.format("청구 내역 (고객명 : %s)\n", invoice.get("customer").getAsString());

        for (JsonElement perfEle : getPerformances(invoices)) {
            JsonObject perf = perfEle.getAsJsonObject();
            result += String.format("%s: %s (%s)\n", getPlay(plays, perf).get("name"),
                    getAmount(perf, getPlay(plays, perf)) / 100, getAudience(perf));
        }

        result += String.format("총액: %s\n", totalAmount(plays, getPerformances(invoices)) / 100);
        result += String.format("적립 포인트 : %s점\n", totalVolumeCredits(plays, getPerformances(invoices)));

        return result;

    }

    private static JsonArray getPerformances(JsonElement invoices) {
        return invoices.getAsJsonArray().get(0).getAsJsonObject()
                .get("performances").getAsJsonArray();
    }


    private static int totalAmount(JsonElement plays, JsonArray performances) {
        int result = 0;
        for (JsonElement perfEle : performances) {
            JsonObject perf = perfEle.getAsJsonObject();
            result += getAmount(perf, getPlay(plays, perf));
        }
        return result;
    }


    private static int totalVolumeCredits(JsonElement plays, JsonArray performances) {
        int result = 0;
        for (JsonElement perf : performances) {
            result += getVolumeCredits(plays, perf.getAsJsonObject());
        }
        return result;
    }

    private static int getVolumeCredits(JsonElement plays, JsonObject perf) {

        int result = 0;
        result += Math.max(getAudience(perf) - 30, 0);
        String type = getPlay(plays, perf).get("type").getAsString();
        if (type.equals("comedy")) {
            result += Math.floor((double) getAudience(perf) / 5);
        }
        return result;
    }

    private static int getAmount(JsonObject aPerformance, JsonObject play) {

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
