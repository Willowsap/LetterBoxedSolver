package nyt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Solver for the New York Times LetterBoxed game.
 * 
 * @author Willow Sapphire
 * @version 08/18/2024
 */
public class LetterBoxedSolver
{
    /**
     * The list of valid words.
     * Alphabetized
     */
    private String[] dictionary;

    /**
     * The letters in the puzzle.
     */
    private char[][] letters;

    /**
     * The number of letters in the puzzle.
     */
    private int numLetters;

    /**
     * A flag to indicate if the words arraylist currently
     * contains all words for the current puzzle.
     */
    private boolean wordsFound;

    /**
     * A list to contain all words in the puzzle.
     * Alphabetized
     */
    private ArrayList<String> words;

    /**
     * Creates a new LetterBoxedSolver.
     * 
     * @param letters the initial puzzle.
     * @param dictFile the path to the dictionary file you want to use.
     */
    public LetterBoxedSolver(String dictFile, char[][] letters)
    {
        readInDictionary(dictFile);
        setLetters(letters);
    }

    /**
     * Creates a new LetterBoxedSolver without initializing the puzzle.
     * 
     * @param dictFile the path to the dictionary file you want to use.
     */
    public LetterBoxedSolver(String dictFile)
    {
        readInDictionary(dictFile);
    }

    /**
     * Finds the solutions with the fewest number of words.
     * 
     * @return a list of the solutions with the fewest number of words.
     * @throws IllegalStateException if the puzzle was not initialized prior.
     */
    public ArrayList<String> findBestSolutions(boolean fewestLetters) throws IllegalStateException
    {
        int numWords = 1;
        ArrayList<String> answers = findSolutions(numWords);
        while (answers.isEmpty())
        {
            answers = findSolutions(++numWords);
        }
        if (fewestLetters)
        {
            filterToShortest(answers);
        }
        return answers;
    }

    /**
     * Finds all solutions containing a give number of words.
     * 
     * @param numWords - the number of words in the solution.
     * @return the solutions.
     * @throws IllegalStateException if the puzzle was not initialized prior.
     */
    public ArrayList<String> findSolutions(int numWords) throws IllegalStateException
    {
        validateState();
        ArrayList<String> answers = new ArrayList<>();
        for (String starter : words)
        {
            findSolutions(starter, numWords - 1, answers);
        }
        return answers;
    }

    /**
     * Recursively finds solutions to the puzzle of a specified length.
     * 
     * @param currentChain the current word chain
     * @param numWordsToAdd the number of words that still need to be added to the chain
     * @param solutions the list of solutions that have been found
     */
    private void findSolutions(String currentChain, int numWordsToAdd, ArrayList<String> solutions)
    {
        // only include answers that require all words to use all letters
        if (numWordsToAdd > 0 && allLettersUsed(currentChain))
        {
            return;
        }
        if (numWordsToAdd == 0)
        {
            if (allLettersUsed(currentChain))
            {
                solutions.add(currentChain);
            }
        }
        else
        {
            int[] prefixRange = prefixRange(
                currentChain.charAt(currentChain.length() - 1) + "", words);
            for (int i = prefixRange[0]; i <= prefixRange[1]; i++)
            {
                findSolutions(currentChain + " - " + words.get(i), numWordsToAdd - 1, solutions);
            }
        }
    }

    /**
     * Gets all words that follow a number of conditions.
     * All conditions are optional.
     * 
     * @param startsWith a string that the words must start with
     *                  ignored if null or empty
     * @param endsWith a string that words must end with
     *                  ignored if null or empty
     * @param contains an array of strings that words must contain (order does not matter)
     *                  ignored if null or empty
     * @param minLength the minimum length of the strings
     *                  ignored if less than 0
     * @param maxLength the maximum lenth of the strings
     *                  ignored if less than 0
     * @param minUnique the minimum number of unique letters that the word must use
     *                  ignored if less than 0
     * @param maxUnique the maximum number of unique letters that the word may use
     *                  ignored if less than 0
     * @return an ArrayList of all words in the puzzle that fit the criteria
     * @throws IllegalStateException if the puzzle has not been set
     */
    public ArrayList<String> findWordsWithPattern(String startsWith, String endsWith,
        String[] contains, int minLength, int maxLength, int minUnique, int maxUnique) throws IllegalStateException
    {
        validateState();
        ArrayList<String> answers = new ArrayList<>(words.size());
        answers.addAll(words);

        // filter on startsWith and/or endsWith and/or contains if requested
        if (startsWith != null || endsWith != null
            || (contains != null && contains.length > 0))
        {
            filterOnPattern(answers, startsWith, endsWith, contains);
        }

        // filter on length if requested
        if (minLength >= 0 || maxLength >= 0)
        {
            filterOnLength(answers, minLength, maxLength);
        }

        // filter on number of unique letters if requested
        if (minUnique >= 0 || maxLength >=0)
        {
            filterOnUniqueLetterCount(answers, minUnique, maxUnique);
        }

        return answers;
    }

