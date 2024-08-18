package nyt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LetterBoxed
{
    String[] dictionary;
    private ArrayList<String> answers;
    private boolean[][] used;
    private char[][] letters;

    public LetterBoxed()
    {
        this.answers = new ArrayList<>();
        readInDictionary();
    }

    public String[] solve(char[][] letters)
    {
        this.letters = letters;
        this.used = new boolean[letters.length][];
        for (int i = 0; i < letters.length; i++) this.used[i] = new boolean[letters[i].length];
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < letters[i].length; j++) {
                ArrayList<String> words = new ArrayList<>();
                String answer = "";
                getWords(letters[i][j] + "", i, words);
                for (int k = 0; k < words.size(); k++) {
                    answer = words.get(k);
                    ArrayList<String> secondWords = new ArrayList<>();
                    getWords("" + answer.charAt(answer.length() - 1), findSide(answer.charAt(answer.length() - 1)), secondWords);
                    for (int l = 0; l < secondWords.size(); l++) {
                        answer = words.get(k);
                        resetUsed();
                        markUsed(answer);
                        markUsed(secondWords.get(l));
                        if (allUsed()) {
                            answers.add(answer + " - " + secondWords.get(l));
                        }
                    }
                }
            }
        }
        return answers.toArray(new String[answers.size()]);
    }

    private void getWords(String prefix, int currSide, ArrayList<String> results)
    {
        if (noMoreWords(prefix)) return;
        if (isInDictionary(prefix)) results.add(prefix);
        for (int i = 0; i < letters.length; i++) {
            if (i != currSide) {
                for (int j = 0; j < letters[i].length; j++) {
                    used[i][j] = true;
                    getWords(prefix + letters[i][j], i, results);
                }
            }
        }
    }

    private void markUsed(String word) {
        for (char c : word.toCharArray()) {
            markUsed(c);
        }
    }

    private void resetUsed() {
        for (int i = 0; i < used.length; i++) {
            for (int j = 0; j < used[i].length; j++) {
                used[i][j] = false;
            }
        }
    }

    private boolean allUsed() {
        for (boolean ba[] : used) {
            for (boolean b : ba) {
                if (!b) return false;
            }
        }
        return true;
    }

    private void markUsed(char c)
    {
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < letters[i].length; j++) {
                if (c == letters[i][j]) {
                    used[i][j] = true;
                    return;
                }
            }
        }
    }

    private boolean isInDictionary(String word)
    {
        return this.dictionary[prefixRange(word)[0]].equals(word);
    }

    private boolean noMoreWords(String word)
    {
        return prefixRange(word)[0] == -1;
    }

    private int[] prefixRange(String prefix)
    {
        int left = 0;
        int right = dictionary.length - 1;
        int[] range = {-1, -1};

        // Binary search to find the lowest index where the prefix matches
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midWord = dictionary[mid];

            if (midWord.startsWith(prefix)) {
                range[0] = mid;
                right = mid - 1;
            } else if (midWord.compareTo(prefix) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        if (range[0] != -1) {
            range[1] = range[0];
            for(; range[1] + 1 >= 0 && range[1] + 1 < dictionary.length && dictionary[range[1] + 1].startsWith(prefix); range[1]++);
        }

        return range;
    }

    private int findSide(char letter)
    {
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < letters[i].length; j++) {
                if (letter == letters[i][j]) return i;
            }
        }
        return -1;
    }

    private void readInDictionary()
    {
        ArrayList<String> words = new ArrayList<>();
        try {
            Scanner dictFile = new Scanner(new File("main/nyt/words_alpha.txt"));
            while (dictFile.hasNextLine())
                words.add(dictFile.nextLine());
            dictFile.close();
            this.dictionary = words.toArray(new String[words.size()]);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read dictionary");
            e.printStackTrace();
        }
    }
}