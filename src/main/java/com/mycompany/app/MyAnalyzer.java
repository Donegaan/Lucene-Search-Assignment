package com.mycompany.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;

public class MyAnalyzer extends Analyzer {

  // Some Code credit:
  // https://github.com/nating/lucene-search-engine/blob/master/luceneapp/src/main/java/com/mycompany/luceneapp/CustomAnalyzer.java
  // and https://www.baeldung.com/lucene-analyzers
  protected TokenStreamComponents createComponents(String s) {

    LetterTokenizer tokenizer = new LetterTokenizer();

    TokenStream tokenstream = new StopFilter(tokenizer, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

    tokenstream = new PorterStemFilter(tokenstream);

    return new TokenStreamComponents(tokenizer, tokenstream);
  }

}