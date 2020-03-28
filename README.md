# Poker Hand Analyzer

## 1.0
My personal project for CPSC 210.

This is a poker hand analyzer that will display odds in real time as the user add cards to the board.
I was inspired to make this program because a few friends of mine played poker and even though I'm not that good, I am very interested in the game.
So I decided that I would make something which could be useful for both beginners and experienced players.
I think any person that's interested in poker or in probability would be someone who could you this application.

Features:
- Allows user to **add** cards to their hand and/or opponents or have hands randomly generated
- Allows user to **change** their hand and/or their opponents hand
- Allows user to **view** the odds of their hand of winning
- Allows user to **add** cards to the table
- Allows user to **choose** what cards get placed on the board or have board cards randomly generated

## 2.0
Features:
- Allows user to **save** the state of the table (player hands and deck).
- Allows user to **load** their saved table by name

## 3.0
Features:
- Click on cards from top to select them, click anywhere else on screen to unselect card.
- When a card is selected, click on any of the empty slots to add card to that section.
- When no card is selected, lick on section (board, or each players hand) to remove last card that was added to the section.
- To load old hand, type the name of the hand into the text input below "load" button then hit load
- To save current hand, type name into text input under "save" button then press save

## Phase 4: Task 2
**Type Hierarchy**
- Clickable abstract class.
- Extended by Card, Table, Player classes.
- Each of the classes that extends Clickable overides the containsX abstract method.