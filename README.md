# Lucene Search Engine

## Building, Compiling, Creating the Index, Querying the Index and Using Trec-eval to compare scores.
```
git clone https://github.com/Donegaan/Lucene_Search_Engine.git
cd Lucene_Search_Engine/
mvn clean
mvn package
java -cp target/app-1.0-SNAPSHOT.jar com.mycompany.app.IndexFiles -docs cran/cran.all.1400
java -cp target/app-1.0-SNAPSHOT.jar com.mycompany.app.SearchFiles -queries cran/cran.qry -model 1
# The “-model” flag indicates the type of scoring you wish to use: 0 is vector space (classic) and 1 is BM25: usage "-model 1".
# The SearchFiles stores the scoring -output.txt in cran/outputs.txt
cd trec_eval-9.0.7
./trec_eval ../cran/QRelsCorrectedforTRECeval ../cran/outputs.txt
```
