

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;

public class ShamirSecretSharing {

    // Function to decode the value from the given base to decimal
    private static int decodeValue(int base, String value) {
        return Integer.parseInt(value, base);
    }

    // Function to perform Lagrange Interpolation and return f(0)
    private static double lagrangeInterpolation(List<int[]> points) {
        double result = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            int xi = points.get(i)[0];
            int yi = points.get(i)[1];
            double term = yi;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int xj = points.get(j)[0];
                    term *= (0.0 - xj) / (xi - xj);
                }
            }

            result += term;
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            // Read JSON input from file
            BufferedReader reader = new BufferedReader(new FileReader("input.json"));
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line.trim());
            }
            reader.close();

            // Parse JSON manually
            Map<String, String> inputData = parseJson(jsonData.toString());

            // Extract n and k
            int n = Integer.parseInt(inputData.get("n"));
            int k = Integer.parseInt(inputData.get("k"));

            // Decode the points
            List<int[]> points = new ArrayList<>();
            for (String key : inputData.keySet()) {
                if (!key.equals("n") && !key.equals("k")) {
                    int x = Integer.parseInt(key); // Use the key as the x-value
                    String baseAndValue = inputData.get(key);
                    String[] baseValueSplit = baseAndValue.split(":");
                    int base = Integer.parseInt(baseValueSplit[0]);
                    String value = baseValueSplit[1];
                    int y = decodeValue(base, value);
                    points.add(new int[]{x, y});
                }
            }

            // Use only the first k points for interpolation (as per the problem statement)
            points = points.subList(0, k);

            // Find the constant term c using Lagrange interpolation
            double constantTerm = lagrangeInterpolation(points);

            // Output the constant term
            System.out.println("The constant term (c) of the polynomial is: " + Math.round(constantTerm));
        } catch (Exception e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }

    // Function to manually parse a simple JSON format
    private static Map<String, String> parseJson(String json) {
        Map<String, String> data = new HashMap<>();
        json = json.replaceAll("[{}\"]", ""); // Remove braces and quotes
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Special handling for base and value pairs
                if (key.matches("\\d+")) {
                    int baseIndex = value.indexOf("base");
                    int valueIndex = value.indexOf("value");
                    String base = value.substring(baseIndex + 5, valueIndex).trim(); // Extract base
                    String val = value.substring(valueIndex + 6).trim(); // Extract value
                    data.put(key, base + ":" + val);
                } else {
                    data.put(key, value);
                }
            }
        }
        return data;
    }
}
