import java.util.ArrayList;

public class Player {

  private String team;
  private boolean pickedUpCard = false;
  private ArrayList<String> illegalSuits = new ArrayList<String>();

  public Player(String team) {
    this.team = team;
  }

  public boolean hasPickedUpCard() {
    return pickedUpCard;
  }

  public ArrayList<String> getIllegalSuits() {
    return illegalSuits;
  }

  public void addIllegalSuit(String suit) {
    illegalSuits.add(suit);
  }

  public ArrayList<String> getLegalSuits() {
    ArrayList<String> legalSuits = new ArrayList<String>();
    if (!illegalSuits.contains("spades")) {
      legalSuits.add("spades");
    }
    if (!illegalSuits.contains("clubs")) {
      legalSuits.add("clubs");
    }
    if (!illegalSuits.contains("diamonds")) {
      legalSuits.add("diamonds");
    }
    if (!illegalSuits.contains("hearts")) {
      legalSuits.add("hearts");
    }
    return legalSuits;
  }

  public String onlySuit() {
    if (illegalSuits.size() < 3) {
      return "";
    }
    if (illegalSuits.contains("spades")) {
      if (illegalSuits.contains("clubs")) {
        if (illegalSuits.contains("hearts")) {
          return "diamonds";
        }
        return "hearts";
      }
      return "clubs";
    }
    return "spades";
  }

  public String getTeam() {
    return team;
  }

}
