import java.util.ArrayList;

public class Duchre {

  // creates game with no played cards
  public static int simulateGame(ArrayList<ArrayList<Card>> hands, String trump, int firstLeader) {
    return simulateGame(hands, trump, firstLeader, new ArrayList<Card>());
  }

  // creates a game with on played card
  public static int simulateGame(ArrayList<ArrayList<Card>> hands, String trump, int firstLeader,
      Card playedCard) {
    ArrayList<Card> playedCards = new ArrayList<Card>();
    playedCards.add(playedCard);
    return simulateGame(hands, trump, firstLeader, playedCards);
  }

  // main function to actually simulate a game; returns expected points for team w/ players 0 & 2
  public static int simulateGame(ArrayList<ArrayList<Card>> hands, String trump, int firstLeader,
      ArrayList<Card> playedCards) {
    int team13score = 0;
    Card[] currHand = new Card[] {null, null, null, null};
    int currLeader = firstLeader;
    Card currWinner;
    // Simulate each round
    for (int i = 0; i < 5; i++) {
      for (int j = currLeader; j < currLeader + 4; j++) {
        // pick and play card
        currHand[j - currLeader] = pickCard(hands.get(j % 4), trump, currHand, playedCards);
        hands.get(j % 4).remove(currHand[j - currLeader]);
        playedCards.add(currHand[j - currLeader]);
      }
      String currSuit = currHand[0] == null ? ""
          : (currHand[0].getValue().equals("")
              ? (currHand[1] == null ? "" : currHand[1].getSuit(trump))
              : currHand[0].getSuit(trump));
      currWinner =
          Card.findWinner(trump, currSuit, currHand[0], currHand[1], currHand[2], currHand[3]);
      // check who won the hand and respond accordingly
      for (int j = 0; j < 4; j++) {
        if (currWinner.equals(currHand[j])) {
          team13score += (j + currLeader) % 2;
          currLeader = (j + currLeader) % 4;
        }
      }
      currHand = new Card[] {null, null, null, null};
    }
    // return value depending on score of team 1-3 (not 0-2)
    if (team13score == 0) {
      return 2;
    } else if (team13score == 5) {
      return -2;
    } else if (team13score < 3) {
      return 1;
    } else {
      return -1;
    }
  }

  // imperfect but pretty good function for selecting which card should be played given game state
  private static Card pickCard(ArrayList<Card> playerHand, String trump, Card[] currHand,
      ArrayList<Card> playedCards) {
    String currSuit = currHand[0] == null ? ""
        : (currHand[0].getValue().equals("")
            ? (currHand[1] == null ? "" : currHand[1].getSuit(trump))
            : currHand[0].getSuit(trump));
    if (!Card.isValidSuit(currSuit)) {
      System.out.println("Invalid currSuit: " + currSuit);
    }
    // Weight all cards
    int[] weights = new int[playerHand.size()];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = playerHand.get(i).valueCard(currSuit, trump);
    }
    if (currHand[0] == null) {
      // All cards are valid
      // play highest trump left in game OR highest nontrump
      // these ifs check if the top x trump are out and thus the player should lead with the highest
      // trump left in the game
      for (int i = 0; i < weights.length; i++) {
        if (weights[i] == 1500) {
          return playerHand.get(i);
        }
        if (Card.contains(playedCards, new Card("j", trump))) {
          if (weights[i] == 1000) {
            return playerHand.get(i);
          }
          if (Card.contains(playedCards, Card.leftBower(trump))) {
            if (weights[i] == 600) {
              return playerHand.get(i);
            }
            if (Card.contains(playedCards, new Card("a", trump))) {
              if (weights[i] == 500) {
                return playerHand.get(i);
              }
              if (Card.contains(playedCards, new Card("k", trump))) {
                if (weights[i] == 400) {
                  return playerHand.get(i);
                }
                if (Card.contains(playedCards, new Card("q", trump))) {
                  if (weights[i] == 200) {
                    return playerHand.get(i);
                  }
                  if (Card.contains(playedCards, new Card("10", trump))) {
                    if (weights[i] == 100) {
                      return playerHand.get(i);
                    }
                  }
                }
              }
            }
          }
        }
      }
      // play highest nontrump
      int bestIndex = 0;
      int bestScore = weights[0];
      for (int i = 1; i < weights.length; i++) {
        if (weights[i] > bestScore && weights[i] < 100) {
          bestIndex = i;
          bestScore = weights[i];
        }
      }
      return playerHand.get(bestIndex);
    }

