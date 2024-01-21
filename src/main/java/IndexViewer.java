import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// This class is used for viewing the documents in the index.
public class IndexViewer {

    public static Map<String, String> getDocumentContentsForTitles(List<String> titles) throws IOException {
        String indexPath = Indexer.ABSOLUTE_PATH + "\\index";
        Map<String, String> documentMap = new TreeMap<>();

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        MAIN_FOR:
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            List<IndexableField> fields = doc.getFields();

            for (IndexableField field : fields) {
                if (field.name().equals("filename")) {
                    if (titles.contains(doc.get("filename"))) {
                        documentMap.put(doc.get("filename"), doc.get("content"));
                        if (documentMap.keySet().size() == titles.size()) {
                            break MAIN_FOR;
                        }
                    }
                }
            }
        }
        reader.close();

        return documentMap;
    }
}