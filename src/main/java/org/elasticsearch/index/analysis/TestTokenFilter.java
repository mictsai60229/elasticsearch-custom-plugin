package org.elasticsearch.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.lang.Character;

public class TestTokenFilter extends TokenFilter {
    
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    

    public TestTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()){
            return false;
        }

        int length = termAtt.length();
        char[] buffer = termAtt.buffer();
        for(int i=0;i<length;i++){
            buffer[i] = Character.toLowerCase(buffer[i]);
        }
        termAtt.setEmpty();
        termAtt.copyBuffer(buffer, 0, length);

        return true;
    }

}
