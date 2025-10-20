package nyt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LetterBoxedApp
{
    public static final String DICTIONARY_PATH = "nyt/edited_dict.txt";

    public static final int NUM_SIDES = 4;
    public static final int NUM_LETTERS_PER_SIDE = 3;
    private final LetterBoxedSolver solver;
    private final Scanner kb;

    public LetterBoxedApp()
    {
        solver = new LetterBoxedSolver(DICTIONARY_PATH);
        kb = new Scanner(System.in);
    }

    public LetterBoxedApp(String pathToPuzzle)
    {
        this();
        getPuzzleFromFile(pathToPuzzle);
    }

    public void run(String filepath)
    {
        if (filepath != null)
        {
            getPuzzleFromFile(filepath);
        }
        String mainMenu = "Please select from the following menu\n"
                + "0) Set puzzle\n"
                + "1) Find solutions\n"
                + "2) Find words\n"
                + "3) Get hints\n"
                + "4) Quit\n";
        boolean hasQuit = false;
        while (!hasQuit)
        {
            switch (getNumber(0, 4, mainMenu))
            {
                case 0:
                    getPuzzle();
                    break;
                case 1:
                    findSolutions();
                    break;
                case 2:
                    findWords();
                    break;
                case 3:
                    getHints();
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    hasQuit = true;           
            }
        }
    }

    private void findSolutions()
    {
        if (!solver.hasLetters())
        {
            System.out.println("You need to enter a puzzle first.");
            return;
        }
        String findSolutionsMenu = 
            "Would you like to find the best solutions or solutions of a given length?\n"
            + "1) Best solutions\n"
            + "2) Solutions of a given length\n"
            + "3) Return to main menu\n";

        int choice = 0;
        while (choice != 3)
        {
            choice = getNumber(1, 3, findSolutionsMenu);
            switch (choice)
            {
                case 1:
                    printAnswers(solver.findBestSolutions(
                        yesOrNo("Would you like to filter the best solutions"
                            + "to only show those that use the fewest letters?")));
                    break;
                case 2:
                    printAnswers(solver.findSolutions(
                        getNumber(1, 5, "How many words would you like in the solutions?")
                    ));
                    break;
            }
        }
    }

    /**
     * Lets the user find all words that fit a given criteria.
     * Shows the user a menu of the different ways they can
     * specify the words they want, then calls the appropriate
     * method to find and display those words.
     * 
     * The method continues to loop and ask the user what they
     * want until they enter 5 (quit).
     */
    private void findWords()
    {
        if (!solver.hasLetters())
        {
            System.out.println("You need to enter a puzzle first.");
            return;
        }
        String findWordsMenu = 
            "How would you like to search for words?\n"
            + "1) Words starting with a certain string\n"
            + "2) Words ending with a certain string\n"
            + "3) Words containing certain strings\n"
            + "4) Words of certain lengths\n"
            + "5) Words containing certain numbers of unique letters\n"
            + "6) A combination of these\n"
            + "7) Return to main menu\n";

        int choice = 0;
        while (choice != 7)
        {
            choice = getNumber(1, 7, findWordsMenu);
            switch (choice)
            {
                case 1:
                    findWordsStartingWith();
                    break;
                case 2:
                    findWordsEndingWith();
                    break;
                case 3:
                    findWordsContaining();
                    break;
                case 4:
                    findWordsByLength();
                    break;
                case 5:
                    findWordsByNumUniqueLetters();
                    break;
                case 6:
                    findWordsCombo();
                    break;
            }
        }
    }

    private void getHints()
    {
        if (!solver.hasLetters())
        {
            System.out.println("You need to enter a puzzle first.");
            return;
        }
        int choice = getNumber(1, 6,
            "Select your hint\n"
            + "1) Number solutions of a given number of words\n"
            + "2) Number of letters in the shortest solution\n"
            + "3) Starting letters of best solutions\n"
            + "4) Connecting letters of best solutions\n"
            + "5) Binary solution questions\n"
            + "6) Return to main menu\n");
        switch (choice)
        {
            case 1:
                findNumSolutions();
                break;
            case 2:
                findLengthBestSolutions();
                break;
            case 3:
                findStartingLettersBestSolutions();
                break;
            case 4:
                findConnectingLettersBestSolutions();
                break;
            case 5:
                binarySolutionQuestions();
                break;
        }
    }

    /**
     * Allows the user to ask yes/no questions about certain
     * aspects of the solutions.
     * 
     * This feature is not yet implemented.
     */
    private void binarySolutionQuestions()
    {
        System.out.println("This feature is not yet implemented.");
        // System.out.println("Select your question:\n"
        //     + "1) Are there best solutions in which a single word contains certain letters?\n"
        //     + "2) Are there best solutions which a word of a given length is used?\n"
        //     + "3) Are there best solutions where the letters are split in a certain way?\n"
        //     + "4) Return to previous menu\n");
    }

    /**
     * Finds the starting letters of the shortest solutions.
     * Prints the letters to the console.
     */
    private void findStartingLettersBestSolutions()
    {
        ArrayList<String> answers = solver.findBestSolutions(false);
        System.out.println("The starting letters of the shortest solutions are:");
        for (String answer : answers)
        {
            System.out.println(answer.charAt(0));
        }
    }

    /**
     * Finds the connecting letters between the words of the shortest solutions.
     * Prints the letters to the console.
     */
    private void findConnectingLettersBestSolutions()
    {
        ArrayList<String> answers = solver.findBestSolutions(false);
        System.out.println("The connecting letters of the shortest solutions are:");
        for (String answer : answers)
        {
            String[] words = answer.split(" - ");
            for (int i = 0; i < words.length - 1; i++)
            {
                System.out.print(words[i].charAt(words[i].length() - 1));
                if (i < words.length - 2)
                {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Finds the length of the shortest solution.
     * Prints the length to the console.
     */
    private void findLengthBestSolutions()
    {
        System.out.printf(
            "The shortest solution is %d letters long\n",
            solver.findBestSolutions(true).get(0).replaceAll("[^a-z]", "").length());
    }

    
    /**
     * Finds the number of solutions with a given number of words.
     * Prints the number to the console.
     */
    private void findNumSolutions()
    {
        int numWords = getNumber(1, 5,
            "How many words would you like in the solutions?\n");
        System.out.printf("There are %d %d-word solutions\n",
            solver.findSolutions(numWords).size(), numWords);
    }

    /**
     * Finds all words ending with a string provided by the user.
     * Prints the list of words to the console.
     */
    private void findWordsStartingWith()
    {
        ArrayList<String> answers = solver.findWordsWithPattern(
            getStartWithFromUser(), null, null, -1, -1, -1, -1);
        printAnswers(answers);
    }

    /**
     * Finds all words ending with a string provided by the user.
     * Prints the list of words to the console.
     */
    private void findWordsEndingWith()
    {
        printAnswers(solver.findWordsWithPattern(
            null, getEndsWithFromUser(), null, -1, -1, -1, -1));
    }

    /**
     * Finds all words ending with a string provided by the user.
     * Prints the list of words to the console.
     */
    private void findWordsContaining()
    {
        printAnswers(solver.findWordsWithPattern(
            null, null, getContainsFromUser(), -1, -1, -1, -1));
    }

    public void findWordsByLength()
    {
        int[] range = getLetterRangeFromUser();
        printAnswers(solver.findWordsWithPattern(null, null, null, range[0], range[1], -1, -1));
    }

    public void findWordsByNumUniqueLetters()
    {
        int[] range = getUniqueLetterRangeFromUser();
        printAnswers(solver.findWordsWithPattern(null, null, null, -1, -1, range[0], range[1]));
    }

    public void findWordsCombo()
    {
        String startsWith = null;
        String endsWith = null;
        String[] contains = null;
        int[] letterRange = null;
        int[] uniqueLetterRange = null;
        if (yesOrNo("Would you like to set a what the words must start with?"))
        {
            startsWith = getStartWithFromUser();
        }
        if (yesOrNo("Would you like to set a what the words must end with?"))
        {
            endsWith = getEndsWithFromUser();
        }
        if (yesOrNo("Would you like to set words / letters that must be in the words?"))
        {
            contains = getContainsFromUser();
        }
        letterRange = getLetterRangeFromUser();
        uniqueLetterRange =getUniqueLetterRangeFromUser();
        printAnswers(solver.findWordsWithPattern(startsWith, endsWith, contains,
            letterRange[0], letterRange[1], uniqueLetterRange[0], uniqueLetterRange[1]));
    }

    private String getStartWithFromUser()
    {
        return getLetters("What letter(s) do you want the words to start with?\n");
    }

    private String getEndsWithFromUser()
    {
        return getLetters("What letter(s) do you want the words to end with?\n");
    }

    public String[] getContainsFromUser()
    {
        String input = "";
        boolean validInput = false;
        while (!validInput)
        {
            System.out.print("Enter the substrings / letters you want "
                + "the words to contain, separated by commas (ex: tr,o,w)\n");
            input = getUserInput().replaceAll("\s+", "").toLowerCase();
            if (!input.matches("[a-z,]+"))
            {
                System.out.println("Your input must only contain letters and commas");
            }
            else
            {
                validInput = true;
            }
        }
        return input.split(",");
    }

    private int[] getLetterRangeFromUser()
    {
        int[] range = {-1, -1};
        if (yesOrNo("Would you like to set a minimum for the word length?"))
        {
            range[0] = getNumber(0, 30, "What is the minimum word length?\n");
        }
        if (yesOrNo("Would you like to set a maximum for the word length?"))
        {
            range[1] = getNumber(0, 30, "What is the maximum word length?\n");
        }
        return range;
    }

    private int[] getUniqueLetterRangeFromUser()
    {
        int[] range = {-1, -1};
        if (yesOrNo("Would you like to set a minimum for the number of unique letters?"))
        {
            range[0] = getNumber(0, NUM_LETTERS_PER_SIDE * NUM_SIDES,
                "What is the minimum number of unique letters?\n");
        }
        if (yesOrNo("Would you like to set a maximum for the number of unique letters?"))
        {
            range[1] = getNumber(0, NUM_LETTERS_PER_SIDE * NUM_SIDES,
                "What is the maximum number of unique letters?\n");
        }
        return range;
    }

    private boolean yesOrNo(String question)
    {
        return getNumber(1, 2, String.format("%s\n1) yes\n2) no\n", question)) == 1;
    }

    /**
     * Prints all the strings in an ArrayList.
     * One string per line.
     * 
     * @param answers the list to print
     */
    private void printAnswers(ArrayList<String> answers)
    {
        for (String answer : answers)
        {
            System.out.println(answer);
        }
    }

    /**
     * Gets a number from the user in a specified range.
     * If invalid input is entered then a message saying the acceptable
     * range is printed and the method attempts to get the input again.
     * 
     * @param prompt a message to display to the user before getting input.
     *      It is re-displayed if invalid input is entered.
     *      A newline is NOT automatically added to the end of it.
     * @param min the minimum acceptable number.
     * @param max the maximum acceptable number.
     * @return the number entered.
     */
    private int getNumber(int min, int max, String prompt)
    {
        boolean validInput = false;
        int input = min - 1;
        while (!validInput)
        {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(getUserInput());
                if (input < min || input > max)
                {
                    System.out.printf("%d is too %s.\n", input, input < min ? "low" : "high");
                }
                else
                {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.printf("%d is not a number.\n", input);
            }
        }
        return input;
    }

    /**
     * Gets a string from the user containing only letters.
     * 
     * @param prompt a message to display to the user before getting their input.
     *      It is re-displayed if they enter invalid input.
     * @return the input from the user with whitespace stripped and converted to lowercase.
     */
    private String getLetters(String prompt)
    {
        String input = "";
        boolean validInput = false;
        while (!validInput)
        {
            System.out.print(prompt);
            input = getUserInput().replaceAll("\s+", "").toLowerCase();
            if (!input.matches("[a-z]+"))
            {
                System.out.println("Your input must only contain letters and contain at least one letter.");
            }
            else
            {
                validInput = true;
            }
        }
        return input;
    }

    /**
     * Get a puzzle from the user.
     * Asks the user which method they want to use.
     * 
     * @return true if a puzzle was successfully loaded.
     *      false otherwise.
     */
    private boolean getPuzzle()
    {
        String prompt = "How would you like to enter the puzzle?\n"
                      + "1) From a file"
                      + "2) Through the console"
                      + "3) Return to main menu";
        int choice = 0;
        boolean success = false;
        while (choice != 3 && !success)
        {
            choice = getNumber(1, 3, prompt);
            switch (choice)
            {
                case 1:
                    System.out.println("What is the path to the file?"); 
                    success = getPuzzleFromFile(getUserInput());
                    break;
                case 2:
                    success = getPuzzleFromKeyboard();
                    break;
            }
        }
        return success;
    }

    /**
     * Gets a puzzle from a filepath provided by the user.
     * The file must start with the puzzle and have at least NUM_SIDES
     * number of lines. Each line should have NUM_LETTERS_PER_SIDE
     * number of letters. Whitespace and any data after the puzzle is ignored.
     * If the input is invalid, an error is printed and the user
     * is returned to their previous location.
     * 
     * @return true if the puzzle was successfully set, false otherwise.
     */
    private boolean getPuzzleFromFile(String filepath)
    {
        char[][] puzzle = new char[NUM_SIDES][NUM_LETTERS_PER_SIDE];
        try (Scanner puzzleFile = new Scanner(new File(filepath))) {
            for (int i = 0; i < NUM_SIDES; i++)
            {
                String side = puzzleFile.nextLine().toLowerCase();
                String message = validateRow(side);
                if (message.equals("valid"))
                {
                    //puzzle[i] = side.toCharArray();
                    setSide(puzzle, side, i);
                }
                else
                {
                    System.out.println(message);
                    return false;
                }
            }
        }
        catch (NoSuchElementException e) {
            System.out.printf("There were not enough lines in the file provided. "
                + "There must be at least %d lines of %d letters each.\n",
                NUM_SIDES, NUM_LETTERS_PER_SIDE);
            return false;
        } catch (FileNotFoundException e) {
            System.out.printf("Could not find file: %s\n", filepath);
        }
        solver.setLetters(puzzle);
        return true;
    }

    /**
     * Gets a puzzle from the user.
     * Asks for one side at a time.
     * If the input is invalid, an error is printed
     * and the user is prompted to re-enter the side.
     * 
     * @return true if the puzzle was successfully set, false otherwise.
     */
    private boolean getPuzzleFromKeyboard()
    {
        char[][] puzzle = new char[NUM_SIDES][NUM_LETTERS_PER_SIDE];
        String input;
        boolean validSide;
        for (int i = 0; i < NUM_SIDES; i++)
        {
            validSide = false;
            while (!validSide)
            {
                input = getLetters(String.format("Enter side %d or enter q to abort\n", i + 1));
                if (input.equals("q"))
                {
                    System.out.println("Returning to previous menu...");
                    return false;
                }
                String message = validateRow(input);
                if (message.equals("valid"))
                {
                    puzzle[i] = input.toCharArray();
                    validSide = true;
                }
                else
                {
                    System.out.println(message);
                }
            }
        }
        solver.setLetters(puzzle);
        return true;
    }

    /**
     * Sets a side of the puzzle.
     * 
     * @param puzzle the puzzle to modify
     * @param side the string containing the letters for the side
     * @param sideNum the index of the side to set
     */
    private void setSide(char[][] puzzle, String side, int sideNum)
    {
        for (int i = 0; i < NUM_LETTERS_PER_SIDE; i++)
        {
            puzzle[sideNum][i] = side.charAt(i);
        }
    }

    /**
     * Validates is a string is a valid puzzle side.
     * 
     * @param side - the side to validate
     * @return "valid" if the side is valid
     *      If the side is not valid, returns a specific error message explaining why.
     */
    private String validateRow(String side)
    {
        if (side.length() != NUM_LETTERS_PER_SIDE)
        {
            return String.format("❌ Invalid input: each side must have exactly %d letters.", NUM_LETTERS_PER_SIDE);
        }
        if (!side.matches("[a-z]+"))
        {
            return "❌ Invalid input: only characters a through z are allowed.";
        }
        return "valid";
    }

    /**
     * Gets raw input from the user.
     * Shuts down the program if the user enters "exit!".
     * 
     * @return the input from the user.
     */
    private String getUserInput()
    {
        String input = kb.nextLine();
        if (input.equals("exit!"))
        {
            System.out.println("Goodbye!");
            System.exit(0);
        }
        return input;
    }

    /**
     * Runs the application.
     * 
     * @param args - optionally proved the path to a file to initialize the puzzle.
     */
    public static void main(String[] args)
    {
        try {
            new LetterBoxedApp().run(
                args.length < 1 ? null : args[0]);
        } catch (NoSuchElementException e) {
            System.out.println("Keyboard input closed.");
            System.out.println("You can always type 'exit!' to quit at any time. Goodbye!");
        }
    }
}
