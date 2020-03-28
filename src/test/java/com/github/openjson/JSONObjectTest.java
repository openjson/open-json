/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.openjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This black box test was written without inspecting the non-free org.json sourcecode.
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class JSONObjectTest {
    @Test
    public void testKeyset() {
        JSONObject x = new JSONObject("{'a':1, 'b':2, 'c':3}");
        Set<String> k = new TreeSet<String>(Arrays.asList("a", "b", "c"));
        assertEquals(x.keySet(), k);
        x = new JSONObject("{}");
        assertEquals(x.keySet().size(), 0);
    }

    @Test
    public void testEmptyObject() throws JSONException {
        JSONObject object = new JSONObject();
        assertEquals(0, object.length());

        // bogus (but documented) behaviour: returns null rather than the empty object!
        assertNull(object.names());

        // returns null rather than an empty array!
        assertNull(object.toJSONArray(new JSONArray()));
        assertEquals("{}", object.toString());
        assertEquals("{}", object.toString(5));
        try {
            object.get("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getBoolean("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getDouble("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getInt("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONArray("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getLong("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getString("foo");
            fail();
        } catch (JSONException ignored) {
        }
        assertFalse(object.has("foo"));
        assertTrue(object.isNull("foo")); // isNull also means "is not present"
        assertNull(object.opt("foo"));
        assertFalse(object.optBoolean("foo"));
        assertTrue(object.optBoolean("foo", true));
        assertEquals(Double.NaN, object.optDouble("foo"), 0);
        assertEquals(5.0, object.optDouble("foo", 5.0), 0);
        assertEquals(0, object.optInt("foo"));
        assertEquals(5, object.optInt("foo", 5));
        assertNull(object.optJSONArray("foo"));
        assertNull(object.optJSONObject("foo"));
        assertEquals(0, object.optLong("foo"));
        assertEquals(Long.MAX_VALUE - 1, object.optLong("foo", Long.MAX_VALUE - 1));
        assertEquals("", object.optString("foo")); // empty string is default!
        assertEquals("bar", object.optString("foo", "bar"));
        assertNull(object.remove("foo"));
    }

    @Test
    public void testEqualsAndHashCode() throws JSONException {
        JSONObject a = new JSONObject();
        JSONObject b = new JSONObject();

        // JSON object doesn't override either equals or hashCode (!)
        assertNotEquals(a, b);
        assertEquals(a.hashCode(), System.identityHashCode(a));
    }

    @Test
    public void testGet() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", value);
        object.put("bar", new Object());
        object.put("baz", new Object());
        assertSame(value, object.get("foo"));
        try {
            object.get("FOO");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put(null, value);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.get(null);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPut() throws JSONException {
        JSONObject object = new JSONObject();
        assertSame(object, object.put("foo", true));
        object.put("foo", false);
        assertEquals(false, object.get("foo"));

        object.put("foo", 5.0d);
        assertEquals(5.0d, object.get("foo"));
        object.put("foo", 0);
        assertEquals(0, object.get("foo"));
        object.put("bar", Long.MAX_VALUE - 1);
        assertEquals(Long.MAX_VALUE - 1, object.get("bar"));
        object.put("baz", "x");
        assertEquals("x", object.get("baz"));
        object.put("bar", JSONObject.NULL);
        assertSame(JSONObject.NULL, object.get("bar"));
    }

    @Test
    public void testPutNullRemoves() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.put("foo", null);
        assertEquals(0, object.length());
        assertFalse(object.has("foo"));
        try {
            object.get("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPutOpt() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.putOpt("foo", null);
        assertEquals("bar", object.get("foo"));
        object.putOpt(null, null);
        assertEquals(1, object.length());
        object.putOpt(null, "bar");
        assertEquals(1, object.length());
    }

    @Test
    public void testPutOptUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.putOpt("foo", Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.putOpt("foo", Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testRemove() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        assertNull(object.remove(null));
        assertNull(object.remove(""));
        assertNull(object.remove("bar"));
        assertEquals("bar", object.remove("foo"));
        assertNull(object.remove("foo"));
    }

    @Test
    public void testBooleans() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", false);
        object.put("baz", "true");
        object.put("quux", "false");
        assertEquals(4, object.length());
        assertTrue(object.getBoolean("foo"));
        assertFalse(object.getBoolean("bar"));
        assertTrue(object.getBoolean("baz"));
        assertFalse(object.getBoolean("quux"));
        assertFalse(object.isNull("foo"));
        assertFalse(object.isNull("quux"));
        assertTrue(object.has("foo"));
        assertTrue(object.has("quux"));
        assertFalse(object.has("missing"));
        assertTrue(object.optBoolean("foo"));
        assertFalse(object.optBoolean("bar"));
        assertTrue(object.optBoolean("baz"));
        assertFalse(object.optBoolean("quux"));
        assertFalse(object.optBoolean("missing"));
        assertTrue(object.optBoolean("foo", true));
        assertFalse(object.optBoolean("bar", true));
        assertTrue(object.optBoolean("baz", true));
        assertFalse(object.optBoolean("quux", true));
        assertTrue(object.optBoolean("missing", true));

        object.put("foo", "truE");
        object.put("bar", "FALSE");
        assertTrue(object.getBoolean("foo"));
        assertFalse(object.getBoolean("bar"));
        assertTrue(object.optBoolean("foo"));
        assertFalse(object.optBoolean("bar"));
        assertTrue(object.optBoolean("foo", false));
        assertFalse(object.optBoolean("bar", false));
    }

    @Test
    public void testCoerceStringToNumber() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("v1", "1");
        object.put("v2", "-1");
        assertEquals(1, object.getInt("v1"));
        assertEquals(-1, object.getLong("v2"));
    }

    @Test
    public void testCoerceStringToNumberWithFail() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("key", "not a number");
        try {
            object.getLong("key");
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(0, object.optLong("key"));
    }

    // http://code.google.com/p/android/issues/detail?id=16411
    @Test
    public void testCoerceStringToBoolean() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "maybe");
        try {
            object.getBoolean("foo");
            fail();
        } catch (JSONException ignored) {
        }
        assertFalse(object.optBoolean("foo"));
        assertTrue(object.optBoolean("foo", true));
    }

    @Test
    public void testNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", Double.MIN_VALUE);
        object.put("bar", 9223372036854775806L);
        object.put("baz", Double.MAX_VALUE);
        object.put("quux", -0d);
        assertEquals(4, object.length());

        String toString = object.toString();
        assertTrue(toString, toString.contains("\"foo\":4.9E-324"));
        assertTrue(toString, toString.contains("\"bar\":9223372036854775806"));
        assertTrue(toString, toString.contains("\"baz\":1.7976931348623157E308"));

        // toString() and getString() return different values for -0d!
        assertTrue(toString, toString.contains("\"quux\":-0}") // no trailing decimal point
                || toString.contains("\"quux\":-0,"));

        assertEquals(Double.MIN_VALUE, object.get("foo"));
        assertEquals(9223372036854775806L, object.get("bar"));
        assertEquals(Double.MAX_VALUE, object.get("baz"));
        assertEquals(-0d, object.get("quux"));
        assertEquals(Double.MIN_VALUE, object.getDouble("foo"), 0);
        assertEquals(9.223372036854776E18, object.getDouble("bar"), 0);
        assertEquals(Double.MAX_VALUE, object.getDouble("baz"), 0);
        assertEquals(-0d, object.getDouble("quux"), 0);
        assertEquals(0, object.getLong("foo"));
        assertEquals(9223372036854775806L, object.getLong("bar"));
        assertEquals(Long.MAX_VALUE, object.getLong("baz"));
        assertEquals(0, object.getLong("quux"));
        assertEquals(0, object.getInt("foo"));
        assertEquals(-2, object.getInt("bar"));
        assertEquals(Integer.MAX_VALUE, object.getInt("baz"));
        assertEquals(0, object.getInt("quux"));
        assertEquals(Double.MIN_VALUE, object.opt("foo"));
        assertEquals(9223372036854775806L, object.optLong("bar"));
        assertEquals(Double.MAX_VALUE, object.optDouble("baz"), 0);
        assertEquals(0, object.optInt("quux"));
        assertEquals(Double.MIN_VALUE, object.opt("foo"));
        assertEquals(9223372036854775806L, object.optLong("bar"));
        assertEquals(Double.MAX_VALUE, object.optDouble("baz"), 0);
        assertEquals(0, object.optInt("quux"));
        assertEquals(Double.MIN_VALUE, object.optDouble("foo", 5.0d), 0);
        assertEquals(9223372036854775806L, object.optLong("bar", 1L));
        assertEquals(Long.MAX_VALUE, object.optLong("baz", 1L));
        assertEquals(0, object.optInt("quux", -1));
        assertEquals("4.9E-324", object.getString("foo"));
        assertEquals("9223372036854775806", object.getString("bar"));
        assertEquals("1.7976931348623157E308", object.getString("baz"));
        assertEquals("-0.0", object.getString("quux"));
    }

    @Test
    public void testFloats() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", (Float) Float.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Float) Float.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Float) Float.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testOtherNumbers() throws JSONException {
        Number nan = new Number() {
            @Override
            public int intValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public long longValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public float floatValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public double doubleValue() {
                return Double.NaN;
            }

            @Override
            public String toString() {
                return "x";
            }
        };

        JSONObject object = new JSONObject();
        try {
            object.put("foo", nan);
            fail("Object.put() accepted a NaN (via a custom Number class)");
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testForeignObjects() throws JSONException {
        Object foreign = new Object() {
            @Override
            public String toString() {
                return "x";
            }
        };

        // foreign object types are accepted and treated as Strings!
        JSONObject object = new JSONObject();
        object.put("foo", foreign);
        assertEquals("{\"foo\":\"x\"}", object.toString());
    }

    @Test
    public void testNullKeys() {
        try {
            new JSONObject().put(null, false);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 0.0d);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 5L);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, "foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStrings() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "true");
        object.put("bar", "5.5");
        object.put("baz", "9223372036854775806");
        object.put("quux", "null");
        object.put("height", "5\"8' tall");

        assertTrue(object.toString().contains("\"foo\":\"true\""));
        assertTrue(object.toString().contains("\"bar\":\"5.5\""));
        assertTrue(object.toString().contains("\"baz\":\"9223372036854775806\""));
        assertTrue(object.toString().contains("\"quux\":\"null\""));
        assertTrue(object.toString().contains("\"height\":\"5\\\"8' tall\""));

        assertEquals("true", object.get("foo"));
        assertEquals("null", object.getString("quux"));
        assertEquals("5\"8' tall", object.getString("height"));
        assertEquals("true", object.opt("foo"));
        assertEquals("5.5", object.optString("bar"));
        assertEquals("true", object.optString("foo", "x"));
        assertFalse(object.isNull("foo"));

        assertTrue(object.getBoolean("foo"));
        assertTrue(object.optBoolean("foo"));
        assertTrue(object.optBoolean("foo", false));
        assertEquals(0, object.optInt("foo"));
        assertEquals(-2, object.optInt("foo", -2));

        assertEquals(5.5d, object.getDouble("bar"), 0);
        assertEquals(5L, object.getLong("bar"));
        assertEquals(5, object.getInt("bar"));
        assertEquals(5, object.optInt("bar", 3));

        // The last digit of the string is a 6 but getLong returns a 7. It's probably parsing as a
        // double and then converting that to a long. This is consistent with JavaScript.
        assertEquals(9223372036854775807L, object.getLong("baz"));
        assertEquals(9.223372036854776E18, object.getDouble("baz"), 0);
        assertEquals(Integer.MAX_VALUE, object.getInt("baz"));

        assertFalse(object.isNull("quux"));
        try {
            object.getDouble("quux");
            fail();
        } catch (JSONException e) {
            // expected
        }
        assertEquals(Double.NaN, object.optDouble("quux"), 0);
        assertEquals(-1.0d, object.optDouble("quux", -1.0d), 0);

        object.put("foo", "TRUE");
        assertTrue(object.getBoolean("foo"));
    }

    @Test
    public void testJSONObjects() throws JSONException {
        JSONObject object = new JSONObject();

        JSONArray a = new JSONArray();
        JSONObject b = new JSONObject();
        object.put("foo", a);
        object.put("bar", b);

        assertSame(a, object.getJSONArray("foo"));
        assertSame(b, object.getJSONObject("bar"));
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONArray("bar");
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(a, object.optJSONArray("foo"));
        assertEquals(b, object.optJSONObject("bar"));
        assertNull(object.optJSONArray("bar"));
        assertNull(object.optJSONObject("foo"));
    }

    @Test
    public void testNullCoercionToString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", JSONObject.NULL);
        assertEquals("null", object.getString("foo"));
    }

    @Test
    public void testArrayCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "[true]");
        try {
            object.getJSONArray("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testObjectCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "{}");
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPutCollection() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("array", Arrays.asList("1", 1, 3D));

        JSONArray array = object.getJSONArray("array");
        assertEquals(3, array.length());
        assertEquals("1", array.get(0));
        assertEquals(1, array.get(1));
        assertEquals(3D, array.get(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutMap() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("map", new HashMap() {{
            put("x", "1");
            put("y", 1);
            put("z", 3D);
        }});

        JSONObject map = object.getJSONObject("map");
        assertEquals(3, map.length());
        assertEquals("1", map.get("x"));
        assertEquals(1, map.get("y"));
        assertEquals(3D, map.get("z"));
    }

    @Test
    public void testAccumulateValueChecking() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        object.accumulate("foo", 1);
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        object.accumulate("foo", 2);
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testToJSONArray() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", true);
        object.put("bar", 5.0d);
        object.put("baz", -0.0d);
        object.put("quux", value);

        JSONArray names = new JSONArray();
        names.put("baz");
        names.put("quux");
        names.put("foo");

        JSONArray array = object.toJSONArray(names);
        assertEquals(-0.0d, array.get(0));
        assertEquals(value, array.get(1));
        assertEquals(true, array.get(2));

        object.put("foo", false);
        assertEquals(true, array.get(2));
    }

    @Test
    public void testToJSONArrayMissingNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", 5.0d);
        object.put("baz", JSONObject.NULL);

        JSONArray names = new JSONArray();
        names.put("bar");
        names.put("foo");
        names.put("quux");
        names.put("baz");

        JSONArray array = object.toJSONArray(names);
        assertEquals(4, array.length());

        assertEquals(5.0d, array.get(0));
        assertEquals(true, array.get(1));
        try {
            array.get(2);
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(JSONObject.NULL, array.get(3));
    }

    @Test
    public void testToJSONArrayNull() throws JSONException {
        JSONObject object = new JSONObject();
        assertNull(object.toJSONArray(null));
        object.put("foo", 5);
        try {
            object.toJSONArray(null);
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testToJSONArrayEndsUpEmpty() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        JSONArray array = new JSONArray();
        array.put("bar");
        assertEquals(1, object.toJSONArray(array).length());
    }

    @Test
    public void testToJSONArrayNonString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("null", 10);
        object.put("false", 15);

        JSONArray names = new JSONArray();
        names.put(JSONObject.NULL);
        names.put(false);
        names.put("foo");

        // array elements are converted to strings to do name lookups on the map!
        JSONArray array = object.toJSONArray(names);
        assertEquals(3, array.length());
        assertEquals(10, array.get(0));
        assertEquals(15, array.get(1));
        assertEquals(5, array.get(2));
    }

    @Test
    public void testPutUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPutUnsupportedNumbersAsObjects() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", (Double) Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Double) Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Double) Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    /**
     * Although JSONObject is usually defensive about which numbers it accepts,
     * it doesn't check inputs in its constructor.
     */
    @Test
    public void testCreateWithUnsupportedNumbers() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", Double.NaN);
        contents.put("bar", Double.NEGATIVE_INFINITY);
        contents.put("baz", Double.POSITIVE_INFINITY);

        JSONObject object = new JSONObject(contents);
        assertEquals(Double.NaN, object.get("foo"));
        assertEquals(Double.NEGATIVE_INFINITY, object.get("bar"));
        assertEquals(Double.POSITIVE_INFINITY, object.get("baz"));
    }

    @Test
    public void testToStringWithUnsupportedNumbers() {
        // when the object contains an unsupported number, toString returns null!
        JSONObject object = new JSONObject(Collections.singletonMap("foo", Double.NaN));
        assertNull(object.toString());
    }

    @Test
    public void testMapConstructorWithNull() throws JSONException {
        JSONObject object = new JSONObject((Map)null);
        assertEquals(0, object.keySet().size());
    }

    @Test
    public void testMapConstructorCopiesContents() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", 5);
        JSONObject object = new JSONObject(contents);
        contents.put("foo", 10);
        assertEquals(5, object.get("foo"));
    }

    @Test
    public void testMapConstructorWithBogusEntries() {
        Map<Object, Object> contents = new HashMap<Object, Object>();
        contents.put(5, 5);

        try {
            new JSONObject(contents);
            fail("JSONObject constructor doesn't validate its input!");
        } catch (Exception ignored) {
        }
    }

    /**
     * Warning: org.json package does allow nulls as values. We don't.
     * See {@link JSONObject#JSONObject(Map)} for details.
     */
    @Test(expected = NullPointerException.class)
    public void testMapConstructorWithBogusEntries2() {
        Map<Object, Object> contents = new HashMap<Object, Object>();
        contents.put(null, 5);
        new JSONObject(contents);
    }

    @Test
    public void testTokenerConstructor() throws JSONException {
        JSONObject object = new JSONObject(new JSONTokener("{\"foo\": false}"));
        assertEquals(1, object.length());
        assertEquals(false, object.get("foo"));
    }

    @Test
    public void testTokenerConstructorWrongType() throws JSONException {
        try {
            new JSONObject(new JSONTokener("[\"foo\", false]"));
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testTokenerConstructorNull() throws JSONException {
        try {
            new JSONObject((JSONTokener) null);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testTokenerConstructorParseFail() {
        try {
            new JSONObject(new JSONTokener("{"));
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStringConstructor() throws JSONException {
        JSONObject object = new JSONObject("{\"foo\": false}");
        assertEquals(1, object.length());
        assertEquals(false, object.get("foo"));
    }

    @Test
    public void testStringConstructorWrongType() throws JSONException {
        try {
            new JSONObject("[\"foo\", false]");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStringConstructorNull() throws JSONException {
        try {
            new JSONObject((String) null);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testStringConstructorParseFail() {
        try {
            new JSONObject("{");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testCopyConstructor() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", JSONObject.NULL);
        source.put("b", false);
        source.put("c", 5);

        JSONObject copy = new JSONObject(source, new String[]{"a", "c"});
        assertEquals(2, copy.length());
        assertEquals(JSONObject.NULL, copy.get("a"));
        assertEquals(5, copy.get("c"));
        assertNull(copy.opt("b"));
    }

    @Test
    public void testCopyConstructorMissingName() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", JSONObject.NULL);
        source.put("b", false);
        source.put("c", 5);

        JSONObject copy = new JSONObject(source, new String[]{"a", "c", "d"});
        assertEquals(2, copy.length());
        assertEquals(JSONObject.NULL, copy.get("a"));
        assertEquals(5, copy.get("c"));
        assertEquals(0, copy.optInt("b"));
    }

    @Test
    public void testCopyConstructorNoNames() throws JSONException {
        JSONObject obj = new JSONObject().put("var", "Test 1");
        JSONObject source = new JSONObject();
        source.put("a", JSONObject.NULL);
        source.put("b", false);
        source.put("c", 5);
        source.put("comp", obj);

        JSONObject copy = new JSONObject(source);
        assertEquals(source.length(), copy.length());
        assertEquals(source.get("a"), copy.get("a"));
        assertEquals(source.get("b"), copy.opt("b"));
        assertEquals(source.get("c"), copy.get("c"));
        assertEquals(source.get("comp"), copy.get("comp"));
    }

    @Test
    public void testArrayConstructor() throws JSONException {
        final JSONArray array = new JSONArray(new Object[] { 1, 2, 3 });
        JSONObject obj = new JSONObject(array);
        assertEquals("The result should be empty object", "{}", obj.toString());
    }

    @Test
    public void testAccumulateMutatesInPlace() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.accumulate("foo", 6);
        JSONArray array = object.getJSONArray("foo");
        assertEquals("[5,6]", array.toString());
        object.accumulate("foo", 7);
        assertEquals("[5,6,7]", array.toString());
    }

    @Test
    public void testAccumulateExistingArray() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("foo", array);
        object.accumulate("foo", 5);
        assertEquals("[5]", array.toString());
    }

    @Test
    public void testAccumulatePutArray() throws JSONException {
        JSONObject object = new JSONObject();
        object.accumulate("foo", 5);
        assertEquals("{\"foo\":5}", object.toString());
        object.accumulate("foo", new JSONArray());
        assertEquals("{\"foo\":[5,[]]}", object.toString());
    }

    @Test
    public void testAccumulateNull() {
        JSONObject object = new JSONObject();
        try {
            object.accumulate(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testEmptyStringKey() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("", 5);
        assertEquals(5, object.get(""));
        assertEquals("{\"\":5}", object.toString());
    }

    @Test
    public void testNullValue() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", JSONObject.NULL);
        object.put("bar", null);

        // there are two ways to represent null; each behaves differently!
        assertTrue(object.has("foo"));
        assertFalse(object.has("bar"));
        assertTrue(object.isNull("foo"));
        assertTrue(object.isNull("bar"));
    }

    @Test
    public void testNullValue_equalsAndHashCode() {
        assertTrue(JSONObject.NULL.equals(null)); // guaranteed by javadoc
        // not guaranteed by javadoc, but seems like a good idea
        assertEquals(0, JSONObject.NULL.hashCode());
    }

    @Test
    public void testHas() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        assertTrue(object.has("foo"));
        assertFalse(object.has("bar"));
        assertFalse(object.has(null));
    }

    @Test
    public void testOptNull() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        assertNull(object.opt(null));
        assertFalse(object.optBoolean(null));
        assertEquals(Double.NaN, object.optDouble(null), 0);
        assertEquals(0, object.optInt(null));
        assertEquals(0L, object.optLong(null));
        assertNull(object.optJSONArray(null));
        assertNull(object.optJSONObject(null));
        assertEquals("", object.optString(null));
        assertTrue(object.optBoolean(null, true));
        assertEquals(0.0d, object.optDouble(null, 0.0d), 0);
        assertEquals(1, object.optInt(null, 1));
        assertEquals(1L, object.optLong(null, 1L));
        assertEquals("baz", object.optString(null, "baz"));
    }

    @Test
    public void testToStringWithIndentFactor() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", new JSONArray(Arrays.asList(5, 6)));
        object.put("bar", new JSONObject());
        String foobar = "{\n" +
                "     \"foo\": [\n" +
                "          5,\n" +
                "          6\n" +
                "     ],\n" +
                "     \"bar\": {}\n" +
                "}";
        String barfoo = "{\n" +
                "     \"bar\": {},\n" +
                "     \"foo\": [\n" +
                "          5,\n" +
                "          6\n" +
                "     ]\n" +
                "}";
        String string = object.toString(5);
        assertTrue(string, foobar.equals(string) || barfoo.equals(string));
    }

    @Test
    public void testToStringWithNulls() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("a", "A String")
            .put("n", 666)
            .put("null", null)
            .put("o", new JSONObject()
                    .put("b", "B String")
                    .put("null", null)
                    .put("bool", false));
        assertEquals("{\"a\":\"A String\",\"n\":666,\"o\":{\"b\":\"B String\",\"bool\":false}}", obj.toString());
    }

    @Test
    public void testToStringWithWrappedNulls() throws JSONException {
        JSONObject obj = new JSONObject(new Bean(100, "Test String", null));
        assertEquals("{\"i\":100,\"s\":\"Test String\"}", obj.toString());
    }

    /**
     * This test shows that toString from openjson is not compatible with org.json for null collections.
     * org.json prints empty array when openjson strips null regardless of a type.
     */
    @Test
    @SuppressWarnings("RedundantCast")
    public void testToStringWithNullCollection() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("c", ((Collection<?>)null));
        assertEquals("{}", obj.toString()); // org.json will output {"c":[]}
    }

    /**
     * This test shows that toString from openjson is not compatible with org.json for null collections.
     * org.json prints empty array when openjson strips null regardless of a type.
     */
    @Test
    @SuppressWarnings("RedundantCast")
    public void testToStringWithNullMap() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("m", ((Map<?,?>)null));
        assertEquals("{}", obj.toString()); // org.json will output {"m":{}}
    }

    @Test
    public void testNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("baz", 7);
        JSONArray array = object.names();
        assertTrue(array.toString().contains("foo"));
        assertTrue(array.toString().contains("bar"));
        assertTrue(array.toString().contains("baz"));
    }

    @Test
    public void testKeysEmptyObject() {
        JSONObject object = new JSONObject();
        assertFalse(object.keys().hasNext());
        try {
            object.keys().next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    @Test
    public void testKeys() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("foo", 7);

        Iterator<String> keys = object.keys();
        Set<String> result = new HashSet<String>();
        assertTrue(keys.hasNext());
        result.add(keys.next());
        assertTrue(keys.hasNext());
        result.add(keys.next());
        assertFalse(keys.hasNext());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "bar")), result);

        try {
            keys.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    @Test
    public void testMutatingKeysMutatesObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        Iterator<String> keys = object.keys();
        keys.next();
        keys.remove();
        assertEquals(0, object.length());
    }

    @Test
    public void testQuote() {
        // covered by JSONStringerTest.testEscaping
    }

    @Test
    public void testQuoteNull() throws JSONException {
        assertEquals("\"\"", JSONObject.quote(null));
    }

    @Test
    public void testNumberToString() throws JSONException {
        assertEquals("5", JSONObject.numberToString(5));
        assertEquals("-0", JSONObject.numberToString(-0.0d));
        assertEquals("9223372036854775806", JSONObject.numberToString(9223372036854775806L));
        assertEquals("4.9E-324", JSONObject.numberToString(Double.MIN_VALUE));
        assertEquals("1.7976931348623157E308", JSONObject.numberToString(Double.MAX_VALUE));
        try {
            JSONObject.numberToString(Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            JSONObject.numberToString(Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            JSONObject.numberToString(Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals("0.001", JSONObject.numberToString(new BigDecimal("0.001")));
        assertEquals("9223372036854775806",
                JSONObject.numberToString(new BigInteger("9223372036854775806")));
        try {
            JSONObject.numberToString(null);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void test_wrap() {
        assertEquals(JSONObject.NULL, JSONObject.wrap(null));

        JSONArray a = new JSONArray();
        assertEquals(a, JSONObject.wrap(a));

        JSONObject o = new JSONObject();
        assertEquals(o, JSONObject.wrap(o));

        assertEquals(JSONObject.NULL, JSONObject.wrap(JSONObject.NULL));

        assertTrue(JSONObject.wrap(new byte[0]) instanceof JSONArray);
        assertTrue(JSONObject.wrap(new ArrayList<String>()) instanceof JSONArray);
        assertTrue(JSONObject.wrap(new HashMap<String, String>()) instanceof JSONObject);
        assertTrue(JSONObject.wrap(new Object()) instanceof String);
        assertTrue(JSONObject.wrap(new Date()) instanceof String);
        assertTrue(JSONObject.wrap(new Bar()) instanceof JSONObject);

        assertTrue(JSONObject.wrap(true) instanceof Boolean);
        assertTrue(JSONObject.wrap((byte) 1) instanceof Byte);
        assertTrue(JSONObject.wrap('\0') instanceof Character);
        assertTrue(JSONObject.wrap(0.0D) instanceof Double);
        assertTrue(JSONObject.wrap(0.0F) instanceof Float);
        assertTrue(JSONObject.wrap(0) instanceof Integer);
        assertTrue(JSONObject.wrap(0L) instanceof Long);
        assertTrue(JSONObject.wrap((short) 0) instanceof Short);
        assertTrue(JSONObject.wrap("hello") instanceof String);

        assertNull(JSONObject.wrap(
                new Object() {
                    public String getX() {
                        throw new IllegalStateException("unsupported");
                    }
                }));
    }

    // https://code.google.com/p/android/issues/detail?id=55114
    @Test
    public void test_toString_listAsMapValue() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("a");
        list.add(new ArrayList<String>());
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put("x", "l");
        map.put("y", list);
        assertEquals("{\"x\":\"l\",\"y\":[\"a\",[]]}", new JSONObject(map).toString());
    }

    @Test
    public void testAppendExistingInvalidKey() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        try {
            object.append("foo", 6);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testAppendExistingArray() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("foo", array);
        object.append("foo", 5);
        assertEquals("[5]", array.toString());
    }

    @Test
    public void testAppendPutArray() throws JSONException {
        JSONObject object = new JSONObject();
        object.append("foo", 5);
        assertEquals("{\"foo\":[5]}", object.toString());
        object.append("foo", new JSONArray());
        assertEquals("{\"foo\":[5,[]]}", object.toString());
    }

    @Test
    public void testAppendNull() {
        JSONObject object = new JSONObject();
        try {
            object.append(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
    }

    // https://code.google.com/p/android/issues/detail?id=103641
    @Test
    public void testInvalidUnicodeEscape() {
        try {
            new JSONObject("{\"q\":\"\\u\", \"r\":[]}");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testBeanThings() {
        Foo f = new Foo();
        assertEquals("{\"a\":1,\"b\":1,\"c\":\"c\",\"d\":[{\"e\":\"echo\"}]}", new JSONObject(f).toString());
    }

    @Test(expected = JSONException.class)
    public void testBeanThingsWithErrors() {
        Foo f = new Foo() {
            @Override
            public double getA() {
                throw new IllegalStateException("Error!");
            }
        };
        new JSONObject(f);
    }

    @Test
    public void testGetNames() {
        assertArrayEquals(new String[]{"a", "b", "c", "d"}, JSONObject.getNames(new JSONObject(new Foo())));
    }

    private static class Foo {
        public double getA() {
            return 1.0;
        }

        public int getB() {
            return 1;
        }

        public String getC() {
            return "c";
        }

        public List<Bar> getD() {
            ArrayList<Bar> r = new ArrayList<Bar>();
            r.add(new Bar());
            return r;
        }
    }

    private static class Bar {
        public String getE() {
            return "echo";
        }
    }

    private static class Bean {
        private Integer i;
        private String s;
        private Object o;
        Bean() {}

        Bean(Integer i, String s, Object o) {
            this.i = i;
            this.s = s;
            this.o = o;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Object getO() {
            return o;
        }

        public void setO(Object o) {
            this.o = o;
        }
    }

    @Test
    public void testEnumWrapper() {
        Object y = JSONObject.wrap(E.A);
        assertEquals("A", y);
        assertTrue(y instanceof String);
    }

    enum E {
        A {
            @Override
            int key() {
                return 1;
            }
        }, B {
            @Override
            int key() {
                return 2;
            }
        };

        int key() {
            return -1;
        }
    }

    @Test
    public void testToString() {
        MyPojo1 myPojo1 = new MyPojo1();
        MyPojo2 myPojo2 = new MyPojo2();

        String json1 = new JSONObject(myPojo1).toString();
        String json2 = new JSONObject().put("myPojo2", myPojo2).toString();

        assertEquals("{\"myProp1\":\"value1\",\"myProp2\":\"value2\"}", json1);
        assertEquals("{\"myPojo2\":{\"myProp3\":\"value3\",\"myProp4\":\"value4\",\"myProp5\":\"value5\"}}", json2);
    }

    public static class MyPojo1 {
        private String myProp1 = "value1";
        private String myProp2 = "value2";

        public MyPojo1() {
        }

        public String getMyProp1() {
            return this.myProp1;
        }

        public String getMyProp2() {
            return this.myProp2;
        }
    }

    public static class MyPojo2 implements JSONString {
        private String myProp3 = "value3";
        private String myProp4 = "value4";

        public MyPojo2() {
        }

        public String getMyProp3() {
            return this.myProp3;
        }

        public String getMyProp4() {
            return this.myProp4;
        }

        @Override
        public String toJSONString() {
            JSONObject object = new JSONObject(JSONObject.objectAsMap(this));
            object.put("myProp5", "value5");
            return object.toString();
        }
    }

    @Test
    public void testArrayJSONString() {
        List<Object> myArray = Arrays.asList(new MyPojo1(), new MyPojo2());
        String expected = "[{\"myProp1\":\"value1\",\"myProp2\":\"value2\"},{\"myProp3\":\"value3\",\"myProp4\":\"value4\",\"myProp5\":\"value5\"}]";
        String json = new JSONArray(myArray).toString();
        assertEquals(expected, json);
    }
}
