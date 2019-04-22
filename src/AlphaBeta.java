import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Point {

    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}

class PointsAndScores {

    int score;
    Point point;

    PointsAndScores(int score, Point point) {
        this.score = score;
        this.point = point;
    }
}

class Board {
    List<Point> availablePoints;
    Scanner scan = new Scanner(System.in);
    int[][] board = new int[3][3]; 
    List<PointsAndScores> rootsChildrenScore = new ArrayList<>();

    public int evaluateBoard() {
        int score = 0;
        
        for (int i = 0; i < 3; ++i){   //Check all rows
            int blank = 0;
            int X = 0;
            int O = 0;
            for (int j = 0; j < 3; ++j) {
                switch (board[i][j]) {
                    case 0:
                        blank++;
                        break;
                    case 1:
                        X++;
                        break;
                    default:
                        O++;
                        break;
                }

            } 
            score+=changeInScore(X, O); 
        }

        for (int j = 0; j < 3; ++j){   //Check all columns
            int blank = 0;
            int X = 0;
            int O = 0;
            for (int i = 0; i < 3; ++i){
                switch (board[i][j]){
                    case 0:
                        blank++;
                        break;
                    case 1:
                        X++;
                        break; 
                    default:
                        O++;
                        break;
                }
            }
            score+=changeInScore(X, O);
        }
        int blank = 0;
        int X = 0;
        int O = 0;

        
        for (int i = 0, j = 0; i < 3; ++i, ++j){ //Check diagonal (1)
            switch (board[i][j]){
                case 1:
                    X++;
                    break;
                case 2:
                    O++;
                    break;
                default:
                    blank++;
                    break;
            }
        }
        score+=changeInScore(X, O);
        blank = 0;
        X = 0;
        O = 0;

        for (int i = 2, j = 0; i > -1; --i, ++j) {  //Check Diagonal (2)
            switch (board[i][j]){
                case 1:
                    X++;
                    break;
                case 2:
                    O++;
                    break;
                default:
                    blank++;
                    break;
            }
        }
        score+=changeInScore(X, O);
        return score;
    }
    
    private int changeInScore(int X, int O){
        int change;
        if (X == 3) {
            change = 100;
        } else if (X == 2 && O == 0) {
            change = 10;
        } else if (X == 1 && O == 0) {
            change = 1;
        } else if (O == 3) {
            change = -100;
        } else if (O == 2 && X == 0) {
            change = -10;
        } else if (O == 1 && X == 0) {
            change = -1;
        } else {
            change = 0;
        } 
        return change;
    }
    
    int uptoDepth = -1;  //specified depth limits for search variable
    
    public int alphaBetaMinimax(int alpha, int beta, int depth, int turn){
        
        if(beta<=alpha){ System.out.println("Pruning at depth = "+depth);if(turn == 1) return Integer.MAX_VALUE; else return Integer.MIN_VALUE; }
        if(depth == uptoDepth || isGameOver()) return evaluateBoard();
        List<Point> pointsAvailable = getAvailableStates();
        if(pointsAvailable.isEmpty()) return 0;
        if(depth==0) rootsChildrenScore.clear(); 
        int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
        for(int i=0;i<pointsAvailable.size(); ++i){
            Point point = pointsAvailable.get(i);
            
            int currentScore = 0;
            
            if(turn == 1){
                placeAMove(point, 1); 
                currentScore = alphaBetaMinimax(alpha, beta, depth+1, 2);
                maxValue = Math.max(maxValue, currentScore); 
                alpha = Math.max(currentScore, alpha);  //Set alpha
                
                if(depth == 0)
                    rootsChildrenScore.add(new PointsAndScores(currentScore, point));
                }
            else if(turn == 2){
                placeAMove(point, 2);
                currentScore = alphaBetaMinimax(alpha, beta, depth+1, 1); 
                minValue = Math.min(minValue, currentScore);
                beta = Math.min(currentScore, beta);  //Set beta
            }
            board[point.x][point.y] = 0; //reset board
            
            //If a pruning has been done, don't evaluate the rest of the sibling states
            if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break;
        }
        return turn == 1 ? maxValue : minValue;
    }  

    public boolean isGameOver(){
        //Game is over is someone has won, or board is full (draw)
        return (hasXWon() || hasOWon() || getAvailableStates().isEmpty());
    }

