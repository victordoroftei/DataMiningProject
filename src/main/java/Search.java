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

// Class used for querying the index
public class Search {

    private DirectoryReader reader;

    private IndexSearcher searcher;

    private QueryParser parser;

    // Initializing all fields
    public Search() {
        String indexPath = Indexer.ABSOLUTE_PATH + "\\index";

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

    // Method used for getting the results for a given query: will return all the documents until the answer is found
    // If the answer is not found, it will return the number of docs + 1
    private TopDocs getResultsForQuery(String clue) throws IOException, ParseException {
        // Normalize the clue
        clue = Indexer.normalize(clue);
        System.out.println("Normalized clue: " + clue);

        // Parse the clue
        Query query = parser.parse(clue);

        // Execute the search
        return searcher.search(query, reader.maxDoc());
    }

    // Method used for getting the rank for a given clue. Also places the titles of the documents in the given list.
    public Integer getRankForClue(String clue, String answer, List<String> titles) throws IOException, ParseException {
        TopDocs results = getResultsForQuery(clue);

        String[] answerSplitArr = answer.split("\\|");
        List<String> possibleAnswers = new ArrayList<>(Arrays.asList(answerSplitArr));
        possibleAnswers = possibleAnswers.stream().map(String::toLowerCase).collect(Collectors.toList());

        int i = 1;
        int rank = -1;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            String title = doc.get("filename");

            // If the answer is found, finish looping.
            if (possibleAnswers.contains(title.toLowerCase())) {
                rank = i;
                break;
            }

            i++;
        }

        // Extract the most relevant 5 documents (needed for prompting ChatGPT)
        i = 1;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            if (i > 5) {
                break;
            }

            Document doc = searcher.doc(scoreDoc.doc);
            String title = doc.get("filename");
            titles.add(title);

            i++;
        }

        if (rank == -1) {
            rank = reader.maxDoc() + 1; // In case we don't find any match, we'll set the rank to be the number of docs + 1.
        }

        return rank;
    }
}
