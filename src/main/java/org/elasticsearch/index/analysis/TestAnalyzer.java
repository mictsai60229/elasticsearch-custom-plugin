package org.elasticsearch.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;

public class TestAnalyzer extends Analyzer{
    
    private int nth;
    private String seperators;
    private String startString;
    private String endString;

    public TestAnalyzer(int nth, String startString, String endString, String seperators) {
        this.nth = nth;
        this.seperators = seperators;
        this.startString = startString;
        this.endString = endString;
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader){
        return new TestCharFilter(reader, startString, endString, nth);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) { 
        
        final Tokenizer source = new TestTokenizer(seperators);
        return new TokenStreamComponents(source, new TestTokenFilter(source));
    }

    @Override
    protected TokenStream normalize(String filedName, TokenStream input){
        return new TestTokenFilter(input);
    }
}
