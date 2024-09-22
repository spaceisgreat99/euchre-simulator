import java.util.ArrayList;

public class Tester {

  public static void main(String[] args) {

    // INITIAL VALUES (User inputs)

    /**
     * SUITS: 1 = spades; 2 = clubs; 3 = hearts; 4 = diamonds;
     * 
     * VALUES: 1 = ace; 2 = king; 3 = queen; 4 = jack; 5 = ten; 6 = nine;
     */
    int suits = 31342; // Input player one's suits, as a single 5-digit number
    int vals = 42622; // Input player one's values, as a single 5-digit number
    int dealer = 4; // Input the player dealing, relative to player one (e.g. player who goes after
                    // player one is player two, etc)
    int cardUpSuit = 4; // Input the suit of the card up
    int cardUpValue = 1; // Input the value of the card up

    // Process user inputs

    Card cardUp = deck[cardUpSuit - 1][cardUpValue - 1];
    ArrayList<Card> p1 = new ArrayList<Card>();
    p1.add(deck[suits / 10000 - 1][vals / 10000 - 1]);
    p1.add(deck[(suits / 1000) % 10 - 1][(vals / 1000) % 10 - 1]);
    p1.add(deck[(suits / 100) % 10 - 1][(vals / 100) % 10 - 1]);
    p1.add(deck[(suits / 10) % 10 - 1][(vals / 10) % 10 - 1]);
    p1.add(deck[suits % 10 - 1][vals % 10 - 1]);

    int playerLeading = dealer % 4 + 1;

    // SIMULATION

    // allSuitTest(p1, playerLeading); // simply tests all suits, does not account for card up
    pickUpTest(p1, playerLeading, cardUp); // tests all possibilities given card up

  }

  // calculates expected points for all suits and no trump, ignoring middle card up
  private static void allSuitTest(ArrayList<Card> p1, int playerLeading) {
    System.out.println("Player 1 hand: " + p1.get(0) + ", " + p1.get(1) + ", " + p1.get(2) + ", "
        + p1.get(3) + ", " + p1.get(4));
    System.out.println("Player " + (playerLeading == 1 ? 4 : playerLeading - 1)
        + " is dealer; Player " + playerLeading + " goes first.");
    double clubResult =
        EuchreCaller.findExpectedPoints(p1, "clubs", playerLeading, null, -1, false);
    double diamondResult =
        EuchreCaller.findExpectedPoints(p1, "diamonds", playerLeading, null, -1, false);
    double heartsResult =
        EuchreCaller.findExpectedPoints(p1, "hearts", playerLeading, null, -1, false);
    double spadesResult =
        EuchreCaller.findExpectedPoints(p1, "spades", playerLeading, null, -1, false);
    double noTrumpResult = EuchreCaller.findExpectedPoints(p1, "", playerLeading, null, -1, false);
    double clubResultA =
        EuchreCaller.findExpectedPoints(p1, "clubs", playerLeading, null, -1, true);
    double diamondResultA =
        EuchreCaller.findExpectedPoints(p1, "diamonds", playerLeading, null, -1, true);
    double heartsResultA =
        EuchreCaller.findExpectedPoints(p1, "hearts", playerLeading, null, -1, true);
    double spadesResultA =
        EuchreCaller.findExpectedPoints(p1, "spades", playerLeading, null, -1, true);
    double noTrumpResultA = EuchreCaller.findExpectedPoints(p1, "", playerLeading, null, -1, true);
    System.out.println("Expected points for Players 1 & 3 by trump: ");
    System.out.println("       Clubs: " + (clubResult + "                 ").substring(0, 14)
        + "Alone: " + clubResultA);
    System.out.println("    Diamonds: " + (diamondResult + "                 ").substring(0, 14)
        + "Alone: " + diamondResultA);
    System.out.println("      Hearts: " + (heartsResult + "                 ").substring(0, 14)
        + "Alone: " + heartsResultA);
    System.out.println("      Spades: " + (spadesResult + "                 ").substring(0, 14)
        + "Alone: " + spadesResultA);
    System.out.println("    No trump: " + (noTrumpResult + "                 ").substring(0, 14)
        + "Alone: " + noTrumpResultA);
  }

