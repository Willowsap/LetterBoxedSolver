package client;

import java.util.Scanner;

import nyt.LetterBoxed;

public class Demo
{
    public static void main(String[] args)
    {
        LetterBoxed lb = new LetterBoxed();
        String[] answers = lb.solve(getLetters());
        for (String answer : answers) System.out.println(answer);
    }

    private static char[][] getLetters() {
        @SuppressWarnings("resource")
        Scanner kb = new Scanner(System.in);
        char[][] letters = new char[4][3];
        System.out.println("Please enter the top row character as one string (no spaces)");
        String word = kb.nextLine().toLowerCase();
        for (int i = 0; i < letters[0].length; i++) {
            letters[0][i] = word.charAt(i);
        }
        System.out.println("Please enter the right row character as one string (no spaces)");
        word = kb.nextLine().toLowerCase();
        for (int i = 0; i < letters[1].length; i++) {
            letters[1][i] = word.charAt(i);
        }
        System.out.println("Please enter the bottom row character as one string (no spaces)");
        word = kb.nextLine().toLowerCase();
        for (int i = 0; i < letters[2].length; i++) {
            letters[2][i] = word.charAt(i);
        }
        System.out.println("Please enter the top left character as one string (no spaces)");
        word = kb.nextLine().toLowerCase();
        for (int i = 0; i < letters[3].length; i++) {
            letters[3][i] = word.charAt(i);
        }
        return letters;
    }
}
