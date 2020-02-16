package com.mycompany.app;

// Misc Lucene imports
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;

public class MyAnalyzer extends Analyzer {

  protected TokenStreamComponents createComponents(String s) {

    // --------------------- Select a tokenizer --------------------

    // StandardTokenizer - “full” “text” “lucene.apache.org”
    // StandardTokenizer tokenizer = new StandardTokenizer();

    // WhitespaceTokenizer - “full-text” “lucene.apache.org”
    // WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

    // LetterTokenizer - “full” “text” “lucene” “apache” “org”
    LetterTokenizer tokenizer = new LetterTokenizer();

    // --------------------- Create the token stream --------------------

    TokenStream tokenstream = new ClassicFilter(tokenizer);

    // ------------------ Filter the token stream ------------------

    // LowerCaseFilter - Convert to lowercase
    // tokenstream = new LowerCaseFilter(tokenstream);

    // StopFilter - Remove stopwords
    tokenstream = new StopFilter(tokenstream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

    // LengthFilter - Remove words that are too long or too short
    tokenstream = new LengthFilter(tokenstream, 2, 20);

    // PorterStemFilter - Implement stemming
    tokenstream = new PorterStemFilter(tokenstream);

    // ----------- Return the components of the token stream --------------

    return new TokenStreamComponents(tokenizer, tokenstream);
  }

}