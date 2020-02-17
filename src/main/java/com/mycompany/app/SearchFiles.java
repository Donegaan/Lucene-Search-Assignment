/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycompany.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchFiles {

    private SearchFiles() {
    }

    /** Simple command-line based search demo. */
    public static void main(String[] args) throws Exception {
        String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        String index = "index";
        String queries = null;
        String scoreModel = "vector";
        int queryId = 1;

        for (int i = 0; i < args.length; i++) {
            if ("-score".equals(args[i])) {
                scoreModel = args[i + 1];
            } else if ("-queries".equals(args[i])) {
                queries = args[i + 1];
                i++;
            }
        }

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        Analyzer analyzer = new MyAnalyzer();

        // Choose Scoring model
        if (scoreModel.equals("bm25")) {
            searcher.setSimilarity(new BM25Similarity());
        } else if (scoreModel.equals("vector")) {
            searcher.setSimilarity(new ClassicSimilarity());
        }

        BufferedReader in = null;
        if (queries != null) {
            in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
        } else {
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }

        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                new String[] { "title", "author", "bibliography", "words" }, analyzer);

        String queryLines = "";
        Boolean first = true;
        String line = "";

        // Code credit:
        // https://github.com/nating/lucene-search-engine/blob/master/luceneapp/src/main/java/com/mycompany/luceneapp/SearchFiles.java
        while ((line = in.readLine()) != null) {

            if (line.substring(0, 2).equals(".I")) {
                if (!first) { // If you come across the next document, indicated by the line being ".I"
                    Query query = parser.parse(QueryParser.escape(queryLines));
                    doSearch(searcher, query, queryId, writer);
                    queryId++;
                } else {
                    first = false;
                }
                queryLines = "";
            } else {
                queryLines += " " + line;
            }
        }

        Query query = parser.parse(QueryParser.escape(queryLines));

        doSearch(searcher, query, queryId, writer);

        writer.close();
        reader.close();
    }

    // Code credit:
    // https://github.com/nating/lucene-search-engine/blob/master/luceneapp/src/main/java/com/mycompany/luceneapp/SearchFiles.java
    public static void doSearch(IndexSearcher searcher, Query query, Integer queryId, PrintWriter writer)
            throws IOException {
        TopDocs results = searcher.search(query, 1400);
        ScoreDoc[] hits = results.scoreDocs;

        // Write the results for each hit
        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            /*
             * Write the results in the format expected by trec_eval: | Query Number | 0 |
             * Document ID | Rank | Score | "EXP" |
             * (https://stackoverflow.com/questions/4275825/how-to-evaluate-a-search-
             * retrieval-engine-using-trec-eval)
             */
            writer.println(queryId + " 0 " + doc.get("path") + " " + i + " " + hits[i].score + " EXP");
        }
    }
}