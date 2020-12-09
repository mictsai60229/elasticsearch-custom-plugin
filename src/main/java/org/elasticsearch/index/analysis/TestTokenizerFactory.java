package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class TestTokenizerFactory extends AbstractTokenizerFactory{
    
    private int maxLength;
    private String seperators;

    public TestTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, settings, name);
        maxLength = settings.getAsInt("maxLength", 255);
        seperators = settings.get("seperators", " \r\n\t");
    }

    @Override
    public Tokenizer create() {
        return new TestTokenizer(maxLength, seperators);
    }
}
