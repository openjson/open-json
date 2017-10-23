package com.github.openjson.performance;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONTokener;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Performance test for toString method. Use manual run from command line or IDE.
 */
public final class ToStringPT {

    public static void main(String[] v) throws IOException {
        JSONArray json = (JSONArray) new JSONTokener(new InputStreamReader(ToStringPT.class.getResourceAsStream("/sample-01.json"))).nextValue();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 300 * 1000; i++) {
            //noinspection ResultOfMethodCallIgnored
            json.toString();
        }
        System.out.println("Total time: " + ((System.currentTimeMillis() - t0) / 1000L) + " seconds");
    }
}
