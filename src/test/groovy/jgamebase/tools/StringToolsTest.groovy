package jgamebase.tools

import spock.lang.Specification
import spock.lang.Unroll

import static org.apache.commons.lang.StringUtils.EMPTY

@Unroll
class StringToolsTest extends Specification {

    def 'nextMediumTest for #inputValue'() throws Exception {
        expect:
        StringTools.nextMedium(inputValue) == expectedValue

        where:
        inputValue   || expectedValue
        null         || EMPTY
        EMPTY        || EMPTY
        ".zip"       || EMPTY
        "a.zip"      || EMPTY
        "test_a.zip" || "test_b.zip"
        "test_1.zip" || "test_2.zip"
    }
}
