import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.lucene.analysis.en.EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;

// The class used for creating the index
public class Indexer {

    private static final Lemmatizer lemmatizer = new Lemmatizer();

    // Method used for normalizing the documents before the indexing is performed.
    // This method is also used for normalizing the clues and categories.
    public static String normalize(String input) throws IOException {
        // Convert the input to lowercase
        input = input.toLowerCase();

        // Create a standard tokenizer
        StandardTokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(input));

        // Create a stop filter, using the English stop words set (more details in the documentation)
        TokenStream tokenStream = new StopFilter(tokenizer, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

        // Extract the tokens after stop words removal
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        String result = "";
        while (tokenStream.incrementToken()) {
            result += charTermAttribute.toString() + " ";
        }

        tokenStream.end();
        tokenStream.close();

        // Lemmatize the processed text
        result = lemmatizer.lemmatize(result);

        return result;
    }

    public static void main(String[] args) throws IOException {
        Path indexPath = Paths.get("E:\\__Teme\\Data Mining (DM)\\testLucene1\\index");
        FSDirectory directory = FSDirectory.open(indexPath);

        // Create an analyzer and the index writer config
        Analyzer analyzer = new StopAnalyzer(ENGLISH_STOP_WORDS_SET);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        int totalNoWikiPages = 0;
        List<String> titles = new ArrayList<>();

        // Create the IndexWriter
        try (IndexWriter writer = new IndexWriter(directory, config)) {

            // Loop through the Wikipedia files (named wiki01.txt through wiki80.txt)
            for (int i = 1; i <= 80; i++) {
                String iStr = String.format("%1$" + 2 + "s", i).replace(' ', '0');
                String fileName = "wiki" + iStr + ".txt";
                System.out.println(fileName);

                Path filePath = Paths.get("E:\\__Teme\\Data Mining (DM)\\testLucene1", fileName);

                // Read the content of the file
                String content = new String(Files.readAllBytes(filePath));

                // Perform RegEx operations on the contents of the files (more details in the documentation).
                content = content.replaceAll("\\[\\[File:.*]]]]", "");
                content = content.replaceAll("\\[\\[File:.*]]", "");

                content = content.replaceAll("\\[\\[Image:.*]]]]", "");
                content = content.replaceAll("\\[\\[Image:.*]]", "");

                content = content.replaceAll("\\[ref].*\\[/ref]", "");
                content = content.replaceAll("\\[tpl].*\\[/tpl]", "");

                // This pattern will be used for identifying titles and breaking down into separate Wiki articles
                Pattern pattern = Pattern.compile("[^=]\\[\\[.+]]");
                Matcher matcher = pattern.matcher(content);

                List<String> result = new ArrayList<>();
                int start = 0;

                // Find separate Wiki articles
                while (matcher.find()) {
                    if (matcher.start() > start) {
                        result.add(content.substring(start, matcher.start()));
                    }

                    start = matcher.start();
                }

                if (start < content.length()) {
                    result.add(content.substring(start));
                }

                System.out.println(result.size());

                for (String string : result) {
                    // Get title indices
                    int indexStart = string.indexOf('[') + 2;
                    int indexEnd = string.indexOf(']');

                    String title;
                    if ((indexStart != 1 && indexEnd != -1) && (indexStart <= indexEnd)) {
                        title = string.substring(indexStart, indexEnd);
                    } else {
                        title = "TITLE COULD NOT BE DETERMINED";    // In case the title cannot be determined (bad input)
                    }

                    titles.add(title);

                    // Normalize the content of the Wikipedia article
                    String normalizedString = normalize(string);

                    // Create a new document
                    Document document = new Document();
                    document.add(new TextField("content", normalizedString, Field.Store.YES));
                    document.add(new StringField("filename", title, Field.Store.YES));

                    // Add the document to the index
                    writer.addDocument(document);
                }

                totalNoWikiPages += result.size();
            }

            writer.commit();
        }

        System.out.println("\nTotal number of wiki pages: " + totalNoWikiPages);
        System.out.println(titles);
    }
}