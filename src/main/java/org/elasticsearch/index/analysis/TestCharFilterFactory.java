package org.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import java.io.Reader;

public class TestCharFilterFactory extends AbstractCharFilterFactory implements NormalizingCharFilterFactory{
    

    private final String startString;
    private final String endString;
    private final int nth;

    public TestCharFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings){
        super(indexSettings, name);

        startString = settings.get("startString", "🦈");
        endString = settings.get("endString", "🦈");
        nth = settings.getAsInt("nth", 2);
    }

    public String getStartString() {
        return startString;
    }

    public String getEndString() {
        return endString;
    }

    @Override
    public Reader create(Reader tokenStream) {
        return new TestCharFilter(tokenStream, startString, endString, nth);
    }
}
