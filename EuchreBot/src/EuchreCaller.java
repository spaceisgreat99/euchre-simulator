import java.util.ArrayList;

public class EuchreCaller {

  private static final int NUM_SIMULATIONS = 10000;

  /**
   * Simulates many games and finds the expected number of points the player will get. Assumes the
   * player called the suit and thus the opposing team has the potential to set them
   * 
   * @param hand           The hand of player 1
   * @param trump          The suit which is trump, "" or "none" for no trump
   * @param leader         The player who is leading this round (1, 2, 3, or 4)
   * @param cardUp         The card which was turned up
   * @param playerWithCard The player (1, 2, 3, or 4) who picked up the card. If this value is 1,
   *                       negative, or greater than 4, it will be ignored. If this value is 0, the
   *                       card will be excluded from the hands of all 4 players. If this value is
   *                       2-4, it will be placed in that player's hand.
   * @return The average number of points the team with players 1 and 3 will get based on random
   *         simulations.
   */
  public static double findExpectedPoints(ArrayList<Card> hand, String trump, int leader,
      Card cardUp, int playerWithCard) {
    return findExpectedPoints(hand, trump, leader, cardUp, playerWithCard, false);
  }

  /**
   * Simulates many games and finds the expected number of points the player will get. Assumes the
   * player called the suit and thus the opposing team has the potential to set them
   * 
   * @param hand           The hand of player 1
   * @param trump          The suit which is trump, "" or "none" for no trump
   * @param leader         The player who is leading this round (1, 2, 3, or 4)
   * @param cardUp         The card which was turned up
   * @param playerWithCard The player (1, 2, 3, or 4) who picked up the card. If this value is 1,
   *                       negative, or greater than 4, it will be ignored. If this value is 0, the
   *                       card will be excluded from the hands of all 4 players. If this value is
   *                       2-4, it will be placed in that player's hand.
   * @param playerAlone    optional, whether player 1 is going alone
   * @return The average number of points the team with players 1 and 3 will get based on random
   *         simulations.
   */
  public static double findExpectedPoints(ArrayList<Card> hand, String trump, int leader,
      Card cardUp, int playerWithCard, boolean playerAlone) {
    int result = 0;
    int newLeader = (playerAlone && leader == 3) ? leader + 1 : leader;
    ArrayList<ArrayList<Card>> hands;
    for (int i = 0; i < NUM_SIMULATIONS; i++) {
      ArrayList<Card> availableCards = new ArrayList<Card>();
      ArrayList<Card> p1 = new ArrayList<Card>();
      ArrayList<Card> p2 = new ArrayList<Card>();
      ArrayList<Card> p3 = new ArrayList<Card>();
      ArrayList<Card> p4 = new ArrayList<Card>();
      // Populate player 1's hand
      for (int j = 0; j < hand.size(); j++) {
        p1.add(hand.get(j));
      }
      // Handle player possibly going alone
      if (playerAlone) {
        for (int j = 0; j < 5; j++) {
          p3.add(Card.noCard());
        }
      }
      // Generate valid cards for players 2-4
      for (Card c : deck) {
        availableCards.add(c);
      }
      for (Card c : p1) {
        for (Card d : deck) {
          if (Card.equals(c, d)) {
            availableCards.remove(d);
          }
        }
      }
      // Handle possible card up relevance
      if (!(cardUp == null)) {
        if (playerWithCard == 0) {
          for (int j = availableCards.size() - 1; j >= 0; j--) {
            if (Card.equals(availableCards.get(j), cardUp)) {
              availableCards.remove(j);
            }
          }
        }
      }
      // Generate hands 2-4
      for (int j = p2.size(); j < 5; j++) {
        p2.add(availableCards.remove((int) (Math.random() * availableCards.size())));
      }
      for (int j = p3.size(); j < 5; j++) {
        p3.add(availableCards.remove((int) (Math.random() * availableCards.size())));
      }
      for (int j = p4.size(); j < 5; j++) {
        p4.add(availableCards.remove((int) (Math.random() * availableCards.size())));
      }
      
      // more card up stuff
      if (playerWithCard == 1) {
        int lowestIndex = 0;
        int lowestScore = p1.get(0).valueCard("", trump);
        for (int j = 1; j < 5; j++) {
          if (p1.get(j).valueCard("", trump) < lowestScore) {
            lowestIndex = j;
            lowestScore = p1.get(j).valueCard("", trump);
          }
        }
        p1.remove(lowestIndex);
        p1.add(cardUp);
      }
      if (playerWithCard == 2) {
        int lowestIndex = 0;
        int lowestScore = p2.get(0).valueCard("", trump);
        for (int j = 1; j < 5; j++) {
          if (p2.get(j).valueCard("", trump) < lowestScore) {
            lowestIndex = j;
            lowestScore = p2.get(j).valueCard("", trump);
          }
        }
        p2.remove(lowestIndex);
        p2.add(cardUp);
      }
      if (playerWithCard == 3 && !playerAlone) {
        int lowestIndex = 0;
        int lowestScore = p3.get(0).valueCard("", trump);
        for (int j = 1; j < 5; j++) {
          if (p3.get(j).valueCard("", trump) < lowestScore) {
            lowestIndex = j;
            lowestScore = p3.get(j).valueCard("", trump);
          }
        }
        p3.remove(lowestIndex);
        p3.add(cardUp);
      }
      if (playerWithCard == 4) {
        int lowestIndex = 0;
        int lowestScore = p4.get(0).valueCard("", trump);
        for (int j = 1; j < 5; j++) {
          if (p4.get(j).valueCard("", trump) < lowestScore) {
            lowestIndex = j;
            lowestScore = p4.get(j).valueCard("", trump);
          }
        }
        p4.remove(lowestIndex);
        p4.add(cardUp);
      }
      
      
      hands = new ArrayList<ArrayList<Card>>();
      hands.add(p1);
      hands.add(p2);
      hands.add(p3);
      hands.add(p4);
      // finally, run the simulation
      int thisResult =
          playerWithCard == 0 ? Duchre.simulateGame(hands, trump, newLeader - 1, cardUp)
              : Duchre.simulateGame(hands, trump, newLeader - 1);
      if (playerAlone && thisResult == 2) {
        thisResult = 4;
      }
      result += thisResult < 0 ? thisResult * 2 : thisResult;
    }

    return (0.0 + result) / NUM_SIMULATIONS;
  }

  private static Card[] deck =
      new Card[] {new Card("a", "spades"), new Card("k", "spades"), new Card("q", "spades"),
          new Card("j", "spades"), new Card("10", "spades"), new Card("9", "spades"),
          new Card("a", "clubs"), new Card("k", "clubs"), new Card("q", "clubs"),
          new Card("j", "clubs"), new Card("10", "clubs"), new Card("9", "clubs"),
          new Card("a", "hearts"), new Card("k", "hearts"), new Card("q", "hearts"),
          new Card("j", "hearts"), new Card("10", "hearts"), new Card("9", "hearts"),
          new Card("a", "diamonds"), new Card("k", "diamonds"), new Card("q", "diamonds"),
          new Card("j", "diamonds"), new Card("10", "diamonds"), new Card("9", "diamonds")};

}
