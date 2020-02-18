# Lucene Search Engine

## Building, Compiling, Creating the Index, Querying the Index and Using Trec-eval to compare scores.
```
git clone https://github.com/Donegaan/Lucene_Search_Engine.git
cd Lucene_Search_Engine/
./run.sh
```
In the run script you can change the score model flag `-score` between `bm25` or `vector`.

## Results
`results/standard-results.txt` is the results when using the StandardAnalyzer class.<br>
`results/standard-results-v2.txt` is the results when using the StandardAnalyzer class with version 2 of the `IndexFiles` class. This is the results I compare my custom analyzer against.<br>
`results/results.txt` gets updated every time the `run.sh` script is executed and uses my custom analyzer, the `MyAnalyzer` class. This is where the trec_eval results are saved.
