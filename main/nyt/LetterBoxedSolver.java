package nyt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Solver for the New York Times LetterBoxed game.
 * 
 * @author Willow Sapphire
 * @version 08/18/2024
 */
public class LetterBoxedSolver
{
    /**
     * The list of valid words. This list includes words not accepted by NYT.
     */
    String[] dictionary;

    /**
     * A list to contain all two word answers.
     */
    private ArrayList<String> answers;

    /**
     * Marks which letters have been used so far.
     */
    private boolean[][] used;

    /**
     * The letters in the grid.
     */
    private char[][] letters;

    /**
     * Creates a new LetterBoxedSolver.
     * 
     * @param dictFile the path to the dictionary file you want to use.
     */
    public LetterBoxedSolver(String dictFile)
    {
        this.answers = new ArrayList<>();
        readInDictionary(dictFile);
    }

    /**
     * Finds all two-word solutions for a given set of letters.
     * 
     * @param letters the letters to use.
     * @return an array of all two word solutions with the words separate by " - ".
     */
    public String[] solve(char[][] letters)
    {
        this.letters = letters;
        this.used = new boolean[letters.length][];
        for (int i = 0; i < letters.length; i++)
        {
            this.used[i] = new boolean[letters[i].length];
        }
        for (int i = 0; i < letters.length; i++)
        {
            for (int j = 0; j < letters[i].length; j++)
            {
                ArrayList<String> words = new ArrayList<>();
                String answer = "";
                getWords(letters[i][j] + "", i, words);
                for (int k = 0; k < words.size(); k++)
                {
                    answer = words.get(k);
                    ArrayList<String> secondWords = new ArrayList<>();
                    getWords("" + answer.charAt(answer.length() - 1), findSide(answer.charAt(answer.length() - 1)), secondWords);
                    for (int l = 0; l < secondWords.size(); l++)
                    {
                        answer = words.get(k);
                        resetUsed();
                        markUsed(answer);
                        markUsed(secondWords.get(l));
                        if (allUsed())
                        {
                            answers.add(answer + " - " + secondWords.get(l));
                        }
                    }
                }
            }
        }
        return answers.toArray(new String[answers.size()]);
    }

    /**
     * Gets all words that can be made using the grid and a given prefix.
     * The words are stored in the provided ArrayList.
     * 
     * @param prefix the prefix with which the words must start.
     * @param currSide the side that the last letter of the prefix is on.
     * @param results the list in which to store answers.
     */
    private void getWords(String prefix, int currSide, ArrayList<String> results)
    {
        if (noMoreWords(prefix))
        {
            return;
        }
        if (isInDictionary(prefix))
        {
            results.add(prefix);
        }
        for (int i = 0; i < letters.length; i++)
        {
            if (i != currSide)
            {
                for (int j = 0; j < letters[i].length; j++)
                {
                    used[i][j] = true;
                    getWords(prefix + letters[i][j], i, results);
                }
            }
        }
    }

    /**
     * Marks all letters in a string as being used.
     * 
     * @param word the word for which to mark.
     */
    private void markUsed(String word)
    {
        for (char c : word.toCharArray())
        {
            markUsed(c);
        }
    }

    /**
     * Resets the used array to be entirely false.
     */
    private void resetUsed()
    {
        for (int i = 0; i < used.length; i++)
        {
            for (int j = 0; j < used[i].length; j++)
            {
                used[i][j] = false;
            }
        }
    }

    /**
     * Checks if every letter in the grid has been used.
     * 
     * @return true if every letter has been used, false otherwise.
     */
    private boolean allUsed()
    {
        for (boolean ba[] : used)
        {
            for (boolean b : ba)
            {
                if (!b)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Marks that a given character has been used in the used array.
     * 
     * @param c the character for which to mark.
     */
    private void markUsed(char c)
    {
        for (int i = 0; i < letters.length; i++)
        {
            for (int j = 0; j < letters[i].length; j++)
            {
                if (c == letters[i][j])
                {
                    used[i][j] = true;
                    return;
                }
            }
        }
    }

    /**
     * Checks if a word is in the dictionary.
     * 
     * @param word the word for which to search.
     * @return true if the word is in the dictionary, false otherwise.
     */
    private boolean isInDictionary(String word)
    {
        return this.dictionary[prefixRange(word)[0]].equals(word);
    }

    /**
     * Checks if there are no more words in the dictionary that begin
     * with a given prefix.
     *
     * @param prefix the prefix for which to search.
     * @return true if there are words that start with prefix, false otherwise.
     */
    private boolean noMoreWords(String prefix)
    {
        return prefixRange(prefix)[0] == -1;
    }

    /**
     * Finds the range in the dictionary where words start with a given prefix.
     * Uses a binary search algorithm.
     * 
     * @param prefix the prefix for which to search.
     * @return an array of ints where [0] is the first index of a word
     *      beginning with the prefix and [1] is the last index of a word
     *      beginning with the prefix.
     */
    private int[] prefixRange(String prefix)
    {
        int left = 0;
        int right = dictionary.length - 1;
        int[] range = {-1, -1};
        while (left <= right)
        {
            int mid = left + (right - left) / 2;
            String midWord = dictionary[mid];

            if (midWord.startsWith(prefix))
            {
                range[0] = mid;
                right = mid - 1;
            }
            else if (midWord.compareTo(prefix) < 0)
            {
                left = mid + 1;
            }
            else
            {
                right = mid - 1;
            }
        }
        if (range[0] != -1)
        {
            range[1] = range[0];
            for(; range[1] + 1 >= 0 && range[1] + 1 < dictionary.length
                && dictionary[range[1] + 1].startsWith(prefix); range[1]++);
        }

        return range;
    }

    /**
     * Finds which side of the grid a given letter is on.
     * 
     * @param letter the letter to find.
     * @return the index of the side containing the given letter
     *      or -1 if it does not exist.
     */
    private int findSide(char letter)
    {
        for (int i = 0; i < letters.length; i++)
        {
            for (int j = 0; j < letters[i].length; j++)
            {
                if (letter == letters[i][j])
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Reads in a list of words from a dictionary file and
     * stores it in the dictionary field.
     * 
     * @param dictName the path to the file to use.
     */
    private void readInDictionary(String dictName)
    {
        ArrayList<String> words = new ArrayList<>();
        try {
            Scanner dictFile = new Scanner(new File(dictName));
            while (dictFile.hasNextLine())
                words.add(dictFile.nextLine().toLowerCase());
            dictFile.close();
            this.dictionary = words.toArray(new String[words.size()]);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read dictionary");
            e.printStackTrace();
        }
    }
}