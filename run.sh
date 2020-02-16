mvn clean
mvn package
java -cp target/app-1.0-SNAPSHOT.jar com.mycompany.app.IndexFiles -docs cran/cran.all.1400
java -cp target/app-1.0-SNAPSHOT.jar com.mycompany.app.SearchFiles -queries cran/cran.qry -score bm25
cd trec_eval-9.0.7
./trec_eval -m all_trec ../cran/QRelsCorrectedforTRECeval ../output.txt > ../results/results.txt