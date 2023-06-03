package de.umr.tsquare.dataintegration.util;

import java.util.Arrays;

public class StringUtil {

    public static int calculateLevenshteinStationDistance (String dbStation, String rmvStation) {
        return min(
                calculateLevenshteinDistance(
                        appendStationsuffix(removeCharsBetweenBrackets(dbStation), false).toLowerCase(),
                        removeCharsBetweenBrackets(rmvStation).toLowerCase()
                ),
                calculateLevenshteinDistance(
                        appendStationsuffix(removeCharsBetweenBrackets(dbStation), true).toLowerCase(),
                        removeCharsBetweenBrackets(rmvStation).toLowerCase()
                )
        );
    }
    private static int calculateLevenshteinDistance (String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static String removeCharsBetweenBrackets(String s) {
        return s.replaceAll("\\(.*?\\)", "");
    }

    public static String appendStationsuffix(String s, boolean isMainStation) {
        if (isMainStation) {
            return s + " Hauptbahnhof";
        }
        return s + " Bahnhof";
    }
    private static int costOfSubstitution (char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min (int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
