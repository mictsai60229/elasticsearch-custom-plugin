package org.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class TestAnalyzerProvider extends AbstractIndexAnalyzerProvider<TestAnalyzer> {
    

    private final TestAnalyzer analyzer;

    public TestAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        int nth = settings.getAsInt("nth", 2);
        String seperators = settings.get("seperators", " \t\r\n");
        String startString = settings.get("startString", "🦈");
        String endString = settings.get("startString", "🦈");

        analyzer = new TestAnalyzer(nth, startString, endString, seperators);
    }

    @Override
    public TestAnalyzer get() {
        return this.analyzer;
    }
}
