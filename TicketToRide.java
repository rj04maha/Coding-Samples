/**
 * This is part of a Java applet that I and 4 others wrote in college in my Object Oriented Programming course. We used this file as the 'front 
 * end' of the program which uses a Mouse Listener to check where on the screen the user has clicked and then does the appropriate action based
 * on which point the user is at in the game. I worked with many other classes that my fellow teammates wrote such as StockCards.java, TrainGame.java, etc
 * 
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JApplet;
import java.io.File;
import javax.imageio.*;
import java.io.IOException;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.Border;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;

/**
 * Class TicketToRide - this class is used as the starting point to call the rest of the program
 * 
 * @author Rachael Mahar
 */
public class TicketToRide extends JApplet implements MouseListener
{
    /** Board image */
    private BufferedImage board;

    /** Number of players*/
    private int NUM_PLAYERS;

    /** If you click to bring stock cards up */
    private boolean stockScreen;

    /** If you click on to view your destinations cards */
    private boolean myDestinationsScreen;

    /** If you click to place trains */
    private boolean placingTrains;

    /** If you click to intruction button */
    private boolean intructionDisplay;

    /** first click */
    private boolean firstClickHappened;

    /** status bar text */
    private String status = "";

    /** where was first click? */
    private int click1CityX, click1CityY;

    /** main train game */
    private TrainGame game;

    /** the surrounding area of around the city that you can click on */
    private final int AREA_AROUND_CITY = 13;

    // players turn
    private Player playerGoing;

    // images of trains assigned to each player's color
    private BufferedImage blackTrain;
    private BufferedImage blueTrain;
    private BufferedImage greenTrain;
    private BufferedImage redTrain;
    private BufferedImage yellowTrain;

    // Map of Pennm
    private PennMap map = new PennMap();

    // array of TrainCards selected to claim a route
    private ArrayList<TrainCard> ticketsUsedToClaimRoute = new ArrayList<TrainCard>();

    // if you are picking trains
    private boolean pickingTrainCards;

    // create start and dest city to create route
    private City endCity;
    private City startCity;
    // the route the player wants to capture
    private Route r;

    // when you finish picking cards used to put down
    private boolean donePickingTrains;

    // placing trains?
    private boolean trainsPlaced;

    // image to view stocks
    private BufferedImage stocks;

    // image to view destination cards
    private BufferedImage destImages;

    // if next button is clicked
    boolean nextButton;

    // if back button is clicked
    boolean backButton;

    // image of next button
    private BufferedImage nextButtonImage;

    // image of back button
    private BufferedImage backButtonImage;

    private int currentCard = 0;

