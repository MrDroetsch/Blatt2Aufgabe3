package aufgabe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// Deterministic Finite State Machine (aufgabe.DFSM)
public class DFSM {
    // ------- inner classes -------
    protected class DeltaDomain {
        public int q;
        public char a;
        private int hash;
        DeltaDomain(int q, char a) {
            this.q = q;
            this.a = a;
            // Achtung! Passt so nur zu unserem Beispiel!
            hash = (Character.getNumericValue(a) << 1) ^ q;
        }
        @Override public int hashCode() {
            return hash;
        }
        @Override public boolean equals(Object o) {
            if(o instanceof DeltaDomain) {
                DeltaDomain rhs = (DeltaDomain) o;
                return(this.q == rhs.q) && (this.a == rhs.a);
            }
            return false;
        }
    }

    protected class DFSMException extends Exception {
        public DFSMException() {
            super("ERROR");
        }

        public DFSMException(String msg) {
            super(msg);
        }
    }

    // ------- aufgabe.DFSM -------

    // Zustände werden von 0 bis numStates−1 durchnummeriert
    protected int numStates;

    // Das Alphabet Sigma
    protected HashSet<Character> alphabet;

    // Die Menge der akzeptierenden Zustände
    protected HashSet<Integer> acceptingStates;

    // Die Transitionsfunktion delta
    protected HashMap<DeltaDomain, Integer> transitionFunction;

    // Der Startzustand
    protected int startingState;

    /*
     Initialisiert einen DEA mit 'numStates' Zuständen, dessen
     Eingabealphabet die Zeichen in 'alphabet' enthält.
    */
    public DFSM(int numStates, char[] alphabet) {
        this.numStates = numStates;

        this.alphabet = new HashSet<>();
        for(char letter : alphabet) {
            this.alphabet.add(letter);
        }

        startingState = 0;
        acceptingStates = new HashSet<>();
        transitionFunction = new HashMap<>();
    }

    private void check(int state) throws DFSMException {
        if(state >= numStates) throw new DFSMException("DEA hat nicht so viele Zustände: " + state + " >= " + numStates);
    }

    private void check(char sign) throws DFSMException {
        if(!alphabet.contains(sign)) throw new DFSMException("Das eingegebene Zeichen kommt nicht aus dem Alphabet des DEA: " + sign);
    }

    private void check(DeltaDomain delta) throws DFSMException {
        if(!transitionFunction.containsKey(delta)) throw new DFSMException("Der Zustand hat keine kante mit diesem Zeichen: [" + delta.a + "]");
    }

    public void setStartingState(int state) throws DFSMException {
        check(state);

        startingState = state;
    }

    // Fügt 'state' zur Menge der akzeptierenden Zustände hinzu
    public void setAccepting(int state) throws DFSMException {
        check(state);

        acceptingStates.add(state);
    }

    // Entfernt 'state' aus der Menge der akzeptierenden Zustände
    public void setRejecting(int state) throws DFSMException {
        if(!acceptingStates.contains(state)) throw new DFSMException("Der Zustand ist nicht akzeptierend und kann daher nicht " +
                "auf verwerfend gesetzt werden: " + state);

        acceptingStates.remove(state);
    }

    /*
    Erweitert die Transitionsfunktion; 'from' und 'to' sind Zustände,
    'with' ist die Beschriftung der Kante
    */
    public void addTransition(int from, char with, int to) throws DFSMException {
        check(from);
        check(to);
        check(with);

        DeltaDomain delta = new DeltaDomain(from, with);

        transitionFunction.put(delta, to);
    }

    /*
    Wendet den DEA auf die Eingabe 's' an und gibt true
    zurück, falls 's' akzeptiert wurde
    */
    public boolean run(String s) throws DFSMException {
        int curState = startingState;
        for(char letter : s.toCharArray()) {
            check(letter);
            DeltaDomain current = new DeltaDomain(curState, letter);
            check(current);
            System.out.print(curState + " -> ");
            curState = transitionFunction.get(current);
        }
        System.out.println(curState);
        return acceptingStates.contains(curState);
    }

    public void runWithListener(String input, Listener listener) throws DFSMException {
        int curState = startingState;
        int pos = 0;
        if(acceptingStates.contains(curState)) {
            listener.accept(pos);
        }
        for(char letter : input.toCharArray()) {
            check(letter);
            DeltaDomain current = new DeltaDomain(curState, letter);
            check(current);
            curState = transitionFunction.get(current);
            pos++;
            if(acceptingStates.contains(curState)) {
                listener.accept(pos);
            }
        }
    }

}