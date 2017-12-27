import java.util.Scanner;
/**
 * 
 * @author Xiaoning Guo
 * CSC 242: Artificial Intelligence Project 1 Tic-Tac-Toe
 * 
 * This is the main class that drives the game
 * There are two methods, gameLoopBasic which is the basic 3x3 Tic Tac Toe game.
 * gameLoopAdvanced is the 9-board Tic Tac Toe game.
 * 
 */

public class Driver {
	Scanner sc = new Scanner(System.in);
	
	public static void main(String args[]){
		
		boolean summaryon = true;
		int gamemode = -1;
		boolean noprune = false;
		
		for(String s: args){
			if(s.contains("off")){ summaryon = true;}
			if(s.contains("0")){ gamemode = 0;}
			if(s.contains("1")){ gamemode = 1;}
			if(s.contains("noprune")){ noprune = true;}
		}
		
		if(gamemode==-1){
			System.out.println("Bad input. Will not exit.");
			System.exit(0);
		}
		
		Driver drive = new Driver();
		
		while(true){
			if(gamemode==0){
				drive.gameLoopBasic(summaryon, noprune);
			}else{
				drive.gameLoopAdvanced(summaryon, noprune);
			}
		}
		
	}
	
	//Basic Tic-Tac-Toe
	public void gameLoopBasic(boolean summaryon, boolean noprune){
		String ai, player; //Which is X or which is O.
		Board board = new Board(); board.initialState();
		int turn;
		
		System.err.println("You are now playing basic Tic-Tac-Toe.");
		//Determining which player is which piece and who moves first.
		while(true){ //While statement is there to allow user to try again after error.
			System.err.println("Do you want to play X or O? X goes first.");
			String turnChoice = sc.nextLine();
			if(turnChoice.equals("o") || turnChoice.equals("O")){
				turn = Board.AI_ID; 
				ai = "X"; player = "O";
			}else if(turnChoice.equals("x") || turnChoice.equals("X")){
				turn = Board.PLAYER_ID; 
				ai = "O"; player = "X";
			}else{
				System.err.println("Invalid input. Try again.");
				continue;
			}
			break;
		}
	
		
		//This is the main control loop for the game.
		while(board.isTerminalState()==Board.NOT_TERMINAL){
			//AI Moves.
			if(turn==Board.AI_ID){
				int decision = board.decision(noprune);
				
				if(summaryon){
					System.err.printf("Nodes generated: %d\n",board.nodeCount);
					System.err.printf("Leaf nodes generated: %d\n",board.leafCount);
					System.err.printf("Best utility: %d\n",board.bestUtil);
					System.err.printf("Search time (milliseconds): %d\n\n", board.timer);
				}
				
				board = board.makeMove(Board.AI_ID, decision);
				System.err.print("My turn. I make the following move: \n");
				System.out.print(decision + "\n\n");
				printBoard(board, ai, player);
				System.err.print("\n");
				turn = Board.PLAYER_ID;
			}
			//Player Moves.
			else{
				int position;
				System.err.println("Enter your position move: ");
				
				while(true){
					try{
						String s = sc.nextLine();
						position = Integer.parseInt(s);
						if(board.isLegalMove(position))break;
						System.err.println("Error. Enter a valid position move:");
						continue;
					}catch(Exception e){
						System.err.println("Critical Error. Enter a valid position move:");
						continue;
					}
				}
				
				board = board.makeMove(Board.PLAYER_ID, position);
				printBoard(board, ai, player);
				System.err.print("\n");
				turn = Board.AI_ID;
			}
		}
		
		//End game display
		if(board.isTerminalState() == Board.AI_ID){
			System.err.print("\nHahaha! I, the superior AI, win!\n");
		}else if(board.isTerminalState() == Board.PLAYER_ID){
			System.err.print("\nYou win!\n\n");
		}else{
			System.err.print("\nWe tied.\n\n");
		}
		
	}
	
