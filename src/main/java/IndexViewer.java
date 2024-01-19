import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IndexViewer {

    public static void main(String[] args) {
        String indexPath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\index";

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

            List<String> titles = new ArrayList<>();
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                List<IndexableField> fields = doc.getFields();
                System.out.println("Document " + i);
                for (IndexableField field : fields) {
                    //if (!field.name().equals("content")) {
                        String fieldName = field.name();
                        String fieldValue = doc.get(fieldName);
                        System.out.println(fieldName + ": " + fieldValue);

                        titles.add(fieldValue);
                    //}
                }
                System.out.println("--------------");
            }
            reader.close();

            titles = titles.stream().sorted().collect(Collectors.toList());
            System.out.println(titles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}