  // calculates expected points for all suits and no trump, accounting for card up and including the
  // possibility of going alone
  private static void pickUpTest(ArrayList<Card> p1, int playerLeading, Card cardUp) {
    String pickUpSuit = cardUp.getSuit();
    int playerWithCard = (playerLeading == 1 ? 4 : playerLeading - 1);
    double suitResult =
        EuchreCaller.findExpectedPoints(p1, pickUpSuit, playerLeading, cardUp, playerWithCard);
    double suitResultA = EuchreCaller.findExpectedPoints(p1, pickUpSuit, playerLeading, cardUp,
        playerWithCard, true);
    double clubResult =
        EuchreCaller.findExpectedPoints(p1, "clubs", playerLeading, cardUp, 0, false);
    double diamondResult =
        EuchreCaller.findExpectedPoints(p1, "diamonds", playerLeading, cardUp, 0, false);
    double heartsResult =
        EuchreCaller.findExpectedPoints(p1, "hearts", playerLeading, cardUp, 0, false);
    double spadesResult =
        EuchreCaller.findExpectedPoints(p1, "spades", playerLeading, cardUp, 0, false);
    double noTrumpResult = EuchreCaller.findExpectedPoints(p1, "", playerLeading, cardUp, 0, false);
    double clubResultA =
        EuchreCaller.findExpectedPoints(p1, "clubs", playerLeading, cardUp, 0, true);
    double diamondResultA =
        EuchreCaller.findExpectedPoints(p1, "diamonds", playerLeading, cardUp, 0, true);
    double heartsResultA =
        EuchreCaller.findExpectedPoints(p1, "hearts", playerLeading, cardUp, 0, true);
    double spadesResultA =
        EuchreCaller.findExpectedPoints(p1, "spades", playerLeading, cardUp, 0, true);
    double noTrumpResultA = EuchreCaller.findExpectedPoints(p1, "", playerLeading, null, 0, true);

    System.out.println("Player 1 hand: " + p1.get(0) + ", " + p1.get(1) + ", " + p1.get(2) + ", "
        + p1.get(3) + ", " + p1.get(4));
    System.out
        .println("Player " + (playerLeading == 1 ? 4 : playerLeading - 1) + " is dealer; Player "
            + playerLeading + " goes first; Player " + playerWithCard + " picks up " + cardUp);
    System.out.println("Expected points for Players 1 & 3 after pickup: ");
    System.out
        .println(("          " + pickUpSuit.substring(0, 1).toUpperCase() + pickUpSuit.substring(1))
            .substring(pickUpSuit.length()) + ": " + suitResult + "          Alone: "
            + suitResultA);
    System.out.println("Possible calls after pass:");
    if (!pickUpSuit.equals("clubs")) {
      System.out.println("       Clubs: " + (clubResult + "                 ").substring(0, 14)
          + "Alone: " + clubResultA);
    }
    if (!pickUpSuit.equals("diamonds")) {
      System.out.println("    Diamonds: " + (diamondResult + "                 ").substring(0, 14)
          + "Alone: " + diamondResultA);
    }
    if (!pickUpSuit.equals("hearts")) {
      System.out.println("      Hearts: " + (heartsResult + "                 ").substring(0, 14)
          + "Alone: " + heartsResultA);
    }
    if (!pickUpSuit.equals("spades")) {
      System.out.println("      Spades: " + (spadesResult + "                 ").substring(0, 14)
          + "Alone: " + spadesResultA);
    }
    System.out.println("    No trump: " + (noTrumpResult + "                 ").substring(0, 14)
        + "Alone: " + noTrumpResultA);
  }

  // array to make it easier to reference cards
  private static Card[][] deck = new Card[][] {
      {new Card("a", "spades"), new Card("k", "spades"), new Card("q", "spades"),
          new Card("j", "spades"), new Card("10", "spades"), new Card("9", "spades")},
      {new Card("a", "clubs"), new Card("k", "clubs"), new Card("q", "clubs"),
          new Card("j", "clubs"), new Card("10", "clubs"), new Card("9", "clubs")},
      {new Card("a", "hearts"), new Card("k", "hearts"), new Card("q", "hearts"),
          new Card("j", "hearts"), new Card("10", "hearts"), new Card("9", "hearts")},
      {new Card("a", "diamonds"), new Card("k", "diamonds"), new Card("q", "diamonds"),
          new Card("j", "diamonds"), new Card("10", "diamonds"), new Card("9", "diamonds")}};

}
