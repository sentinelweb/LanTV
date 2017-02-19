package co.uk.sentinelweb.lantv.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 18/02/2017.
 */
public class StringUtilsTest {
    final String[] testArray = new String[]{"a","b","c","d","e"};
    final List<String> testList = Arrays.asList(testArray);
    @Test
    public void concat() throws Exception {
        assertEquals("a/b/c/d/e", StringUtils.concat(testList,"/"));
    }

    @Test
    public void concatWithIndex() throws Exception {
        assertEquals("a/b/c/d/e", StringUtils.concat(testList,"/", 0, 0));
        assertEquals("a/b/c/d", StringUtils.concat(testList,"/", 0, -1));
        assertEquals("a/b/c/d", StringUtils.concat(testList,"/", 0, testList.size()-1));
        assertEquals("b/c/d/e", StringUtils.concat(testList,"/", 1, 0));
        assertEquals("b/c/d", StringUtils.concat(testList,"/", 1, -1));
        assertEquals("b/c/d", StringUtils.concat(testList,"/", 1, testList.size()-1));
        assertEquals("c/d", StringUtils.concat(testList,"/", 2, -1));
    }

    @Test
    public void concatArray() throws Exception {
        assertEquals("a/b/c/d/e", StringUtils.concat(testArray,"/"));
    }

    @Test
    public void concatArrayWithIndex() throws Exception {
        assertEquals("a/b/c/d/e", StringUtils.concat(testArray,"/", 0, 0));
        assertEquals("a/b/c/d", StringUtils.concat(testArray,"/", 0, -1));
        assertEquals("a/b/c/d", StringUtils.concat(testArray,"/", 0, testArray.length-1));
        assertEquals("b/c/d/e", StringUtils.concat(testArray,"/", 1, 0));
        assertEquals("b/c/d", StringUtils.concat(testArray,"/", 1, -1));
        assertEquals("b/c/d", StringUtils.concat(testArray,"/", 1, testArray.length-1));
        assertEquals("c/d", StringUtils.concat(testArray,"/", 2, -1));
    }

}