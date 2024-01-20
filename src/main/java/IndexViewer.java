import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class IndexViewer {

    public static void main(String[] args) {
        String indexPath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\index";

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            boolean exists = false;
            int id = -1;
            MAIN_FOR: for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                List<IndexableField> fields = doc.getFields();
                System.out.println("Document " + i);
                for (IndexableField field : fields) {
                    if (!field.name().equals("content")) {
                        String fieldName = field.name();
                        String fieldValue = doc.get(fieldName);
                        System.out.println(fieldName + ": " + fieldValue);

                        if (fieldValue.equalsIgnoreCase("Komodo dragon")) {
                            exists = true;
                            id = i;
                            System.out.println(doc.get("content"));
                            break MAIN_FOR;
                        }
                    }
                }
                System.out.println("--------------");
            }
            reader.close();

            System.out.println("Exists: " + exists);
            System.out.println("ID: " + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}