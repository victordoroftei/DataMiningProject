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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Search {

    private DirectoryReader reader;

    private IndexSearcher searcher;

    private QueryParser parser;

    public Search() {
        String indexPath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\index";

        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        } catch (IOException e) {
            System.err.println("Could not open index directory!");
            e.printStackTrace();
        }

        searcher = new IndexSearcher(reader);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        parser = new QueryParser("content", analyzer);
    }

    private TopDocs getResultsForQuery(String clue) throws IOException, ParseException {
        clue = Indexer.normalize(clue);
        //clue = Indexer.normalizeClue(clue);
        System.out.println("Normalized clue: " + clue);

        Query query = parser.parse(clue);

        // Executing the search
        return searcher.search(query, reader.maxDoc());
    }

    public Integer getRankForClue(String clue, String answer) throws IOException, ParseException {
        TopDocs results = getResultsForQuery(clue);

        String[] answerSplitArr = answer.split("\\|");
        List<String> possibleAnswers = new ArrayList<>(Arrays.asList(answerSplitArr));
        possibleAnswers = possibleAnswers.stream().map(String::toLowerCase) .collect(Collectors.toList());

        int i = 1;
        int rank = -1;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            //System.out.println("Document ID: " + scoreDoc.doc + ", Score: " + scoreDoc.score + ", Rank: " + i);

            String title = doc.get("filename");
            //System.out.println(title);

            if (possibleAnswers.contains(title.toLowerCase())) {
                rank = i;
                break;
            }

            i++;
        }

        if (rank == -1) {
            rank = reader.maxDoc() + 1; // In case we don't find any match, we'll set the rank to be the number of docs + 1.
        }

        // reader.close();

        return rank;
    }

//    public static void main(String[] args) throws IOException, ParseException {
//        // Path to the index directory
//        String indexPath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\index";
//
//        // Opening the index
//        reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//
//        // Creating the IndexSearcher
//        searcher = new IndexSearcher(reader);
//
//        // Using the same analyzer that was used for indexing
//        StandardAnalyzer analyzer = new StandardAnalyzer();
//
//        // Parsing the query
//        // Assuming we are searching in the "content" field
//        parser = new QueryParser("content", analyzer);
//
//        System.out.println(getResultsForQuery("Suceava"));
//    }
}
