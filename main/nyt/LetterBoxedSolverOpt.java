package nyt;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class LetterBoxedSolverOpt {
    private String[] dictionary;
    private ArrayList<String> answers;
    private boolean[][] used;
    private char[][] letters;
    public LetterBoxedSolverOpt(String dictPath) throws FileNotFoundException {
        this.answers = new ArrayList<>();
        Scanner dictFile = new Scanner(new File(dictPath));
        this.dictionary = new String[Integer.parseInt(dictFile.nextLine())];
        for (int i = 0; i < this.dictionary.length; i++)
            this.dictionary[i] = dictFile.nextLine().toLowerCase();
        dictFile.close();
    }
    public String[] findBestSolution(char[][] letters) {
        this.letters = letters;
        this.used = new boolean[letters.length][];
        for (int i = 0; i < used.length; i++)
            this.used[i] = new boolean[letters[i].length];
        ArrayList<String> firstWords = new ArrayList<>();
        for (int i = 0; i < letters.length; i++)
            for (int j = 0; j < letters[i].length; j++)
                getWords(letters[i][j] + "", firstWords);
        for (String word : firstWords)
            if (validSolution(word))
                answers.add(word);
        if (answers.size() == 0) {
            answers = firstWords;
            int best = 2;
            do solveLength(best++);
            while (answers.size() == 0);
        }
        return answers.toArray(new String[answers.size()]);
    }
    public void solveLength(int numWords) {
        int amnt = answers.size();
        for (int i = 0; i < amnt; i++) {
            String answer = answers.get(0);
            ArrayList<String> words = new ArrayList<>();
            getWords(answer.charAt(answer.length() - 1) + "", words);
            for (String word : words)
                if (numWords != 2 || validSolution(answer + word))
                    answers.addLast(answer + " - " + word);
            answers.remove(0);
        }
        if (numWords > 2)
            solveLength(numWords - 1);
    }
    private void getWords(String prefix, ArrayList<String> results) {
        int currSide = 0;
        for (int i = 0; i < letters.length; i++)
            for (int j = 0; j < letters[i].length; j++)
                if (prefix.charAt(prefix.length() - 1) == letters[i][j])
                    currSide = i;
        int range[] = {-1, -1};
        for (int left = 0, right = dictionary.length, mid = left + (right - left) / 2;
            left <= right; mid = left + (right - left) / 2) {
            String midWord = dictionary[mid];
            if (midWord.startsWith(prefix)) range[0] = (right = mid - 1) + 1;
            else if (midWord.compareTo(prefix) < 0) left = mid + 1;
            else right = mid - 1;
        }
        if (range[0] != -1)
            for(range[1] = range[0]; range[1] + 1 >= 0 && range[1] + 1 < dictionary.length
                && dictionary[range[1] + 1].startsWith(prefix); range[1]++);
        if (range[0] == -1)
            return;
        if (this.dictionary[range[0]].equals(prefix))
            results.add(prefix);
        for (int i = 0; i < letters.length; i++)
            if (i != currSide)
                for (int j = 0; j < letters[i].length; j++)
                    getWords(prefix + letters[i][j], results);
    }
    private boolean validSolution(String solution) {
        for (int i = 0; i < used.length; i++)
            for (int j = 0; j < used[i].length; j++)
                used[i][j] = false;
        for (char c : solution.toCharArray())
            for (int i = 0; i < letters.length; i++)
                for (int j = 0; j < letters[i].length; j++)
                    if (c == letters[i][j])
                        used[i][j] = true;
        for (boolean ba[] : used)
            for (boolean b : ba)
                if (!b)
                    return false;
        return true;
    }
}