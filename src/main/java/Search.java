import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.ScoreDoc;

import java.nio.file.Paths;
import java.io.IOException;

public class Search {

    public static void main(String[] args) throws ParseException, IOException {
        // Path to the index directory
        String indexPath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\index";

        // Opening the index
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

        // Creating the IndexSearcher
        IndexSearcher searcher = new IndexSearcher(reader);

        // Using the same analyzer that was used for indexing
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // Parsing the query
        // Assuming we are searching in the "content" field
        QueryParser parser = new QueryParser("content", analyzer);

        String input = Indexer.normalize("News flash! This less-than-yappy pappy is sixth veep to be nation's top dog after chief takes deep sleep!");
        System.out.println(input);

        Query query = parser.parse(input);

        // Executing the search
        TopDocs results = searcher.search(query, 10000); // Searching for top 20 results

        // Processing the search results
        int i = 0;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Document ID: " + scoreDoc.doc + ", Score: " + scoreDoc.score + ", Rank: " + i);
            System.out.println(doc.get("filename")); // Assuming you have a field named "filename"
            // You can retrieve and display other fields similarly
            i++;
        }

        // Closing the reader
        reader.close();
    }
}
