package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.charfilter.BaseCharFilter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TestCharFilter extends BaseCharFilter{
    private static final String DEFAULT_PADDING_STRING = "🦈";
    private static final int NTH = 2;
    
    private String startString;
    private String endString;
    private int nth;
    private Reader transformedInput;
    
    public TestCharFilter(Reader input, String startString, String endString, int nth) {
        super(input);
        this.startString = startString;
        this.endString = endString;
        this.nth = nth;
    }

    public TestCharFilter(Reader input){
        this(input, DEFAULT_PADDING_STRING, DEFAULT_PADDING_STRING, NTH);
    }


    public int read(char[] cbuf, int off, int len) throws IOException {
        if(this.transformedInput == null) {
            this.fill();
        }

        return this.transformedInput.read(cbuf, off, len);
    }

    private void fill() throws IOException {
        StringBuilder buffered = new StringBuilder();
        char[] temp = new char[1024];

        for (int i=0;i<this.nth;i++){
            buffered.append(this.startString);
            buffered.append(" ");
        }
        
        for(int cnt = this.input.read(temp); cnt > 0; cnt = this.input.read(temp)) {
            buffered.append(temp, 0, cnt);
        }
        for (int i=0;i<this.nth;i++){
            buffered.append(" ");
            buffered.append(this.endString);
        }

        this.transformedInput = new StringReader(buffered.toString());
    }

    public int read() throws IOException {
        if(this.transformedInput == null) {
            this.fill();
        }

        return this.transformedInput.read();
    }

    protected int correct(int currentOff) {
        return Math.max(0, super.correct(currentOff));
    }
}
