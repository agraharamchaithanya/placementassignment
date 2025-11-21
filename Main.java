import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class Main {
    public static void main(String[] args) {
        String inputFile = "input.json";
        if (args.length > 0) inputFile = args[0]; // allow passing filename

        try (JsonReader reader = new JsonReader(new FileReader(inputFile))) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            // Read keys block
            JsonObject keysObj = root.getAsJsonObject("keys");
            int n = keysObj.get("n").getAsInt();
            int k = keysObj.get("k").getAsInt();

            System.out.println("n = " + n);
            System.out.println("k = " + k);
            System.out.println("Decoded Roots:");

            // collect numeric keys except "keys"
            List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(root.entrySet());
            // remove the "keys" entry
            entries.removeIf(e -> e.getKey().equals("keys"));

            // sort remaining entries by numeric key (so "1","2","3","6" order)
            Collections.sort(entries, new Comparator<Map.Entry<String, JsonElement>>() {
                public int compare(Map.Entry<String, JsonElement> a, Map.Entry<String, JsonElement> b) {
                    try {
                        Integer ia = Integer.parseInt(a.getKey());
                        Integer ib = Integer.parseInt(b.getKey());
                        return ia.compareTo(ib);
                    } catch (Exception ex) {
                        return a.getKey().compareTo(b.getKey());
                    }
                }
            });

            for (Map.Entry<String, JsonElement> entry : entries) {
                String idx = entry.getKey();
                JsonObject rootObj = entry.getValue().getAsJsonObject();

                String baseStr = rootObj.get("base").getAsString();
                String valueStr = rootObj.get("value").getAsString();

                int base = Integer.parseInt(baseStr);

                // BigInteger can parse with radix up to 36, handle lowercase
                String normalizedValue = valueStr.toLowerCase();

                // Remove leading spaces if any
                normalizedValue = normalizedValue.trim();

                // Convert using BigInteger
                BigInteger decoded;
                try {
                    decoded = new BigInteger(normalizedValue, base);
                } catch (NumberFormatException ex) {
                    // If number contains digits >= base or unsupported chars, report error
                    System.out.println("Root " + idx + " : invalid digits for base " + base + " -> " + valueStr);
                    continue;
                }

                System.out.println("Root " + idx + " = " + decoded.toString());
            }

        } catch (Exception e) {
            System.err.println("Error reading or parsing JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
