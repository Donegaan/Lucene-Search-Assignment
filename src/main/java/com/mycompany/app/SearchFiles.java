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
        String queryString = null;
        String queryLines = "";
        int hitsPerPage = 1400;

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

        // Some code from:
        // https://github.com/dywalsh/ApacheLucene-Search-Engine/blob/master/my-app/src/main/java/com/mycompany/app/SearchFiles.java
        String line = in.readLine();
        while (line != null) {

            if (line.substring(0, 2).equals(".I")) { // Detect new query
                line = in.readLine();
                queryLines = "";
                if (line.substring(0, 2).equals(".W")) {
                    line = in.readLine();
                }
                while (!line.substring(0, 2).equals(".I")) {
                    queryLines += " " + line; // Concatenate all of the query lines
                    line = in.readLine();
                    if (line == null) {
                        break;
                    }
                }
            }
            Query query = parser.parse(QueryParser.escape(queryLines)); // Create query from

            doPagingSearch(searcher, query, writer, queryId, hitsPerPage, queries == null && queryString == null);
            queryId++;
        }

        writer.close();
        reader.close();
    }

    // Some code credit:
    // https://github.com/nating/lucene-search-engine/blob/master/luceneapp/src/main/java/com/mycompany/luceneapp/SearchFiles.java

    public static void doPagingSearch(IndexSearcher searcher, Query query, PrintWriter writer, int queryId,
            int hitsPerPage, boolean interactive) throws IOException {

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = Math.toIntExact(results.totalHits.value);
        System.out.println(numTotalHits + " total matching documents");

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        while (true) {
            end = Math.min(hits.length, start + hitsPerPage);
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                writer.println(queryId + " 0 " + doc.get("path") + " " + i + " " + hits[i].score + " EXP");
            }
            if (!interactive || end == 0) {
                break;
            }
        }
    }

}