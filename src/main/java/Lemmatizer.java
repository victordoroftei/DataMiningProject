import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Class used for lemmatizing text, using the StanfordCoreNLP library.
public class Lemmatizer {

    protected StanfordCoreNLP pipeline;

    public Lemmatizer() {
        // Create StanfordCoreNLP object properties, with the following annotations which are required for lemmatization:
        // "tokenize", "pos" (part of speech) and "lemma".
        Properties props = new Properties();
        props.put("annotators", "tokenize, pos, lemma");

        pipeline = new StanfordCoreNLP(props);
    }

    // Method used for lemmatizing the given text
    public String lemmatize(String documentText) {
        CoreDocument document = pipeline.processToCoreDocument(documentText);

        List<String> lemmaList = new ArrayList<>();
        for (CoreLabel tok : document.tokens()) {
            // Remove residual characters
            lemmaList.add(tok.lemma().replace(",", "").replace("'s", "").replace(".", "").replace(":", ""));
        }

        // Create a String from the list of lemmatized components
        return String.join(" ", lemmaList);
    }
}