    /**
     * Checks if a letter is in the current puzzle.
     * 
     * @param letter the letter for which to search.
     * @return false if the letter is not found or if the
     *      puzzle is not currently valid.
     */
    public boolean letterInPuzzle(char letter)
    {
        if (letters != null)
        {
            for (int i = 0; i < letters.length; i++)
            {
                for (int j = 0; j < letters[i].length; j++)
                {
                    if (letters[i][j] == letter)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if there are letters in the solver.
     * 
     * @return true if letters have been set. false otherwise.
     */
    public boolean hasLetters()
    {
        return letters != null;
    }

    /**
     * Accessor method for the grid.
     * 
     * @return letters.
     */
    public char[][] getLetters()
    {
        return this.letters;
    }

    /**
     * Sets the grid to a new set of letters.
     * Also sets the used array.
     * 
     * @param letters the new letters.
     */
    public void setLetters(char[][] letters)
    {
        this.letters = letters;
        this.numLetters = 0;
        for (int i = 0; i < letters.length; i++)
        {
            numLetters += letters[i].length;
        }
    }

    /**
     * Filters an arraylist to only contain the strings of 
     * the shortest length
     * @param list - the list to filter
     */
    private void filterToShortest(List<String> list)
    {
        int minLength = Integer.MAX_VALUE;
        for (String s : list)
        {
            if (s.length() < minLength)
            {
                minLength = s.length();
            }
        }
        Iterator<String> i = list.iterator();
        while(i.hasNext())
        {
            if (i.next().length() > minLength)
            {
                i.remove();
            }
        }
    }

    /**
     * Removes all strings from a list whose number of unique letters used
     * do not fall within a given range.
     * minLength and maxLength are both inclusive.
     * 
     * @param input the list of strings to filter
     * @param minUnique the minimum acceptable number of unique letters
     * @param maxUnique the maximum acceptable number of unique letters
     *                  ignored if negative
     */
    private void filterOnUniqueLetterCount(List<String> input,
        int minUnique, int maxUnique)
    {
        Iterator<String> i = input.iterator();
        while (i.hasNext())
        {
            int numUnique = getNumUniqueLettersUsed(i.next());
            if(numUnique < minUnique || (maxUnique >= 0 && numUnique > maxUnique))
            {
                i.remove();
            }
        }
    }

    /**
     * Removes all strings from a list whose lengths
     * do not fall within the given range.
     * minLength and maxLength are both inclusive.
     * 
     * @param input the list of strings to filter
     * @param minLength the minimum acceptable length
     * @param maxLength the maximum acceptable length
     *                  ignored if negative
     */
    private void filterOnLength(List<String> input, int minLength, int maxLength)
    {
        Iterator<String> i = input.iterator();
        while (i.hasNext())
        {
            int length = i.next().length();
            if(length < minLength || (maxLength >= 0 && length > maxLength))
            {
                i.remove();
            }
        }
    }

    /**
     * Removes all strings from a list that don't follow a given pattern.
     * 
     * @param input - the list of strings to filter
     * @param startsWith - a string that matches must begin with (may be null)
     * @param endsWith - a string that matches must end with (may be null)
     * @param contains - an array of strings that matches must contain (may be null)
     */
    private void filterOnPattern(List<String> input, String startsWith, String endsWith,
        String[] contains)
    {
        StringBuilder regex = new StringBuilder();
        if (startsWith != null)
        {
            regex.append(startsWith);
        }
        regex.append(".*");
        if (contains != null)
        {
            for (String s : contains)
            {
                regex.append("(?=.*").append(s).append(")");
            }
            regex.append(".*");
        }
        if (endsWith != null)
        {
            regex.append(endsWith);
        }
        Pattern pattern = Pattern.compile(regex.toString());
        Iterator<String> i = input.iterator();
        while (i.hasNext())
        {
            String word = i.next();
            if(!pattern.matcher(word).matches())
            {
                i.remove();
            }
        }
    }

    /**
     * Validates that the letters are set and that words have been found.
     * Finds words if they are not found.
     * Throws an exception if the letters are not set.
     * 
     * @throws IllegalStateException if the letters have not been set.
     */
    private void validateState() throws IllegalStateException
    {
        if (letters == null)
        {
            throw new IllegalStateException("Letter grid has not been set.");
        }
        if (!wordsFound)
        {
            getAllWords();
        }
    }

    /**
     * Checks if a solutions uses all of the letters.
     * 
     * @param solution the solution.
     * @return
     */
    private boolean allLettersUsed(String solution)
    {
        return getNumUniqueLettersUsed(solution) == numLetters;
    }

    /**
     * Gets the number of unique letters in a string.
     * 
     * @param word the string for which to count letters.
     * 
     * @return the number of unique letters in the string.
     */
    private int getNumUniqueLettersUsed(String word)
    {
        word = word.replaceAll("[^a-z]", "");
        int mask = 0;
        for (char c : word.toCharArray())
        {
            mask |= (1 << (c - 'a'));
        }
        return Integer.bitCount(mask);
    }

    /**
     * Finds all words in the puzzle and stores
     * them in the words field.
     */
    private void getAllWords()
    {
        this.words = new ArrayList<>();
        for (int i = 0; i < letters.length; i++)
        {
            for (int j = 0; j < letters[i].length; j++)
            {
                getWordsWithPrefix(letters[i][j] + "", findSide(letters[i][j]), this.words);
            }
        }
        Collections.sort(words);
        wordsFound = true;
    }

    /**
     * Gets all words that can be made using the grid and a given prefix.
     * The words are stored in the provided ArrayList.
     * 
     * @param prefix the prefix with which the words must start.
     * @param currSide the side that the last letter of the prefix is on.
     * @param results the list in which to store answers.
     */
    private void getWordsWithPrefix(String prefix, int currSide, ArrayList<String> results)
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
                    getWordsWithPrefix(prefix + letters[i][j], i, results);
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
        return this.dictionary[prefixRange(word, this.dictionary)[0]].equals(word);
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
        return prefixRange(prefix, this.dictionary)[0] == -1;
    }

    /**
     * Finds the range in the dictionary where words start with a given prefix.
     * Uses a binary search algorithm.
     * 
     * @param prefix the prefix for which to search.
     * @param data the list of strings to search.
     * @return an array of ints where [0] is the first index of a word
     *      beginning with the prefix and [1] is the last index of a word
     *      beginning with the prefix.
     */
    private int[] prefixRange(String prefix, String[] data)
    {
        int left = 0;
        int right = data.length - 1;
        int[] range = {-1, -1};
        while (left <= right)
        {
            int mid = left + (right - left) / 2;
            String midWord = data[mid];

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
            for(range[1] = range[0]; range[1] + 1 >= 0 && range[1] + 1 < data.length
              && data[range[1] + 1].startsWith(prefix); range[1]++);
        }
        return range;
    }

    /**
     * Finds the range in the dictionary where words start with a given prefix.
     * Uses a binary search algorithm.
     * 
     * @param prefix the prefix for which to search.
     * @param data the list of strings to search.
     * @return an array of ints where [0] is the first index of a word
     *      beginning with the prefix and [1] is the last index of a word
     *      beginning with the prefix.
     */
    private int[] prefixRange(String prefix, List<String> data)
    {
        int left = 0;
        int right = data.size() - 1;
        int[] range = {-1, -1};
        while (left <= right)
        {
            int mid = left + (right - left) / 2;
            String midWord = data.get(mid);

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
            for(range[1] = range[0]; range[1] + 1 >= 0 && range[1] + 1 < data.size()
              && data.get(range[1] + 1).startsWith(prefix); range[1]++);
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
