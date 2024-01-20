import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Lemmatizer {

    protected StanfordCoreNLP pipeline;

    public Lemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, pos, lemma");

        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        pipeline = new StanfordCoreNLP(props);
    }

    public String lemmatize(String documentText) {
        CoreDocument document = pipeline.processToCoreDocument(documentText);

        List<String> lemmaList = new ArrayList<>();
        for (CoreLabel tok : document.tokens()) {
            lemmaList.add(tok.lemma().replace(",", "").replace("'s", "").replace(".", "").replace(":", ""));
        }

        return String.join(" ", lemmaList);
    }
}