    if (currHand[1] == null) {
      // One card has been played
      // Amplify weights if card is of correct suit.
      int bestIndex = 0;
      int bestScore = weights[0];
      int card1s = currHand[0].valueCard(currSuit, trump);
      for (int i = 1; i < weights.length; i++) {
        // If card can beat first card, play it
        if (weights[i] > card1s && bestScore < card1s) {
          bestIndex = i;
          bestScore = weights[i];
        }
        // If lower card still wins, play it
        if (weights[i] > card1s && weights[i] < bestScore) {
          bestIndex = i;
          bestScore = weights[i];
        }
        // If card is worse than best card AND best card cannot win, play lower card
        if (weights[i] < bestScore && bestScore < card1s) {
          bestIndex = i;
          bestScore = weights[i];
        }
      }
      // Another for loop that only tracks cards of the proper suit.
      for (int i = 0; i < weights.length; i++) {
        if (!playerHand.get(i).getSuit(trump).equals(currSuit)) {
          continue; // If suit is not leading suit, skip
        }
        if (!playerHand.get(bestIndex).getSuit(trump).equals(currSuit)) {
          bestIndex = i;
          bestScore = weights[i]; // If current card is wrong suit, replace it with this card
        }
        // If card can beat first card, play it
        if (weights[i] > bestScore && weights[i] > card1s) {
          bestIndex = i;
          bestScore = weights[i];
        }
        // If card is worse than best card AND best card cannot win, play lower card
        if (weights[i] < bestScore && bestScore < card1s) {
          bestIndex = i;
          bestScore = weights[i];
        }
      }
      return playerHand.get(bestIndex);
    }