	//9-Board-Tic-Tac-Toe
	public void gameLoopAdvanced(boolean summaryon, boolean noprune){
		String ai, player; //Which is X or which is O.
		Grid grid = new Grid(); grid.initialState();
		int turn;
		
		System.err.println("You are now playing 9-Board-Tic-Tac-Toe.");
		//Determining which player is which piece and who moves first.
		while(true){ //While statement is there to allow user to try again after error.
			System.err.println("Do you want to play X or O? X goes first.");
			String turnChoice = sc.nextLine();
			if(turnChoice.equals("o") || turnChoice.equals("O")){
				turn = Board.AI_ID; 
				ai = "X"; player = "O";
			}else if(turnChoice.equals("x") || turnChoice.equals("X")){
				turn = Board.PLAYER_ID; 
				ai = "O"; player = "X";
			}else{
				System.err.println("Invalid input. Try again.");
				continue;
			}
			break;
		}
	
		//This is the main control loop for the game.
		while(grid.gamestate==Board.NOT_TERMINAL){
			
			//AI Moves.
			if(turn==Board.AI_ID){
				Integer[] decision = grid.decision(noprune);
				
				if(summaryon){
					System.err.printf("Maximum depth of search: %d\n",grid.depth);
					System.err.printf("Nodes generated: %d\n",grid.nodeCount);
					System.err.printf("Goal-state nodes found: %d\n",grid.leafCount);
					System.err.printf("Heuristic nodes generated: %d\n",grid.heurCount);
					System.err.printf("Best utility or heuristic: %d\n",grid.bestUtil);
					System.err.printf("Search time(milliseconds): %d\n\n", grid.timer);
				}
				
				System.err.print("My turn. I make the following move: \n");
				System.out.print(decision[0] + " " + decision[1] + "\n\n");
				grid = grid.makeMove(Board.AI_ID, decision[0], decision[1]);
				printGrid(grid, ai, player);
				turn = Board.PLAYER_ID;
			}
			//Human Moves.
			else{
				int gpos, bpos;
				System.err.println("Enter your position move: ");
				
				while(true){
					try{
						String s = sc.nextLine();
						String[] s2 = s.split(" ");
						gpos = Integer.parseInt(s2[0]);
						bpos = Integer.parseInt(s2[1]);
						if(grid.isLegalMove(gpos, bpos))break;
						System.err.println("Error. Enter a valid position move:");
						continue;
					}catch(Exception e){
						System.err.println("Critical error. Enter a valid position move:");
						continue;
					}
				}
				
				grid = grid.makeMove(Board.PLAYER_ID, gpos, bpos);
				printGrid(grid, ai, player);
				System.err.print("\n");
				turn = Board.AI_ID;
			}
		}
		
		//End game display
		if(grid.gamestate == Board.AI_ID){
			System.err.print("\nHahaha! I, the superior AI, win!\n\n");
		}else if(grid.gamestate == Board.PLAYER_ID){
			System.err.print("\nYou win!\n\n");
		}else{
			System.err.print("\nWe tied.\n\n");
		}
	}
	
	//Prints the board in a basic Tic-Tac-Toe.
	public void printBoard(Board b, String ai, String player){
		String[] array = new String[9];
		for(int i=0; i<9;i++){
			array[i] = (b.bArray[i]==Board.AI_ID ? ai : b.bArray[i]==Board.PLAYER_ID ? player : "-");
		}
		System.err.printf("%s%s%s\n%s%s%s\n%s%s%s\n", (Object[]) array);
	}
	
	
	//Prints the grid if the game played is 9-Board-Tic-Tac-Toe.
	public void printGrid(Grid g, String ai, String player){
		String[][] array = new String[9][9];
		
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				array[i][j] = (g.gArray[3*(i/3) + j/3].bArray[3*(i%3) + j%3]==Board.AI_ID ? ai 
							 : g.gArray[3*(i/3) + j/3].bArray[3*(i%3) + j%3]==Board.PLAYER_ID ? player 
							 : "-");
			}
			
			System.err.printf("%s%s%s  %s%s%s  %s%s%s\n", (Object[]) array[i]);
			if(i%3==2){
				System.err.print("\n");
			}
		}
	}
	
}