    public boolean hasXWon(){
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
            //System.out.println("X Diagonal Win");
            return true;
        }
        for (int i = 0; i < 3; ++i){
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
                // System.out.println("X Row or Column win");
                return true;
            }
        }
        return false;
    }

    public boolean hasOWon(){
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
            return true;        //System.out.println("O Diagonal Win");
        }
        for (int i = 0; i < 3; ++i) {
            if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
                return true;    //System.out.println("O Row or Column win");
            }
        }
        return false;
    }

    public List<Point> getAvailableStates() {
        availablePoints = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 0) {
                    availablePoints.add(new Point(i, j));
                }
            }
        }
        return availablePoints;
    }

    public void placeAMove(Point point, int player){
        board[point.x][point.y] = player;   //player = 1 for X, 2 for O
    }

    public Point returnBestMove(){
        int MAX = -100000;
        int best = -1;

        for (int i = 0; i < rootsChildrenScore.size(); ++i){
            if (MAX < rootsChildrenScore.get(i).score){
                MAX = rootsChildrenScore.get(i).score;
                best = i;
            }
        }
        return rootsChildrenScore.get(best).point;
    }
    int getRow(){
        System.out.println("Choose Row: (1)TOP (2)MIDDLE (3)BOTTOM \n");
        Scanner scan1 = new Scanner(System.in);
        
            int y = scan1.nextInt();
		while(y < 1 || y > 3 ){
                    try{
                        System.out.println("ERROR: Enter Valid Row \n");
                        System.out.println("\nChoose Row: (1)TOP (2)MIDDLE (3)BOTTOM \n");    
                            y = scan1.nextInt();
                    } catch(InputMismatchException ex) {
                        scan1.next();
                    }
		}	
        if (y == 1){System.out.println("TOP ROW CHOSEN\n");}        
        if (y == 2){System.out.println("MIDDLE ROW CHOSEN\n");}
	if (y == 3){System.out.println("BOTTOM ROW CHOSEN\n");}
        return (y-1);  //1 2 3 nicer inputs than 0 1 2
    }
    
    int getCol(){
       System.out.println("Choose Column: (1)LEFT (2)MIDDLE (3)RIGHT \n");    
       Scanner scan1 = new Scanner(System.in);
        int x = scan1.nextInt();
            while(x < 1 || x > 3 ){
                try {
                    System.out.println("ERROR: Enter Valid Column \n");
                    System.out.println("\nChoose Column: (1)LEFT (2)MIDDLE (3)RIGHT \n");    
                        x = scan1.nextInt();
			} catch (InputMismatchException ex) {
                            scan1.next();
			}
		}	
        if (x == 1){System.out.println("LEFT COLUMN CHOSEN\n");}        
        if (x == 2){System.out.println("MIDDLE COLUMN CHOSEN\n");}
	if (x == 3){System.out.println("RIGHT COLUMN CHOSEN\n");}
        return (x-1);  //1 2 3 nicer inputs than 0 1 2
    }
    
    void takeHumanInput() {
        int b = getCol();
        int a = getRow(); 
        while(board[a][b]==1 || board[a][b]==2){
            System.out.println("Error: Spot Taken, Choose Another! \n");
            displayBoard();
            b = getCol();
            a = getRow();
        }    
        Point userMove = new Point(a,b);
        placeAMove(userMove, 2); //2 for O and O is the user
    }
    
    public void displayBoard() {
        System.out.println();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j]==0){
                    System.out.print("|  ");
                }
                if(board[i][j]==1){
                    System.out.print("X  ");
                }
                if (board[i][j]==2){
                    System.out.print("O  ");
                }
            }System.out.println();
        }System.out.println("\n");
    }
    
    public void resetBoard(){       // resets board state to show default "|" characters
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                board[i][j] = 0;
            }
        }
    }
    
    public int endMessage(){
        System.out.println("(1)Play Again? (2)Quit \n");  
        Scanner scan1 = new Scanner(System.in);
        int z = scan1.nextInt();
            while(z < 1 || z > 2){
                try{
                    System.out.println("ERROR: Enter Valid Response: (1) or (2) \n");
                    System.out.println("\n(1)Play Again? (2)Quit \n");  
                    z = scan1.nextInt();
                }catch(InputMismatchException ex) {
                    can1.next();
                }
            }
        return z;
    }

    
    public int getChoice(){
        System.out.println("Who moves first? (1)Computer (2)User: \n");  
        Scanner scan = new Scanner(System.in);
            int choice = scan.nextInt();
		while(choice < 1 || choice > 2 ){
                    try {
                        System.out.println("\nERROR: Enter valid input (1) or (2) \n");
                        System.out.println("Who's gonna move first? (1)Computer (2)User: \n");    
                            choice = scan.nextInt();
			} catch (InputMismatchException ex) {
                            scan.next();
			}
		}
            displayBoard();
            if (choice == 1){System.out.println("COMPUTER GOES FIRST:\n");}
            if (choice == 2){System.out.println("USER GOES FIRST\n");}
        return choice;
    }
}

public class AlphaBeta {
    public static void main(String[] args) { 
        while(true){
            Board b = new Board();
            Random rand = new Random();
            b.displayBoard();
            int choice = b.getChoice();
            {System.out.println("You are O \n");}

            if (choice == 1) {
                Point p = new Point(rand.nextInt(3), rand.nextInt(3));
                b.placeAMove(p, 1);
                b.displayBoard();
            }
            
            while (!b.isGameOver()) {   
                b.takeHumanInput();
                b.displayBoard();
                if (b.isGameOver()) break;

                b.alphaBetaMinimax(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1);
                for (PointsAndScores pas : b.rootsChildrenScore) 
                    System.out.println("Point: " + pas.point + " Score: " + pas.score);

                b.placeAMove(b.returnBestMove(), 1);
                b.displayBoard();
            }
            
            if (b.hasXWon()) {
                System.out.println("Defeated...\n");
            } else if (b.hasOWon()) {
                System.out.println("Victory!\n");
            } else {
                System.out.println("Draw\n");
            }
            
            int end = b.endMessage();
            if (end == 1){
                b.resetBoard();
            }
            if (end == 2){
                break;
            }
        }
    }
}
