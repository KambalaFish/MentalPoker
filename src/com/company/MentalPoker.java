package com.company;

import java.util.*;
import java.util.stream.Collectors;

public class MentalPoker {
    private final int p;
    private final Random random;

    public MentalPoker(int p){
        this.p = p;
        random = new Random();
    }

    private int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private void generateCards(ArrayList<Integer> cards){
        for (int i=0; i<3;i++){
            int newCard = generateRandomNumber(2, p-1);
            while (cards.contains(newCard)){
                newCard = generateRandomNumber(2, p-1);
            }
            cards.add(newCard);
        }
    }

    private EuclidRow generalizedEuclidAlgorithm(int a, int b){
        if (b>a){
            a = a + b;
            b = a - b;
            a = a - b;
        }
        EuclidRow u = new EuclidRow(a, 1, 0);
        EuclidRow v = new EuclidRow(b, 0, 1);
        EuclidRow t = new EuclidRow(0,0,0);

        while (v.gcd!=0){
            int q = u.gcd / v.gcd;
            t.gcd = u.gcd % v.gcd;
            t.a = u.a - q * v.a;
            t.b = u.b - q * v.b;
            u.set(v);
            v.set(t);
        }
        return u;
    }

    private int getC(){
        int c = generateRandomNumber(2, p-2);
        while (generalizedEuclidAlgorithm(p-1, c).gcd!=1){
            c = generateRandomNumber(2, p-2);
        }
        return c;
    }

    private int getD(int c){
        int d = generalizedEuclidAlgorithm(p-1, c).b;
        if (d<0)
            return d + (p-1);
        return d;
    }

    private int calculatePowerByMod(int base, int power) {
        int result = 1;
        while (power > 0) {
            if ((power & 1) == 1)
                result = (result * base) % p;
            base = (base * base) % p;
            power = power >> 1;
        }
        return result;
    }

    private void shakeCards(ArrayList<Integer> cards){
        int shift = generateRandomNumber(0, cards.size() - 1);
        Collections.rotate(cards, -shift);
     }


    public void distributeCards(){
        int cA = getC(), dA = getD(cA);
        System.out.println("Alice picked her secret keys. cA = "+cA+", dA = "+dA);
        int cB = getC(), dB = getD(cB);
        System.out.println("Bob picked his secret keys. cB = "+cB+", dB = "+dB);

        ArrayList<Integer> cards = new ArrayList<>();
        generateCards(cards);
        System.out.println("Alice picks three random numbers for cards: " + cards.get(0)+", "+cards.get(1)+", "+cards.get(2));
        System.out.println();
        cards = (ArrayList<Integer>) cards.stream().map(card -> calculatePowerByMod(card, cA)).collect(Collectors.toList());
        System.out.println("Alice calculates numbers by formula: u = cardNumber^cA mod p. And she gets numbers: "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()
        );
        shakeCards(cards);
        System.out.println("Alice shook calculated numbers. Now Alice sends to Bob following sequence of u numbers: "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()
        );
        System.out.println();
        System.out.println("Bob received "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()+" from Alice."
        );

        int indexForAliceCard = generateRandomNumber(0, cards.size()-1);
        int aliceCardU = cards.get(indexForAliceCard);
        cards.remove(indexForAliceCard);
        System.out.println("Bob randomly picked for Alice number: "+aliceCardU+". Bob sends the number to Alice.");
        int initialAliceCard = calculatePowerByMod(aliceCardU, dA);
        System.out.println("Alice received the number from Bob and learns her card by calculating: u^dA mod p = cardNumber^(cA*dA) = "+initialAliceCard);
        System.out.println();
        cards = (ArrayList<Integer>) cards.stream().map(card -> calculatePowerByMod(card, cB)).collect(Collectors.toList());
        System.out.println("Bob continues his actions and calculates remaining two numbers by formula: v = u^cB mod p. Those numbers are: "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()
        );
        shakeCards(cards);
        System.out.println("Bob shook calculated numbers. Now Bob sends to Alice following sequence of v numbers: "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()
        );
        System.out.println("Alice received "+
                cards.stream().map(Object::toString).reduce((a, b) -> a+", "+b).get()+" from Bob."
        );

        int indexForBobCard = generateRandomNumber(0, cards.size()-1);
        int bobCardV = cards.get(indexForBobCard);
        cards.remove(indexForBobCard);
        System.out.println("Alice randomly picked for Bob following v number: "+bobCardV);
        int bobCardW = calculatePowerByMod(bobCardV, dA);
        System.out.println("Alice calculated w by formula: w = v^dA mod p = "+bobCardW+" and sent the number to Bob");
        int initialBobCard = calculatePowerByMod(bobCardW, dB);
        System.out.println("Bob received w number = "+bobCardW+" from Alice.");
        System.out.println("Bob calculated z by formula: z=w^dB = "+initialBobCard+". Now Bob knows his card.");

    }
}