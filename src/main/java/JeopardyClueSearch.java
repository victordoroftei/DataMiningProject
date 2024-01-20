import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JeopardyClueSearch {

    private Search search;

    public JeopardyClueSearch() {
        search = new Search();
    }

    public void searchAllClues() {
        int numQuestions = 0;
        double MRR = 0D;
        List<Integer> ranks = new ArrayList<>();

        String jeopardyFilePath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\other\\questions.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(jeopardyFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String category = line;
                line = reader.readLine();

                String clue = line;
                line = reader.readLine();

                String answer = line;
                line = reader.readLine();

                Integer rank = search.getRankForClue(category + " " + clue, answer);
                ranks.add(rank);
                numQuestions++;

                System.out.printf("%s%n%s%n%s%nRANK: %d%n%n", category, clue, answer, rank);
            }

            for (Integer rank : ranks) {
                MRR = MRR + 1.0D / rank;
            }

            MRR *= 1.0D / numQuestions;
            System.out.println("MRR is: " + MRR);
        } catch (IOException e) {
            System.err.println("Could not read from Jeopardy file!");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Could not parse query!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JeopardyClueSearch jeopardyClueSearch = new JeopardyClueSearch();
        jeopardyClueSearch.searchAllClues();
    }
}
