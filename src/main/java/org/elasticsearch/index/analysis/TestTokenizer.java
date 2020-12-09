package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.CharacterUtils;
import org.apache.lucene.analysis.CharacterUtils.CharacterBuffer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.util.HashSet;

public class TestTokenizer extends Tokenizer{


    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int DEFAULT_MAX_WORD_LEN = 255;
    public static final String DEFAULT_SEPERATORS = " \r\n\t";

    private int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;
    private HashSet<Integer> seperatorsSet = new HashSet<Integer>();
    private int maxTokenLen;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(DEFAULT_BUFFER_SIZE);

    public TestTokenizer(String seperators){
        this(DEFAULT_MAX_WORD_LEN, seperators);
    }

    public TestTokenizer(){
        this(DEFAULT_MAX_WORD_LEN, DEFAULT_SEPERATORS);
    }

    public TestTokenizer(int tokenLen, String seperators){
        super();
        this.maxTokenLen = tokenLen;
        constructSet(seperators);
    }



    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        int length = 0;
        int start = -1; // this variable is always initialized
        int end = -1;
        char[] buffer = termAtt.buffer();
        while (true) {
            if (bufferIndex >= dataLen) {
                offset += dataLen;
                CharacterUtils.fill(ioBuffer, input); // read supplementary char aware with CharacterUtils
                if (ioBuffer.getLength() == 0) {
                    dataLen = 0; // so next offset += dataLen won't decrement offset
                    if (length > 0) {
                        break;
                    } 
                    else {
                        finalOffset = correctOffset(offset);
                        return false;
                    }
                }
                dataLen = ioBuffer.getLength();
                bufferIndex = 0;
            }
            // use CharacterUtils here to support < 3.1 UTF-16 code unit behavior if the char based methods are gone
            final int c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());
            final int charCount = Character.charCount(c);
            bufferIndex += charCount;

            if (isTokenChar(c)) {               // if it's a token char
                if (length == 0) {                // start of token
                    assert start == -1;
                    start = offset + bufferIndex - charCount;
                    end = start;
                } 
                else if (length >= buffer.length-1) { // check if a supplementary could run out of bounds
                    buffer = termAtt.resizeBuffer(2+length); // make sure a supplementary fits in the buffer
                }
                end += charCount;
                length += Character.toChars(c, buffer, length); // buffer it, normalized
                if (length >= maxTokenLen) { // buffer overflow! make sure to check for >= surrogate pair could break == test
                    break;
                }
            } 
            else if (length > 0) {           // at non-Letter w/ chars
                break;                           // return 'em
            }
        }

        termAtt.setLength(length);
        assert start != -1;
        offsetAtt.setOffset(correctOffset(start), finalOffset = correctOffset(end));
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        bufferIndex = 0;
        offset = 0;
        dataLen = 0;
        finalOffset = 0;
        ioBuffer.reset(); // make sure to reset the IO buffer!!
    }


    private void constructSet(String seperators){
        seperators.codePoints().forEach(value->seperatorsSet.add(value));;
    }

    private boolean isTokenChar(int c){
        return !seperatorsSet.contains(c);
    }
}
