import java.util.ArrayList;

public class Card {
  private String suit;
  private String value;
  private String team = null;

  private static final String[] VALUES = new String[] {"a", "k", "q", "j", "10", "9"};

  public static boolean isValidSuit(String suit) {
    return (suit.equals("hearts") || suit.equals("diamonds") || suit.equals("clubs")
        || suit.equals("spades") || suit.equals("none") || suit.equals(""));
  }

  public Card(String value, String suit) {
    this.suit = suit;
    this.value = value;
  }

  public Card(String value, String suit, String team) {
    this.suit = suit;
    this.value = value;
    this.team = team;
  }

  public String getSuit() {
    return suit;
  }

  public String getSuit(String trump) {
    if (value.equals("j") && this.getColor().equals(getColor(trump))) {
      return trump;
    }
    return this.getSuit();
  }

  public String getValue() {
    return value;
  }

  public String getTeam() {
    return team;
  }

  public String getColor() {
    return ((suit.equals("hearts") || suit.equals("diamonds")) ? "red" : "black");
  }

  public static String getColor(String suit) {
    if (suit.equals("hearts") || suit.equals("diamonds")) {
      return "red";
    }
    if (suit.equals("spades") || suit.equals("clubs")) {
      return "black";
    }
    return "none";
  }

  public String toString() {
    return value.toUpperCase() + " of " + suit.toUpperCase();
  }

  public boolean equals(Card other) {
    if (other == null) {
      return false;
    }
    return this.getSuit().equals(other.getSuit()) && this.getValue().equals(other.getValue());
  }

  public static boolean equals(Card c1, Card c2) {
    return c1.getSuit().equals(c2.getSuit()) && c1.getValue().equals(c2.getValue());
  }

  public static boolean contains(ArrayList<Card> cards, Card cardToFind) {
    for (Card c : cards) {
      if (cardToFind.equals(c)) {
        return true;
      }
    }
    return false;
  }

  // Finds winner between two cards. Assumes cards are NOT identical.
  public static Card findWinner(String trump, String lead, Card c1, Card c2) {
    if (c1 == null) {
      return c2;
    }
    if (c2 == null) {
      return c1;
    }
    if (c1.getValue().equals("")) {
      return c2;
    }
    if (c2.getValue().equals("")) {
      return c1;
    }
    if (!isValidSuit(trump) || !isValidSuit(lead)) {
      System.out.println("Invalid suit: " + trump + " or " + lead);
      return null;
    }
    // Check for bowers
    if (c1.getSuit().equals(trump) && c1.getValue().equals("j")) {
      return c1;
    }
    if (c2.getSuit().equals(trump) && c2.getValue().equals("j")) {
      return c2;
    }
    if (c1.getColor().equals(getColor(trump)) && c1.getValue().equals("j")) {
      return c1;
    }
    if (c2.getColor().equals(getColor(trump)) && c2.getValue().equals("j")) {
      return c2;
    }
    // Check if one card is trump
    if (c1.getSuit().equals(trump) && !c2.getSuit().equals(trump)) {
      return c1;
    }
    if (!c1.getSuit().equals(trump) && c2.getSuit().equals(trump)) {
      return c2;
    }
    // Check if one card is lead suit
    if (c1.getSuit().equals(lead) && !c2.getSuit().equals(lead)) {
      return c1;
    }
    if (!c1.getSuit().equals(lead) && c2.getSuit().equals(lead)) {
      return c2;
    }
    // Now assume the higher card wins. If neither cards are of the lead suit or trump, the higher
    // one will be returned but should be filtered out at a higher level.
    // First return c1 if the cards are the same value, for no particular reason as it shouldn't be
    // important at a higher level.
    if (c1.getValue().equals(c2.getValue())) {
      return c1;
    }
    for (int i = 0; i < VALUES.length; i++) {
      if (c1.getValue().equals(VALUES[i])) {
        return c1;
      }
      if (c2.getValue().equals(VALUES[i])) {
        return c2;
      }
    }
    return c1; // We only want to return null in case of an error. Again, shouldn't be important as
               // this function is not necessarily responsible for data validation.
  }

  public static Card findWinner(String trump, String lead, Card c1, Card c2, Card c3) {
    return findWinner(trump, lead, c1, findWinner(trump, lead, c2, c3));
  }

  public static Card findWinner(String trump, String lead, Card c1, Card c2, Card c3, Card c4) {
    return findWinner(trump, lead, findWinner(trump, lead, c1, c2),
        findWinner(trump, lead, c3, c4));
  }

  public static Card leftBower(String trump) {
    if (trump.equals("hearts")) {
      return new Card("j", "diamonds");
    }
    if (trump.equals("diamonds")) {
      return new Card("j", "hearts");
    }
    if (trump.equals("spades")) {
      return new Card("j", "clubs");
    }
    if (trump.equals("clubs")) {
      return new Card("j", "spades");
    }
    return new Card("j", "");
  }

  public static Card noCard() {
    return new Card("", "n"); // Suit is n to avoid it being mistaken for a trump card when there is
                              // no trump
  }

  /**
   * Evalutes the card
   * 
   * @param lead  the lead, "" if no lead yet
   * @param trump the trump, "" if no trump
   * @return the card's value, on a relative scale
   */
  public int valueCard(String lead, String trump) {
    return valueCard(lead, trump, new ArrayList<Card>());
  }

  public int valueCard(String lead, String trump, ArrayList<Card> playedCards) {
    if (value.equals("")) {
      return 0; // Assume this is a noCard and thus has no value
    }
    int retValue = 0;
    // bowers
    if (suit.equals(trump) && value.equals("j")) {
      return 1500; // r bower
    }
    if (this.getColor().equals(getColor(trump)) && value.equals("j")) {
      return 1000; // l bower
    }
    // not a bower
    for (int i = 0; i < VALUES.length; i++) {
      if (this.value.equals(VALUES[i])) {
        retValue = VALUES.length - i;
      }
    }
    // this adds extra bonuses if cards above the card have already been played
    if (!suit.equals(trump)) {
      if (playedCards.contains(new Card("a", suit))) {
        retValue++;
      }
      if (!value.equals("a")) {
        if (playedCards.contains(new Card("k", suit))) {
          retValue++;
        }
        if (!value.equals("k")) {
          if (playedCards.contains(new Card("q", suit))) {
            retValue++;
          }
          if (!value.equals("q")) {
            if (playedCards.contains(new Card("j", suit))) {
              retValue++;
            }
            if (!value.equals("j")) {
              if (playedCards.contains(new Card("10", suit))) {
                retValue++;
              }
            }
          }
        }
      }
    }
    if (suit.equals(trump)) {
      return retValue * 100; // this avoids double couting trump bonus and lead bonus
    }
    if (suit.equals(lead)) {
      return retValue * 10;
    }
    return retValue;
  }

}
