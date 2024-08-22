package nyt;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class LetterBoxedSolverOpt {
    public static String[] solve(char[][] l, String p) throws FileNotFoundException {
        Scanner s = new Scanner(new File(p));
        String[] d = new String[Integer.parseInt(s.nextLine())];
        for (int i = 0; i < d.length; i++)
            d[i] = s.nextLine().toLowerCase();
        s.close();
        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> f = new ArrayList<>();
        boolean[][] u = new boolean[l.length][];
        for (int i = 0; i < u.length; i++)
            u[i] = new boolean[l[i].length];
        for (int i = 0; i < l.length; i++)
            for (int j = 0; j < l[i].length; j++)
                w(l[i][j] + "", f, l, d);
        for (String w : f)
            if (v(w, l, u))
                a.add(w);
        if (a.size() == 0) {
            a = f;
            int b = 2;
            do solveLength(b++, a, l, d, u);
            while (a.size() == 0);
        }
        return a.toArray(new String[a.size()]);
    }
    public static void solveLength(int n, ArrayList<String> a, char[][] l, String[] d, boolean[][] u) {
        int amnt = a.size();
        for (int i = 0; i < amnt; i++) {
            String r = a.get(0);
            ArrayList<String> s = new ArrayList<>();
            w(r.charAt(r.length() - 1) + "", s, l, d);
            for (String w : s)
                if (n != 2 || v(r + w, l, u))
                    a.addLast(r + " - " + w);
            a.remove(0);
        }
        if (n > 2)
            solveLength(n - 1, a, l, d, u);
    }
    private static void w(String p, ArrayList<String> r, char[][] l, String[] d) {
        int c = 0;
        for (int i = 0; i < l.length; i++)
            for (int j = 0; j < l[i].length; j++)
                if (p.charAt(p.length() - 1) == l[i][j])
                    c = i;
        int g[] = {-1, -1};
        for (int f = 0, t = d.length, m = f + (t - f) / 2;
            f <= t; m = f + (t - f) / 2) {
            String o = d[m];
            if (o.startsWith(p)) g[0] = (t = m - 1) + 1;
            else if (o.compareTo(p) < 0) f = m + 1;
            else t = m - 1;
        }
        if (g[0] != -1)
            for(g[1] = g[0]; g[1] + 1 >= 0 && g[1] + 1 < d.length
                && d[g[1] + 1].startsWith(p); g[1]++);
        if (g[0] == -1)
            return;
        if (d[g[0]].equals(p))
            r.add(p);
        for (int i = 0; i < l.length; i++)
            if (i != c)
                for (int j = 0; j < l[i].length; j++)
                    w(p+ l[i][j], r, l, d);
    }
    private static boolean v(String s, char[][] l, boolean[][] u) {
        for (int i = 0; i < u.length; i++)
            for (int j = 0; j < u[i].length; j++)
                u[i][j] = false;
        for (char c : s.toCharArray())
            for (int i = 0; i < l.length; i++)
                for (int j = 0; j < l[i].length; j++)
                    if (c == l[i][j])
                        u[i][j] = true;
        for (boolean ba[] : u)
            for (boolean b : ba)
                if (!b)
                    return false;
        return true;
    }
}