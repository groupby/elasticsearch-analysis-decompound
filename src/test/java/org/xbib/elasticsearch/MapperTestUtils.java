package org.xbib.elasticsearch;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.IndexAnalyzers;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.index.mapper.DocumentMapperParser;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.similarity.SimilarityService;
import org.elasticsearch.indices.IndicesModule;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.indices.mapper.MapperRegistry;
import org.elasticsearch.script.ScriptService;
import org.xbib.elasticsearch.plugin.analysis.decompound.AnalysisDecompoundPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class MapperTestUtils {

    public static AnalysisRegistry analysisService(Settings customSettings) throws IOException {
        Settings settings = Settings.builder()
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put(customSettings)
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisDecompoundPlugin plugin = new AnalysisDecompoundPlugin();
        AnalysisModule analysisModule = new AnalysisModule(environment, Collections.singletonList(plugin));
        return analysisModule.getAnalysisRegistry();
    }

    public static DocumentMapperParser newDocumentMapperParser(String index) throws IOException {
        return newDocumentMapperParser(Settings.EMPTY, index);
    }

    public static DocumentMapperParser newDocumentMapperParser(Settings customSettings, String index) throws IOException {
        Settings settings = Settings.builder()
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put(customSettings)
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisDecompoundPlugin plugin = new AnalysisDecompoundPlugin();
        AnalysisModule analysisModule = new AnalysisModule(environment, Collections.singletonList(plugin));
        IndicesModule indicesModule = new IndicesModule(Collections.emptyList());
        MapperRegistry mapperRegistry = indicesModule.getMapperRegistry();
        AnalysisRegistry analysisRegistry = analysisModule.getAnalysisRegistry();
        IndexMetaData indexMetaData = IndexMetaData.builder(index)
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        ScriptService scriptService = new ScriptService(settings, Collections.emptyMap(), Collections.emptyMap());
        SimilarityService similarityService = new SimilarityService(indexSettings, scriptService, SimilarityService.BUILT_IN);
        Map<String, CharFilterFactory> charFilterFactoryMap = analysisRegistry.buildCharFilterFactories(indexSettings);
        Map<String, TokenFilterFactory> tokenFilterFactoryMap = analysisRegistry.buildTokenFilterFactories(indexSettings);
        Map<String, TokenizerFactory> tokenizerFactoryMap = analysisRegistry.buildTokenizerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> analyzerProviderMap = analysisRegistry.buildAnalyzerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> normalizerProviderMap = analysisRegistry.buildNormalizerFactories(indexSettings);
        IndexAnalyzers indexAnalyzers = analysisRegistry.build(indexSettings,
                analyzerProviderMap,
                normalizerProviderMap,
                tokenizerFactoryMap,
                charFilterFactoryMap,
                tokenFilterFactoryMap);
        MapperService mapperService = new MapperService(indexSettings, indexAnalyzers, NamedXContentRegistry.EMPTY,
                similarityService, mapperRegistry, null);
        return new DocumentMapperParser(indexSettings, mapperService, indexAnalyzers, NamedXContentRegistry.EMPTY,
                similarityService, mapperRegistry, null);
    }

    public static Analyzer analyzer(String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home"))
                .build();
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, CharFilterFactory> charFilterFactoryMap = analysisRegistry.buildCharFilterFactories(indexSettings);
        Map<String, TokenFilterFactory> tokenFilterFactoryMap = analysisRegistry.buildTokenFilterFactories(indexSettings);
        Map<String, TokenizerFactory> tokenizerFactoryMap = analysisRegistry.buildTokenizerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> analyzerProviderMap = analysisRegistry.buildAnalyzerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> normalizerProviderMap = analysisRegistry.buildNormalizerFactories(indexSettings);
        IndexAnalyzers indexAnalyzers = analysisRegistry.build(indexSettings,
                analyzerProviderMap,
                normalizerProviderMap,
                tokenizerFactoryMap,
                charFilterFactoryMap,
                tokenFilterFactoryMap);
        Analyzer analyzer = indexAnalyzers.get(name) != null ? indexAnalyzers.get(name) : analysisRegistry.getAnalyzer(name);
        assertNotNull(analyzer);
        return analyzer;
    }

    public static Analyzer analyzer(Settings customSettings, String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .put(customSettings)
                .build();
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, CharFilterFactory> charFilterFactoryMap = analysisRegistry.buildCharFilterFactories(indexSettings);
        Map<String, TokenFilterFactory> tokenFilterFactoryMap = analysisRegistry.buildTokenFilterFactories(indexSettings);
        Map<String, TokenizerFactory> tokenizerFactoryMap = analysisRegistry.buildTokenizerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> analyzerProviderMap = analysisRegistry.buildAnalyzerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> normalizerProviderMap = analysisRegistry.buildNormalizerFactories(indexSettings);
        IndexAnalyzers indexAnalyzers = analysisRegistry.build(indexSettings,
                analyzerProviderMap,
                normalizerProviderMap,
                tokenizerFactoryMap,
                charFilterFactoryMap,
                tokenFilterFactoryMap);
        Analyzer analyzer = indexAnalyzers.get(name) != null ? indexAnalyzers.get(name) : analysisRegistry.getAnalyzer(name);
        assertNotNull(analyzer);
        return analyzer;
    }

    public static Analyzer analyzer(ClassLoader classLoader, String resource, String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .loadFromStream(resource, classLoader.getResourceAsStream(resource), false)
                .build();
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, CharFilterFactory> charFilterFactoryMap = analysisRegistry.buildCharFilterFactories(indexSettings);
        Map<String, TokenFilterFactory> tokenFilterFactoryMap = analysisRegistry.buildTokenFilterFactories(indexSettings);
        Map<String, TokenizerFactory> tokenizerFactoryMap = analysisRegistry.buildTokenizerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> analyzerProviderMap = analysisRegistry.buildAnalyzerFactories(indexSettings);
        Map<String, AnalyzerProvider<?>> normalizerProviderMap = analysisRegistry.buildNormalizerFactories(indexSettings);
        IndexAnalyzers indexAnalyzers = analysisRegistry.build(indexSettings,
                analyzerProviderMap,
                normalizerProviderMap,
                tokenizerFactoryMap,
                charFilterFactoryMap,
                tokenFilterFactoryMap);
        Analyzer analyzer = indexAnalyzers.get(name) != null ? indexAnalyzers.get(name) : analysisRegistry.getAnalyzer(name);
        assertNotNull(analyzer);
        return analyzer;
    }

    public static TokenizerFactory tokenizerFactory(String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .build();
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, TokenizerFactory> map = analysisRegistry.buildTokenizerFactories(indexSettings);
        TokenizerFactory tokenizerFactory = map.containsKey(name) ? map.get(name) :
                analysisRegistry.getTokenizerProvider(name).get(new Environment(settings, null), name);
        assertNotNull(tokenizerFactory);
        return tokenizerFactory;
    }

    public static TokenizerFactory tokenizerFactory(String resource, String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home"))
                .loadFromStream(resource, MapperTestUtils.class.getClassLoader().getResource(resource).openStream(), false)
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, TokenizerFactory> map = analysisRegistry.buildTokenizerFactories(indexSettings);
        TokenizerFactory tokenizerFactory = map.containsKey(name) ? map.get(name) :
                analysisRegistry.getTokenizerProvider(name).get(environment, name);
        assertNotNull(tokenizerFactory);
        return tokenizerFactory;
    }

    public static TokenFilterFactory tokenFilterFactory(String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, TokenFilterFactory> map = analysisRegistry.buildTokenFilterFactories(indexSettings);
        return map.containsKey(name) ? map.get(name) :
                analysisRegistry.getTokenFilterProvider(name).get(environment, name);
    }

    public static TokenFilterFactory tokenFilterFactory(ClassLoader classLoader, String resource, String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .loadFromStream(resource, classLoader.getResourceAsStream(resource), false)
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, TokenFilterFactory> map = analysisRegistry.buildTokenFilterFactories(indexSettings);
        return map.containsKey(name) ? map.get(name) :
                analysisRegistry.getTokenFilterProvider(name).get(environment, name);
    }

    public static CharFilterFactory charFilterFactory(String resource, String name) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .loadFromStream(resource, MapperTestUtils.class.getClassLoader().getResource(resource).openStream(), false)
                .build();
        Environment environment = new Environment(settings, null);
        AnalysisRegistry analysisRegistry = analysisService(settings);
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        IndexSettings indexSettings = new IndexSettings(indexMetaData, settings);
        Map<String, CharFilterFactory> map = analysisRegistry.buildCharFilterFactories(indexSettings);
        return map.containsKey(name) ? map.get(name) :
                analysisRegistry.getCharFilterProvider(name).get(environment, name);
    }
}
