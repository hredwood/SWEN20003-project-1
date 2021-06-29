import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;


/**
 * An example Bagel game.
 */
public class ShadowTreasure extends AbstractGame {

    // for rounding double number; use this to print the location of the player
    private static DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Define magic numbers: tick length, step size, meet distance qualifier,
     * energy boundary for if statement, energy label position, text colour,
     * and whether the sandwich is displayed.
     */
    private static final int TICK_LENGTH = 10;
    private static final int STEP_SIZE = 10;
    private static final int MEET_DISTANCE = 50;
    private static final int ENERGY_BOUNDARY = 3;
    private boolean SANDWICH_APPEARS = true;
    private static final Point ENERGY_LABEL = new Point(20,760);
    public static final Colour BLACK= new Colour(0.0,0.0,0.0);

    /**
     * Load background image (and co-ordinates) and objects for game
    */
    private Image background = new Image("res/images/background.png");
    private static final double  BACKGROUND_X = 0;
    private static final double BACKGROUND_Y = 0;
    private Zombie zombie;
    private Sandwich sandwich;
    private Player player;

    /**
     * Set count for frames
     */
    private int frameCount = 0;

    /**
     * Check whether player 'meets' object
     */
    public boolean meets(Point object1, Point object2){
        double distance = distance(object1, object2);
        if (distance < MEET_DISTANCE){
            return true;
        }
        return false;

    }

    /**
     * Return distance between two objects
     */
    public double distance(Point object1, Point object2){
        return Math.sqrt(Math.pow(object1.x-object2.x, 2) + Math.pow(object1.y-object2.y, 2));
    }

    public static void printInfo(double x, double y, int e) {
        System.out.println(df.format(x) + "," + df.format(y) + "," + e);
    }
    
    public ShadowTreasure() throws IOException {
        this.loadEnvironment("res/IO/environment.csv");
    }

    /**
     * Load from input file
     */
    private void loadEnvironment(String filename){
        /**
         * Read from the file adapted from
         * Input and Output Lecture Slides by Shanika Karunasekera
         */
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String text;
            /**
             * For each line in csv file initialise new object
             */
            while ((text = br.readLine()) != null){
                /**
                 * Erase text's special characters before
                 */
                text = text.replaceAll("[^a-zA-Z0-9,]", "");
                String info[]= text.split(",");
                /**
                 * Assign co-ordinates from csv file to sandwich with its corresponding image
                 */
                if (info[0].equals("Sandwich")){
                    Point coordinates = new Point(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
                    Image sandwich_image = new Image("res/images/sandwich.png");
                    sandwich = new Sandwich(coordinates, sandwich_image);
                /**
                * Assign co-ordinates from csv file to zombie with its corresponding image
                */
                } else if (info[0].equals("Zombie")){
                    Point coordinates = new Point(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
                    Image zombie_image = new Image("res/images/zombie.png");
                    zombie = new Zombie(coordinates, zombie_image);

                /**
                * Assign co-ordinates from csv file to player with its corresponding image
                */
                } else{
                    Point coordinates = new Point(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
                    Image player_image = new Image("res/images/player.png");
                    player = new Player(coordinates, player_image, Integer.parseInt(info[3]));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Performs a state update.
     */
    @Override
    public void update(Input input) {
        /**
         * Only update every 10 frames
         */
        if (frameCount%TICK_LENGTH==0){
            /**
             * Print information on Player's movement to stdout
             */
            printInfo(player.getCoordinates().x, player.getCoordinates().y, player.getEnergy());

            /**
             * When Player 'meets' a Zombie, reduce Player's energy level and end game
             */
            if (meets(player.getCoordinates(), zombie.getCoordinates())){
                player.setEnergy(player.getEnergy()-3);
                Window.close();
            /**
             *  When Player 'meets' a sandwich, increase Player's energy level
             *  and sandwich does not appear anymore
             */
            } else if(meets(player.getCoordinates(), sandwich.getCoordinates())){
                if (SANDWICH_APPEARS){
                    player.setEnergy(player.getEnergy()+5);
                    SANDWICH_APPEARS=false;
                }
            }
            /**
             * Implementation of Line 6 and 7 of Algorithm 1
             */
            if (player.getEnergy() >= ENERGY_BOUNDARY){
                /**
                 * Player takes one step towards zombie with a step size of 10
                 */
                double distance = distance(player.getCoordinates(), zombie.getCoordinates());
                double x_step = STEP_SIZE*(player.getCoordinates().x-zombie.getCoordinates().x)/distance;
                double y_step = STEP_SIZE*(player.getCoordinates().y-zombie.getCoordinates().y)/distance;
                double new_x = player.getCoordinates().x - x_step;
                double new_y = player.getCoordinates().y - y_step;
                player.setCoordinates(new Point(new_x,new_y));
            } else{
                /**
                 * Player takes one step towards sandwich with a step size of 10
                 */
                double distance = distance(player.getCoordinates(), sandwich.getCoordinates());
                double x_step = STEP_SIZE*(player.getCoordinates().x-sandwich.getCoordinates().x)/distance;
                double y_step = STEP_SIZE*(player.getCoordinates().y-sandwich.getCoordinates().y)/distance;
                double new_x = player.getCoordinates().x - x_step;
                double new_y = player.getCoordinates().y - y_step;
                player.setCoordinates(new Point(new_x,new_y));
            }

        }

        /** Display background, zombie, sandwich and player onto
         * game with set x, y coordinates for whole game.
         * Sandwich should only appear if it hasn't been eaten yet
         */
        background.drawFromTopLeft(BACKGROUND_X,BACKGROUND_Y);
        zombie.getImage().draw(zombie.getCoordinates().x,zombie.getCoordinates().y);
        player.getImage().draw(player.getCoordinates().x,player.getCoordinates().y);
        if (SANDWICH_APPEARS){
            sandwich.getImage().draw(sandwich.getCoordinates().x,sandwich.getCoordinates().y);
        }


        /**
         * Display energy level of Player in the window
         */
        Font font = new Font("res/font/DejaVuSans-Bold.ttf", 20);
        String energyLevel = "energy: " + player.getEnergy();
        DrawOptions colour = new DrawOptions().setBlendColour(BLACK);
        font.drawString(energyLevel, ENERGY_LABEL.x, ENERGY_LABEL.y, colour);
        /**
         * Only increment frame count at the end of implementation
         */
        frameCount++;
    }


    /**
     * The entry point for the program.
     */
    public static void main(String[] args) throws IOException {
        ShadowTreasure game = new ShadowTreasure();
        game.run();
    }
}