    if (currHand[2] == null) {
      // Two cards have been played
      // If teammate is winning, do not try to take trick except for certain exceptions
      if (Card.findWinner(trump, currSuit, currHand[0], currHand[1]).equals(currHand[0])) {
        // turn weight negative to account for exceptions
        // trump case
        if (currHand[0].getSuit(trump).equals(trump)) {
          if (currHand[0].getValue().equals("9")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")
                    || playerHand.get(i).getValue().equals("k")
                    || playerHand.get(i).getValue().equals("q")
                    || playerHand.get(i).getValue().equals("j")) {
                  weights[i] *= -1;
                }
              } else if (playerHand.get(i).getColor().equals(Card.getColor(currSuit))
                  && playerHand.get(i).getValue().equals("j")) {
                weights[i] *= -1;
              }
            }
          }
          if (currHand[0].getValue().equals("10")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")
                    || playerHand.get(i).getValue().equals("k")
                    || playerHand.get(i).getValue().equals("j")) {
                  weights[i] *= -1;
                }
              } else if (playerHand.get(i).getColor().equals(Card.getColor(currSuit))
                  && playerHand.get(i).getValue().equals("j")) {
                weights[i] *= -1;
              }
            }
          }
          if (currHand[0].getValue().equals("q")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")
                    || playerHand.get(i).getValue().equals("j")) {
                  weights[i] *= -1;
                }
              } else if (playerHand.get(i).getColor().equals(Card.getColor(currSuit))
                  && playerHand.get(i).getValue().equals("j")) {
                weights[i] *= -1;
              }
            }
          }
          if (currHand[0].getValue().equals("k")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("j")) {
                  weights[i] *= -1;
                }
              } else if (playerHand.get(i).getColor().equals(Card.getColor(currSuit))
                  && playerHand.get(i).getValue().equals("j")) {
                weights[i] *= -1;
              }
            }
          }
          if (currHand[0].getValue().equals("a")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("j")) {
                  weights[i] *= -1;
                }
              }
            }
          }
        } else {
          // non trump case
          if (currHand[0].getValue().equals("9") || currHand[0].getValue().equals("10")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")
                    || playerHand.get(i).getValue().equals("k")
                    || playerHand.get(i).getValue().equals("q")) {
                  weights[i] *= -1;
                }
              }
            }
          }
          if (currHand[0].getValue().equals("j")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")
                    || playerHand.get(i).getValue().equals("k")) {
                  weights[i] *= -1;
                }
              }
            }
          }
          if (currHand[0].getValue().equals("q")) {
            for (int i = 0; i < weights.length; i++) {
              if (playerHand.get(i).getSuit(trump).equals(currSuit)) {
                if (playerHand.get(i).getValue().equals("a")) {
                  weights[i] *= -1;
                }
              }
            }
          }
        }
        // now simply return the lowest scoring card
        int bestIndex = 0;
        int bestScore = playerHand.get(0).valueCard(currSuit, trump);
        for (int i = 1; i < weights.length; i++) {
          // If card can beat first card, play it
          if (weights[i] < bestScore) {
            bestIndex = i;
            bestScore = weights[i];
          }
        }
        // Another for loop that only tracks cards of the proper suit.
        for (int i = 0; i < weights.length; i++) {
          if (!playerHand.get(i).getSuit(trump).equals(currSuit)) {
            continue; // If suit is not leading suit, skip
          }
          if (!playerHand.get(bestIndex).getSuit(trump).equals(currSuit)) {
            bestIndex = i;
            bestScore = weights[i]; // If current card is wrong suit, replace it with this card
          }
          if (weights[i] < bestScore) {
            bestIndex = i;
            bestScore = weights[i];
          }
        }
        return playerHand.get(bestIndex);
      }
      // assume teammate is not winning
      // try to take trick w/ highest nontrump or lowest trump card that can
      int bestIndex = 0;
      int bestScore = weights[0];
      int currLeader = currHand[1].valueCard(currSuit, trump);
      for (int i = 1; i < weights.length; i++) {
        if (weights[i] > currLeader && playerHand.get(i).getSuit(trump).equals(trump)) {
          if (weights[i] < bestScore) {
            // If card is trump and can win, choose lowest one
            bestScore = weights[i];
            bestIndex = i;
          }
        }
      }
      // Another for loop that only tracks cards of the proper suit.
      for (int i = 0; i < weights.length; i++) {
        if (!playerHand.get(i).getSuit(trump).equals(currSuit)) {
          continue; // If suit is not leading suit, skip
        }
        if (!playerHand.get(bestIndex).getSuit(trump).equals(currSuit)) {
          bestIndex = i;
          bestScore = weights[i]; // If current card is wrong suit, replace it with this card
        }
        if (weights[i] > bestScore && weights[i] > currLeader) {
          bestIndex = i;
          bestScore = weights[i]; // If card wins and is higher, play it
        }
        if (weights[i] < bestScore && bestScore < currLeader) {
          bestIndex = i;
          bestScore = weights[i]; // If neither card cannot win, choose lwoer one.
        }
      }
      return playerHand.get(bestIndex);
    }
    if (currHand[3] != null) {
      System.out.println("Error: pickCard was passed a full trick");
      return null;
    }
    // Now assume we are the last player
    // If our teammate is winning, play lowest value card.
    if (Card.findWinner(trump, currSuit, currHand[0], currHand[1], currHand[2])
        .equals(currHand[1])) {
      int bestIndex = 0;
      int bestScore = weights[0];
      for (int i = 1; i < weights.length; i++) {
        if (weights[i] < bestScore) {
          bestIndex = i;
          bestScore = weights[i]; // choose lowest card
        }
      }
      // for loop with only trump
      for (int i = 0; i < weights.length; i++) {
        if (!playerHand.get(i).getSuit(trump).equals(currSuit)) {
          continue; // If suit is not leading suit, skip
        }
        if (!playerHand.get(bestIndex).getSuit(trump).equals(currSuit)) {
          bestIndex = i;
          bestScore = weights[i]; // If current card is wrong suit, replace it with this card
        }
        if (weights[i] < bestScore) {
          bestIndex = i;
          bestScore = weights[i]; // If card is lower, choose it
        }
      }
      return playerHand.get(bestIndex);
    }
    // Assume we are currently losing and try to take trick
    int bestIndex = 0;
    int bestScore = weights[0];
    int currLeader = Card.findWinner(trump, currSuit, currHand[0], currHand[1], currHand[2])
        .valueCard(currSuit, trump);
    for (int i = 1; i < weights.length; i++) {
      if (weights[i] > currLeader && bestScore < currLeader) {
        bestScore = weights[i];
        bestIndex = i; // If we are not winning, try to win
      }
      if (weights[i] > currLeader && weights[i] < bestScore) {
        bestScore = weights[i];
        bestIndex = i; // Choose lowest card that can win
      }
      if (weights[i] < bestScore && bestScore < currLeader) {
        bestIndex = i;
        bestScore = weights[i];
      }
    }
    // Another for loop that only tracks cards of the proper suit.
    for (int i = 0; i < weights.length; i++) {
      if (!playerHand.get(i).getSuit(trump).equals(currSuit)) {
        continue; // If suit is not leading suit, skip
      }
      if (!playerHand.get(bestIndex).getSuit(trump).equals(currSuit)) {
        bestIndex = i;
        bestScore = weights[i]; // If current card is wrong suit, replace it with this card
      }
      if (weights[i] < bestScore && weights[i] > currLeader) {
        bestIndex = i;
        bestScore = weights[i]; // If card wins and is lower, play it
      }
      if (weights[i] < bestScore && bestScore < currLeader) {
        bestIndex = i;
        bestScore = weights[i]; // If both lose, play lower card
      }
    }
    return playerHand.get(bestIndex);
  }
}