    /**
     * Called by the browser or applet viewer to inform this JApplet that it
     * has been loaded into the system. It is always called before the first 
     * time that the start method is called.
     */
    public void init()
    {
        addMouseListener(this);

        // read board and train color pieces in
        try{
            board = ImageIO.read(new File("TicketToRideGraphics/board.jpg"));
            blackTrain = ImageIO.read(new File("TicketToRideGraphics/TrainPieces/BLACKTRAIN.jpg"));
            blueTrain = ImageIO.read(new File("TicketToRideGraphics/TrainPieces/BLUETRAIN.jpg"));
            greenTrain = ImageIO.read(new File("TicketToRideGraphics/TrainPieces/GREENTRAIN.jpg"));
            redTrain = ImageIO.read(new File("TicketToRideGraphics/TrainPieces/REDTRAIN.jpg"));
            yellowTrain = ImageIO.read(new File("TicketToRideGraphics/TrainPieces/YELLOWTRAIN.jpg"));
            stocks = ImageIO.read(new File("TicketToRideGraphics/VIEWSTOCKS.png"));
            destImages = ImageIO.read(new File("TicketToRideGraphics/stockImage.jpg"));
            nextButtonImage = ImageIO.read(new File("TicketToRideGraphics/next.png"));
            backButtonImage = ImageIO.read(new File("TicketToRideGraphics/back.png"));
        }
        catch (IOException e){
            System.err.println("Image file for board missing");
        }

        // pick how many players are playing
        Object[] possiblePlayers = { "2", "3", "4", "5",};
        Object selectedValue = JOptionPane.showInputDialog(null, "Choose how many players", "Number of Players", JOptionPane.INFORMATION_MESSAGE, null, possiblePlayers, possiblePlayers[0]);
        NUM_PLAYERS = Integer.valueOf((String) selectedValue);

        String[] playerNames = new String[NUM_PLAYERS];
        TrainColor[] playerColors = new TrainColor[NUM_PLAYERS];

        // possible colors for players to choose
        List<Object> possibleColors = new ArrayList<>();
        possibleColors.add("Blue");
        possibleColors.add("Green");
        possibleColors.add("Black");
        possibleColors.add("Red");
        possibleColors.add("Yellow");

        // assign names to players
        for (int i = 0; i < NUM_PLAYERS; i++) {
            // ask user for players name
            String player = JOptionPane.showInputDialog("Please enter player " + (i+1) + "'s name.");
            // ask for color for user
            String selectedColor = (String)JOptionPane.showInputDialog(null, "Choose a color for player " + player + "", "Color", JOptionPane.INFORMATION_MESSAGE, null, possibleColors.toArray(), possibleColors.get(0));
            for (int j = 0; j < possibleColors.size(); j++){
                if(selectedColor.equals((String)possibleColors.get(j))){
                    // once you pick a color, it becomes null in the list
                    possibleColors.remove(j);
                }
            }

            // creates a new player and adds it to list of players
            TrainColor c = null;

            switch(selectedColor){
                case "Blue": c = TrainColor.BLUE; break;
                case "Green": c = TrainColor.GREEN; break;
                case "Black": c = TrainColor.BLACK; break;
                case "Red": c = TrainColor.RED; break;
                case "Yellow": c = TrainColor.YELLOW; break;
            }

            playerNames[i] = player;
            playerColors[i] = c;
        }

        game = new TrainGame(playerNames, playerColors, map);

        for(Player p : game.players){
            // list of player's dest cards
            List<DestCard> cardList = p.destCards;
            // list of names of all dest cards
            List<String> namesOfDestCards = new ArrayList<>();
            // list of DestCards to pass to method
            List<DestCard> destCards = new ArrayList<>();

            for (int i = 0; i < cardList.size(); i++){
                namesOfDestCards.add(cardList.get(i).source.name + " to " + cardList.get(i).dest.name);
            }

            // if you are done
            namesOfDestCards.add("DONE");
            // loops until you click done
            boolean done = false;
            // number of cards
            int numCards = 0;
            while (!done){
                String destCard = (String)JOptionPane.showInputDialog(null, "Please pick at least 3 destination cards for " + p.name, "Destination Cards", JOptionPane.INFORMATION_MESSAGE, null, namesOfDestCards.toArray(), null);

                // if you click done, loop ends
                if(destCard.equals("DONE")){
                    if (numCards >= 3){
                        done = true;
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "You have not picked three cards yet", "You have not picked three cards yet", JOptionPane.ERROR_MESSAGE);
                    }
                }else{

                    //convert the String to a DestCard:
                    String firstCity = destCard.substring(0, destCard.indexOf(" "));
                    String secondCity = destCard.substring(destCard.indexOf(" ")+4);

                    DestCard d = new DestCard(new City(firstCity), new City(secondCity), 0, "routes2.jpg");
                    DestCard d2 = null;
                    //find the matching DestCard in the cardList
                    for (int i = 0; i < cardList.size(); i++){
                        if(cardList.get(i).equals(d)){
                            d2 = cardList.get(i);
                            break;
                        }
                    }
                    //add it to the destCard arrayList
                    destCards.add(d2);
                    if(d2 == null){//debugging
                        game.players[0].name = firstCity;
                        game.players[1].name = secondCity;
                    }

                    for (int j = 0; j < namesOfDestCards.size(); j++){
                        if(destCard.equals((String)namesOfDestCards.get(j))){
                            // once you pick a color, it becomes null in the list
                            namesOfDestCards.remove(j);
                            numCards++;
                        }
                    }
                }

            }
            game.dcardsSetup(p, destCards.toArray(new DestCard[] {} ));

        }
    }

    /**
     * Paint method for applet.
     * 
     * @param  g   the Graphics object for this applet
     */
    public void paint(Graphics g)
    {
        // displays board
        g.drawImage(board, 0, 0, null);

        // draw images of card on board
        g.drawImage(game.trainPile.get(0).image, 150, 797, null);
        g.drawImage(game.trainPile.get(1).image, 352, 797, null);
        g.drawImage(game.trainPile.get(2).image, 552, 797, null);
        g.drawImage(game.trainPile.get(3).image, 752, 797, null);
        g.drawImage(game.trainPile.get(4).image, 954, 797, null);

        // number of cards the player has
        int numWild = 0;
        int numBlue = 0;
        int numWhite = 0;
        int numYellow = 0;
        int numPink = 0;
        int numOrange = 0;
        int numBlack = 0;
        int numGreen = 0;
        int numRed = 0;

        // loops through current players hand to count how many cards of each type
        for (int i = 0; i < game.players[game.activePlayer].hand.size(); i++){
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.WILD)) {
                numWild++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.BLUE)) {
                numBlue++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.WHITE)) {
                numWhite++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.YELLOW)) {
                numYellow++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.PINK)) {
                numPink++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.ORANGE)) {
                numOrange++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.BLACK)) {
                numBlack++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.GREEN)) {
                numGreen++;
            }
            if (game.players[game.activePlayer].hand.get(i).color.equals(TrainColor.RED)) {
                numRed++;
            }
        }

        // change font of numbers
        Font myFont = new Font("Papyrus", Font.BOLD, 100);
        g.setFont(myFont);

        g.setColor(Color.white);
        // display number of cards player has
        g.drawString(numWild + "", 35, 62);
        g.drawString(numBlue + "", 35, 148);
        g.drawString(numWhite + "", 35, 233);
        g.drawString(numYellow + "", 35, 318);
        g.drawString(numPink + "", 35, 403);
        g.drawString(numOrange + "", 35, 488);
        g.drawString(numBlack + "", 35, 574);
        g.drawString(numGreen + "", 35, 658);
        g.drawString(numRed + "", 35, 744);

        // if stock screen is up
        myFont = new Font("Papyrus", Font.BOLD, 200);
        g.setFont(myFont);
        if (stockScreen){
            g.drawImage(stocks, 135, 0, null);
            g.drawString("" + (game.players[game.activePlayer].pennsylvania.size()), 211, 133);
            g.drawString("" + (game.players[game.activePlayer].readingRailRoad.size()), 580, 133);
            g.drawString("" + (game.players[game.activePlayer].westernMaryland.size()), 940, 133);
            g.drawString("" + (game.players[game.activePlayer].baltimore.size()), 211, 307);
            g.drawString("" + (game.players[game.activePlayer].lehighValley.size()),580, 307);
            g.drawString("" + (game.players[game.activePlayer].jerseyCentralLine.size()), 940, 307);
            g.drawString("" + (game.players[game.activePlayer].erieLackawanna.size()), 211, 490);
            g.drawString("" + (game.players[game.activePlayer].newyorkCentralSystem.size()), 580, 490);
            g.drawString("" + (game.players[game.activePlayer].brpRailWay.size()), 940, 490);
        }

        // if my destination screen is up
        if (myDestinationsScreen){

            if (nextButton){
                currentCard++;
                if(currentCard > game.players[game.activePlayer].destCards.size()-1){
                    currentCard = 0;
                }
                nextButton = false;
                backButton = false;
                g.drawImage(game.players[game.activePlayer].destCards.get(currentCard).cardImage, 135, 0, null);
                g.drawImage(nextButtonImage, 600, 550, null);
                g.drawImage(backButtonImage, 500, 550, null);
            }

            else if(backButton){
                currentCard--;
                if(currentCard < 0){
                    currentCard = game.players[game.activePlayer].destCards.size()-1;
                }
                backButton = false;
                nextButton = false;
                g.drawImage(game.players[game.activePlayer].destCards.get(currentCard).cardImage, 135, 0, null);
                g.drawImage(nextButtonImage, 600, 550, null);
                g.drawImage(backButtonImage, 500, 550, null);
            }else{
                g.drawImage(game.players[game.activePlayer].destCards.get(currentCard).cardImage, 135, 0, null);
                g.drawImage(nextButtonImage, 600, 550, null);
                g.drawImage(backButtonImage, 500, 550, null);
            }
        }

        myFont = new Font("Papyrus", Font.BOLD, 20);
        g.setFont(myFont);
        // display who's turn it is number and name
        g.drawString("PLAYER:", 1140, 60);
        g.drawString(game.players[game.activePlayer].name, 1145, 130);
        myFont = new Font("Papyrus", Font.BOLD, 60);
        g.setFont(myFont);
        g.drawString("" + (game.activePlayer + 1), 1170, 100);

        // picture of players color train according to whos turn it is
        if (game.players[game.activePlayer].color.equals(TrainColor.BLACK)) {
            g.drawImage(blackTrain, 1138, 461, null);
        }
        else if (game.players[game.activePlayer].color.equals(TrainColor.BLUE)) {
            g.drawImage(blueTrain, 1138, 461, null);
        }
        else if (game.players[game.activePlayer].color.equals(TrainColor.GREEN)) {
            g.drawImage(greenTrain, 1138, 461, null);
        }
        else if (game.players[game.activePlayer].color.equals(TrainColor.RED)) {
            g.drawImage(redTrain, 1138, 461, null);
        }
        else if (game.players[game.activePlayer].color.equals(TrainColor.YELLOW)) {
            g.drawImage(yellowTrain, 1138, 461, null);
        }

        myFont = new Font("Papyrus", Font.BOLD, 50);
        g.setFont(myFont);
        g.drawString("x" + game.players[game.activePlayer].numTrains, 1150, 575);

        // string of points depending on person playing
        g.drawString("" + (game.players[game.activePlayer].numPoints), 1170, 200);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // if you click on top of color deck to take, adds to your stack
        if (x > 8 && x < 125 && y > 766 && y < 942 && !myDestinationsScreen && !stockScreen){ 
            status = "You just picked a new card from the pile!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard();
            repaint();
            return;
        }

        // if you pick a color card to take from slot 1, will immediately draw from top and fill in spot
        else if (x > 150 && x < 325 && y > 797 && y < 906 && !myDestinationsScreen && !stockScreen){
            status = "You just picked a new card from slot one!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard( 0);
            repaint();
            return;
        }

        // if you pick a color card to take from slot 2, will immediately draw from top and fill in spot
        else if (x > 352 && x < 527 && y > 797 && y < 906 && !myDestinationsScreen && !stockScreen){ 
            status = "You just picked a new card from slot two!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard(1);
            repaint();
            return;
        }

        // if you pick a color card to take from slot 3, will immediately draw from top and fill in spot
        else if (x > 553 && x < 728 && y > 797 && y < 906 && !myDestinationsScreen && !stockScreen){ 
            status = "You just picked a new card from slot three!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard(2);
            repaint();
            return;
        }

        // if you pick a color card to take from slot 4, will immediately draw from top and fill in spot
        else if (x > 752 && x < 927 && y > 797 && y < 906 && !myDestinationsScreen && !stockScreen){ 
            status = "You just picked a new card from slot four!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard(3);
            repaint();
            return;
        }

        // if you pick a color card to take from slot 5, will immediately draw from top and fill in spot
        else if (x > 955 && x < 1130 && y > 797 && y < 906 && !myDestinationsScreen && !stockScreen){ 
            status = "You just picked a new card from slot five!";
            showStatus(status);
            // players draws a card from deck
            game.drawCard(4);
            repaint();
            return;
        }

        // if you end your turn
        else if (x > 1108 && x < 1217 && y > 640 && y < 780  && !myDestinationsScreen && !stockScreen){ 
            status = "You just ended your turn!";
            showStatus(status);
            // if it is last player, start from first player again

            //reset all placing train booleans/variables
            firstClickHappened = false;
            pickingTrainCards = false;
            donePickingTrains = false;
            trainsPlaced = false;
            startCity = null;
            endCity = null;
            r = null;
            ticketsUsedToClaimRoute.clear();
            placingTrains = false;
            // changed turn
            game.changeTurn();
            repaint();
            return;
        }

        // if you click to view your stocks
        else if (x > 560 && x < 717 && y > 681 && y < 780 && !myDestinationsScreen){ 
            if (!stockScreen){
                status = "You are viewing your stocks!";
                showStatus(status);
                stockScreen = true;
                repaint();
                return;
            }
            else {
                stockScreen = false;
            }
        }

        // if you click to view your routes 
        else if (x > 260 && x < 418 && y > 681 && y < 740 && !stockScreen){
            if (!myDestinationsScreen){
                status = "You are viewing your destination cards!";
                showStatus(status);
                myDestinationsScreen = true;
                repaint();
                return;
            }
            else {
                myDestinationsScreen = false;
                currentCard = 0;
                nextButton = false;
                backButton = false;
            }

        }
        // if destination screen is up, and you click next button to view next image
        else if(myDestinationsScreen && x > 604 && x < 685 && y > 551 && y < 597){ // if next button button is clicked
            nextButton = true;
            repaint();
            return;
        }
        // if destination screen is up, and you click back button to view previous image
        else if(myDestinationsScreen && x > 502 && x < 582 && y > 551 && y < 597){ // if back button button is clicked
            backButton = true;
            repaint();
            return;
        }
        // if you click to get more destination cards
        else if (x > 891 && x < 990 && y > 682 && y < 781 && !myDestinationsScreen && !stockScreen){ 
            // four cards are selected for a player to choose
            game.showDest(4);
            // empty list of cards delt
            List<Object> destCardsDealt = new ArrayList<>();
            // fills list with destination cards
            for(DestCard d: game.shownDestCards){
                destCardsDealt.add(d);
            }
            // changes destination cards to string form
            for(int i = 0; i < destCardsDealt.size(); i++){
                destCardsDealt.set(i, destCardsDealt.get(i).toString());
            }
            // empty list of DestCards to pass to method
            List<DestCard> destCards = new ArrayList<>();

            // if you are done
            destCardsDealt.add("DONE");
            // loops until you click done
            boolean done = false;
            // number of cards
            int numCards = 0;
            while (!done){
                String destCard = (String)JOptionPane.showInputDialog(null,  game.players[game.activePlayer].name + ", please pick at least one destination card", "Destination Cards", JOptionPane.INFORMATION_MESSAGE, null, destCardsDealt.toArray(new DestCard[]{}), null);

                // if you click done, loop ends
                if(destCard.equals("DONE")){
                    if (numCards >= 1){
                        done = true;
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "You have to pick at least one!", "You have to pick at least one!", JOptionPane.ERROR_MESSAGE);
                    }
                }else{

                    //convert the String to a DestCard:
                    String firstCity = destCard.substring(0, destCard.indexOf(" "));
                    String secondCity = destCard.substring(destCard.indexOf(" ")+4);

                    DestCard d = new DestCard(new City(firstCity), new City(secondCity), 0, "routes2.jpg");
                    //find the matching DestCard in the cardList
                    for (int i = 0; i < map.dest.size(); i++){
                        if( map.dest.get(i).equals(d)){
                            destCards.add(d);
                            break;
                        }
                    }

                    for (int j = 0; j < destCardsDealt.size(); j++){
                        if(destCard.equals((String)destCardsDealt.get(j))){
                            // once you pick a dest card, it removes from the list
                            destCardsDealt.remove(j);
                            numCards++;
                        }
                    }
                }

            }

        }

        // if you click to place trains
        else if (x > 1134 && x < 1263 && y > 369 && y < 426 && (!placingTrains)){ 
            status = "You are placing trains! Pick a city to start at!";
            showStatus(status);
            placingTrains = true;
            return;
        }

        // you are placing trains
        else if (placingTrains && !myDestinationsScreen && !stockScreen) {

            // if it is second click
            if (pickingTrainCards){
                // if click wild
                if (x > 0 && x < 136 && y > 0 && y < 85){
                    TrainCard w = new TrainCard(TrainColor.WILD, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a wild card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any wild cards to use");
                    }
                }
                // if click blue
                if (x > 0 && x < 136 && y > 85 && y < 170){ 
                    TrainCard w = new TrainCard(TrainColor.BLUE, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a blue card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any blue cards to use");
                    }
                }
                // if click white
                if (x > 0 && x < 136 && y > 170 && y < 225){ 
                    TrainCard w = new TrainCard(TrainColor.WHITE, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a white card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any white cards to use");
                    }
                }
                // if click yellow
                if (x > 0 && x < 136 && y > 255 && y < 340){ 
                    TrainCard w = new TrainCard(TrainColor.YELLOW, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a yellow card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any yellow cards to use");
                    }
                }
                // if click pink
                if (x > 0 && x < 136 && y > 340 && y < 425){ 
                    TrainCard w = new TrainCard(TrainColor.PINK, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a pink card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any pink card to use");
                    }
                }
                // if click orange
                if (x > 0 && x < 136 && y > 425 && y < 510){
                    TrainCard w = new TrainCard(TrainColor.ORANGE, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a orange card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any orange cards to use");
                    }
                }
                // if click black
                if (x > 0 && x < 136 && y > 510 && y < 595){
                    TrainCard w = new TrainCard(TrainColor.BLACK, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a black card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any black cards to use");
                    }
                }
                // if click green
                if (x > 0 && x < 136 && y > 595 && y < 680){ 
                    TrainCard w = new TrainCard(TrainColor.GREEN, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a green card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any green cards to use");
                    }
                }
                // if click red
                if (x > 0 && x < 136 && y > 680 && y < 765){
                    TrainCard w = new TrainCard(TrainColor.RED, null);
                    if (game.players[game.activePlayer].hand.contains(w)){
                        ticketsUsedToClaimRoute.add(w);
                        status = "You just picked a red card to hand in";
                        showStatus(status);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have any red cards to use");
                    }
                }
                // if you click on the hand button, to hand in cards
                if (x > 1134 && x < 1263 && y > 369 && y < 426){
                    // checks to see if array of cards is accepting
                    try{
                        trainsPlaced = game.claimRoute(r, ticketsUsedToClaimRoute.toArray(new TrainCard[]{}));
                    }catch(IllegalArgumentException iae){
                        boolean messageGiven = false;
                        boolean doubleTried = false;
                        for(Route r2: map.routes){
                            if(r2.equals(r) && r2.color != r.color){
                                doubleTried = true;
                                try{
                                    trainsPlaced = game.claimRoute(r2, ticketsUsedToClaimRoute.toArray(new TrainCard[]{}));
                                    if (trainsPlaced){
                                        status = "Your hand was accepted";
                                        showStatus(status);
                                        // draw polygons
                                        repaint();
                                    }else{
                                        status = "Your hand was rejected";
                                        showStatus(status);
                                    }
                                }catch(IllegalArgumentException ie){
                                    JOptionPane.showMessageDialog(null, ie.getMessage());
                                    messageGiven = true;
                                    break;
                                }
                            }
                        }
                        if(!messageGiven && !doubleTried) JOptionPane.showMessageDialog(null, iae.getMessage());
                        status = "Your hand was rejected";
                        showStatus(status);
                    }
                    repaint();
                    
                    // reset everything
                    startCity = null;
                    endCity = null;
                    r = null;
                    ticketsUsedToClaimRoute.clear();
                    pickingTrainCards = false;
                    trainsPlaced = false;
                    placingTrains = false;
                    firstClickHappened = false;
                    donePickingTrains = false;
                }
                return;

            }
            else if (firstClickHappened) { // if it is second click
                // reset for next click
                firstClickHappened = false;

                // search for end city in list of cities
                for (int i = 0; i < map.cities.length; i++){
                    if (x > map.cities[i].xStart && x < map.cities[i].xEnd && y > map.cities[i].yStart && y < map.cities[i].yEnd){
                        endCity = map.cities[i];
                    }
                }

                // loop through routes to find this specific route from start city to end city
                for (int i = 0; i < map.routes.length; i++){
                    if ((startCity.equals(map.routes[i].source) && endCity.equals(map.routes[i].dest)) || (startCity.equals(map.routes[i].dest) && endCity.equals(map.routes[i].source))){
                        r = map.routes[i];
                        // if route was already taken

                    }
                }        

                // if a route was not found
                if (r == null){
                    JOptionPane.showMessageDialog(null, "Not a valid route", "Not a valid route", JOptionPane.ERROR_MESSAGE);
                    // reset everything
                    startCity = null;
                    endCity = null;
                    r = null;
                    ticketsUsedToClaimRoute.clear();
                    pickingTrainCards = false;
                    trainsPlaced = false;
                    placingTrains = false;
                    firstClickHappened = false;
                    donePickingTrains = false;
                    return;
                }
                // a valid route was found
                JOptionPane.showMessageDialog(null, "You just selected " + startCity.name + " to " + endCity.name + ". Click which cards you want to use to claim route then click hand button");
                pickingTrainCards = true;

                return;
            }
            else { // if it is the first click

                // search for start city in list of cities
                for (int i = 0; i < map.cities.length; i++){
                    if (x > map.cities[i].xStart && x < map.cities[i].xEnd && y > map.cities[i].yStart && y < map.cities[i].yEnd){
                        startCity = map.cities[i];
                        status = "You are trying to select a city to start at.";
                        showStatus(status);
                    }
                }

                status = "You just selected " + startCity.name + " to start at.";
                showStatus(status);

                firstClickHappened = true;
                return;

            }

        }
        else {
            status = "Pick a valid button.";
            showStatus(status);
        }
        repaint();

    }

    @Override
    public void mousePressed(MouseEvent e) {    }

    @Override
    public void mouseReleased(MouseEvent e) {    }

    @Override
    public void mouseEntered(MouseEvent e) {    }

    @Override
    public void mouseExited(MouseEvent e) {    }

}