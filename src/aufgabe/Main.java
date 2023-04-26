package aufgabe;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        try {
            // Aufgabe a
            char[] bool = new char[]{'0', '1'};
            DFSM dea = new DFSM(2, bool);

            // Alle geraden Zahlen
            dea.setStartingState(0);
            dea.setAccepting(1);
            dea.addTransition(0, '0', 1);
            dea.addTransition(0, '1', 0);
            dea.addTransition(1, '0', 1);
            dea.addTransition(1, '1', 0);

            String[] strings = new String[] {
                    genString(6, bool),
                    genString(6, bool),
                    genString(7, bool),
                    genString(11, bool),
                    genString(3, bool),
                    "",
                    "0",
                    "1"
            };
            System.out.println("Normal Run:");
            for(String string : strings) {
                System.out.println("\nWort " + string + ":");
                System.out.print("Zustände: ");
                System.out.println("Accept: " + dea.run(string));
            }
            // Aufgabe b,c
            System.out.println("\n\nRun With aufgabe.Listener:");
            for(String string : strings) {
                System.out.println("\nWort " + string + ": ");
                System.out.println("Hits: ");
                dea.runWithListener(string, pos -> System.out.println("pos=" + pos));
            }

            // Aufgabe d
            System.out.println("\n\nbfSearch(\"ababa\",\"aba\") = " + bfSearch("ababa", "aba"));

            // Aufgabe e
            String wort = genString(1_000_000_000, bool);
            //System.out.println("Wort: " + wort);
            //System.out.println("bfSearch: " + bfSearch(wort, "001001"));
            //System.out.println("deaSearch: " + dea_search_001001(wort));

            // aufgabe f
            /*
            Für das Pattern wort '001001' (Länge 6)
            Es müsste eine Verbesserung von (in diesem Beispiel) ca. Faktor 6 sein.
            Die Komplexität vom bfSearch braucht also in etwa 6*n iterationen (durch die 2 verschachtelten Schleifen)
            Die Komplexität vom deaSearch schätze ich auf lineare komplexität n mit Faktor 2 (da geschaut werden muss ob das Zeichen im Alphabet ist)
            Zumindest wäre das meine Vermutung.
             */

            // Aufgabe g
            System.out.println("\n\nbfSearch:");
            long cur = System.currentTimeMillis();
            bfSearch(wort, "001001");
            System.out.println(System.currentTimeMillis() - cur);
            /*
            1.000

             */

            System.out.println("deaSearch:");
            cur = System.currentTimeMillis();
            dea_search_001001(wort);
            System.out.println(System.currentTimeMillis() - cur);
            /*
            1.000

             */

            // Das Ergebnis überrascht mich, da es scheint als wäre die Brute-Force-Methode um
            // den Faktor 2-3 schneller bei einer Zeichenkette mit 1.000.000 Einträgen
            // Bei Größeren Wortlängen scheint die Laufzeit relativistisch gesehen ähnlicher zu sein. (1.000.000.000 Einträge)

            // Nachdem ich dann weiter nachgedacht habe ist mir aufgefallen, dass beim dea genauso 6*n vorliegt. Dadurch, dass immer geschaut werden
            //
        } catch(DFSM.DFSMException e) {
            System.err.println(e.getMessage());
        }

    }

    // Aufgabe c
    public static String genString(int size, char[] alphabet) {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();
        int length = alphabet.length;
        for(int i = 0; i < size; i++) {
            builder.append(alphabet[rand.nextInt(length)]);
        }
        return builder.toString();
    }

    // Aufgabe d
    public static int bfSearch(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        char[] cText = text.toCharArray();
        char[] cPattern = pattern.toCharArray();

        if(textLen < patternLen) return 0;

        int count = 0;
        for(int i = 0; i < textLen - (patternLen - 1); i++) {
            boolean hit = true;
            for(int j = 0; j < patternLen; j++) {
                if(cText[i+j] != cPattern[j]) hit = false;
            }
            if(hit) count++;
        }

        return count;
    }

    // Aufgabe e
    public static int dea_search_001001(String input) throws DFSM.DFSMException {
        DFSM dea = new DFSM(7, new char[]{'0', '1'});

        dea.setStartingState(0);
        dea.setAccepting(6);
        dea.addTransition(0, '0', 1);
        dea.addTransition(0, '1', 0);

        dea.addTransition(1, '0', 2);
        dea.addTransition(1, '1', 0);

        dea.addTransition(2, '1', 3);
        dea.addTransition(2, '0', 2);

        dea.addTransition(3, '0', 4);
        dea.addTransition(3, '1', 0);

        dea.addTransition(4, '0', 5);
        dea.addTransition(4, '1', 0);

        dea.addTransition(5, '1', 6);
        dea.addTransition(5, '0', 2);

        dea.addTransition(6, '1', 0);
        dea.addTransition(6, '0', 4);

        CountAccepted countAccepted = new CountAccepted();
        dea.runWithListener(input, countAccepted);

        return countAccepted.count;
    }
    public static class CountAccepted implements Listener {
        int count = 0;

        @Override
        public void accept(int pos) {
            count++;
        }
    }

}
