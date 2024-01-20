import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// This class is used for iterating through the lines from other/questions.txt, containing the Jeopardy clues and answers.
public class JeopardyClueSearch {

    private Search search;

    public JeopardyClueSearch() {
        search = new Search();
    }

    // Method used for iterating through the file and querying the index
    public void searchAllClues() {
        int numQuestions = 0;
        double MRR = 0D, pAtOne = 0D;
        List<Integer> ranks = new ArrayList<>();

        Map<Integer, Integer> freqMap = new TreeMap<>();
        String jeopardyFilePath = "E:\\__Teme\\Data Mining (DM)\\testLucene1\\other\\questions.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(jeopardyFilePath))) {
            String line;
            int found = 0;
            while ((line = reader.readLine()) != null) {
                String category = line;
                line = reader.readLine();

                String clue = line;
                line = reader.readLine();

                String answer = line;
                line = reader.readLine();

                List<String> titles = new ArrayList<>();
                // Performing the query: getting the rank for the clue and category
                Integer rank = search.getRankForClue(category + " " + clue, answer, titles);

                if (rank == 1) {
                    pAtOne++;
                }

                // We selected the documents with ranks 2, 3, 4 and 5 (20 in total) and extracted their contents to files.
                // We also generated some prompts for ChatGPT, to be used along those files.
                if (rank > 1 && rank <= 5) {
//                    String prompt = String.format("I will give you a clue and a set of documents (the uploaded files) that are ranked by our algorithm (title and content). I want you to re-rank the documents(if it's the case) based on the clue and category:\n" +
//                            "clue: %s .\n" +
//                            "category: %s\n" +
//                            " As a response, I want to write me the re-ranked titles, where the titles of the documents are the names of the files.\n" +
//                            "\n", clue, category);
//                    System.out.println(prompt);

                    Map<String, String> map = IndexViewer.getDocumentContentsForTitles(titles);

                    // Write the contents of the documents to files (used for prompting ChatGPT)
//                    for (String s : map.keySet()) {
//                        try {
//                            FileWriter myWriter = new FileWriter(String.format("%d-%s.txt", found, s));
//
//                            myWriter.write(s + "\n\n");
//                            myWriter.write(map.get(s));
//                            myWriter.close();
//
//                            System.out.println("Successfully wrote to the file.");
//                        } catch (IOException e) {
//                            System.out.println("An error occurred.");
//                            e.printStackTrace();
//                        }
//                    }

                    found++;
                }

                ranks.add(rank);

                // Map containing the frequencies of ranks (key: rank, value: number of documents with that rank)
                if (freqMap.containsKey(rank)) {
                    freqMap.put(rank, freqMap.get(rank) + 1);
                } else {
                    freqMap.put(rank, 1);
                }

                numQuestions++;

                System.out.printf("%s%n%s%n%s%nRANK: %d%n%n", category, clue, answer, rank);
            }

            // Calculating MRR
            for (Integer rank : ranks) {
                MRR = MRR + 1.0D / rank;
            }

            MRR *= 1.0D / numQuestions;
            pAtOne /= numQuestions;

            System.out.println("MRR is: " + MRR);
            System.out.println("P@1 is: " + pAtOne);

            System.out.println("Frequency Map:");
            System.out.println(freqMap);
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
