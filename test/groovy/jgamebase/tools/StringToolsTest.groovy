package jgamebase.tools

import spock.lang.Specification
import spock.lang.Unroll

import static org.apache.commons.lang.StringUtils.EMPTY
import static spock.util.matcher.HamcrestMatchers.closeTo

@Unroll
class StringToolsTest extends Specification {

    def 'beforeChar(#input) == #result'() {
        expect:
        StringTools.beforeChar(input, ' ' as char) == result

        where:
        input      || result
        null       || EMPTY
        EMPTY      || EMPTY
        'a'        || EMPTY
        //'a '       || EMPTY //TODO: BUG?
        ' ab a'    || 'ab'
        'ab ac ad' || 'ab'
        'a b'      || 'a'
    }

    def 'afterChar(#input) == #result'() {
        expect:
        StringTools.afterChar(input, ' ' as char) == result

        where:
        input      || result
        null       || EMPTY
        EMPTY      || EMPTY
        'a'        || EMPTY
        'a '       || EMPTY
        ' ab a'    || 'a'
        'ab ac ad' || 'ac ad'
        'a b'      || 'b'
    }

    def 'padZeroBefore(#input, #length) == #result'() {
        expect:
        StringTools.padZeroBefore(input, length) == result

        where:
        input || length || result
        null  || 1      || EMPTY
        EMPTY || 1      || '0'
        'a'   || 1      || 'a'
        'a'   || 2      || '0a'
    }

    def 'firstCharAsString(#input) == #result'() {
        expect:
        StringTools.firstCharAsString(input) == result

        where:
        input || result
        null  || EMPTY
        EMPTY || EMPTY
        'a'   || 'a'
        'ab'  || 'a'
    }

    def 'lastCharAsString(#input) == #result'() {
        expect:
        StringTools.lastCharAsString(input) == result

        where:
        input || result
        null  || EMPTY
        EMPTY || EMPTY
        'a'   || 'a'
        'ab'  || 'b'
    }

    def 'capitalize(#input) == #result'() {
        expect:
        StringTools.capitalize(input) == result

        where:
        input   || result
        null    || null
        EMPTY   || EMPTY
        'a'     || 'A'
        'ab'    || 'Ab'
        'ab_CD' || 'Ab_Cd'
        'aB CD' || 'Ab Cd'
    }

    def 'htmlDecode(#input) == #result'() {
        expect:
        StringTools.htmlDecode(input) == result

        where:
        input             || result
        EMPTY             || EMPTY
        '<test>a</test>'  || 'a'
        '<test>a</testv>' || 'a'
    }

    def 'htmlEncode(#input) == #result'() {
        expect:
        StringTools.htmlEncode(input) == result

        where:
        input    || result
        null     || EMPTY
        EMPTY    || EMPTY
        '<>&"\'' || '&lt;&gt;&amp;&quot;&apos;'
        'ab'     || 'ab'
    }

    def 'startWithUpperCase(#input) == #result'() {
        expect:
        StringTools.startWithUpperCase(input) == result

        where:
        input || result
        // EMPTY || EMPTY // TODO: BUG?
        'ab'  || 'Ab'
        'AA'  || 'AA'
        ' AA' || ' AA'
    }

    def 'nextMediumTest() for #inputValue'() throws Exception {
        expect:
        StringTools.nextMedium(inputValue) == expectedValue

        where:
        inputValue   || expectedValue
        null         || EMPTY
        EMPTY        || EMPTY
        '.zip'       || EMPTY
        'a.zip'      || EMPTY
        'test_a.zip' || 'test_b.zip'
        'test_1.zip' || 'test_2.zip'
    }

    def 'getStringSimilarity() for "#string1" and "#string2"'() {
        expect:
        def out = StringTools.getStringSimilarity(string1, string2)
        out closeTo(result, 0.001d)

        where:
        string1 || string2 || result
        'book'  || 'book'  || 1
        'abc'   || 'abd'   || 2 / 3
        'book'  || 'back'  || 0.5
        'abc'   || 'axx'   || 1 / 3
        'abc'   || 'xyz'   || 0
        EMPTY   || 'abc'   || 0
        'abc'   || EMPTY   || 0
    }

    def 'countLowerCase(#input) == #result'() {
        expect:
        StringTools.countLowerCase(input) == result

        where:
        input || result
        null  || 0
        EMPTY || 0
        'abc' || 3
        'aBc' || 2
        'BaB' || 1
        'AAA' || 0
    }

    def 'countUpperCase(#input) == #result'() {
        expect:
        StringTools.countUpperCase(input) == result

        where:
        input || result
        null  || 0
        EMPTY || 0
        'abc' || 0
        'aBc' || 1
        'BaB' || 2
        'AAA' || 3
    }

    def 'countWhitespace(#input) == #result'() {
        expect:
        StringTools.countWhitespace(input) == result

        where:
        input  || result
        null   || 0
        EMPTY  || 0
        ' '    || 1
        'a  c' || 2
        ' a '  || 2
        'a'    || 0
    }

    def 'isCamelCase(#input) == #result'() {
        expect:
        StringTools.isCamelCase(input) == result

        where:
        input    || result
        null     || false
        EMPTY    || false
        ' '      || false
        'a  c'   || false
        'testMe' || true
        'ASD'    || false
    }

    def 'deCamelCase(#input) == #result'() {
        expect:
        StringTools.deCamelCase(input) == result

        where:
        input    || result
        null     || null
        EMPTY    || EMPTY
        ' '      || ' '
        'a c'    || 'a c'
        'testMe' || 'test Me'
        'ASD'    || 'ASD'
    }
}
