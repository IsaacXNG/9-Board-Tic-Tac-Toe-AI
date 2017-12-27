import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Xiaoning Guo
 * CSC 242: Artificial Intelligence Project 1 Tic-Tac-Toe
 * 
 * This is a board class that stores the information of a single board in an array
 */
public class Board {
	static final int NOT_TERMINAL = -10, TIE = 0,
					 AI_ID = 1,	PLAYER_ID = -1,
					 INFINITY = 100;
					 
	Integer[] bArray;       //Records the state of the game.
	HashSet<Integer> moves; //Available moves.
	int nodeCount, leafCount, bestUtil; //For printing summary after each move
	long timer; //How long it takes for the argmax minimax to find an optimal move

	public Board(){
		bArray = new Integer[9];
		moves = new HashSet<>();
		nodeCount = leafCount = 0;
		bestUtil = -INFINITY;
	}
	
	/*
	 * Initializing the integer array to all 0 signifies that no moves have been made yet. 
	 * Only the first board should have all 0s. 
	 */
	public void initialState(){
		for(int i=0; i<9; i++){
			moves.add(i+1);
			bArray[i] = 0;
		}
	}
	
	/*
	 * If there are no available moves and no one has won, then the game is a tie and returns 0.
	 * If 3 in a row wins, then the sum of the elements will equal either -3 or 3. 
	 * Returning one of the entrees will return the victor of the game. 1 for AI, -1 for player.
	 * 
	 * Returns NOT_TERMINAL(-10) if the game has not ended yet.
	 */
	public int isTerminalState(){
		if(Math.abs(bArray[0]+bArray[1]+bArray[2])==3){       //Checks rows
			return bArray[0];
		}else if(Math.abs(bArray[3]+bArray[4]+bArray[5])==3){
			return bArray[3];
		}else if(Math.abs(bArray[6]+bArray[7]+bArray[8])==3){
			return bArray[6];
		}else if(Math.abs(bArray[0]+bArray[3]+bArray[6])==3){ //Checks columns
			return bArray[0];
		}else if(Math.abs(bArray[1]+bArray[4]+bArray[7])==3){
			return bArray[1];
		}else if(Math.abs(bArray[2]+bArray[5]+bArray[8])==3){
			return bArray[2];
		}else if(Math.abs(bArray[0]+bArray[4]+bArray[8])==3){ //Checks diagonals
			return bArray[0];
		}else if(Math.abs(bArray[2]+bArray[4]+bArray[6])==3){
			return bArray[2];
		}
		if(moves.isEmpty()){
			return TIE;
		}
		
		//Check tie
		return NOT_TERMINAL; 
	}
	
	/*
	 * If there are any 2 in a rows, it's a good sign that the player is heading towards the right direction.
	 * Returns a heuristic value of +1 for AI or -1 for player.
	 * Returns 0 if none of these has occurred. 
	 */
	public int isGoodState(){
		if(Math.abs(bArray[0]+bArray[1]+bArray[2])==2){       //Checks rows
			return bArray[0];
		}else if(Math.abs(bArray[3]+bArray[4]+bArray[5])==2){
			return bArray[3];
		}else if(Math.abs(bArray[6]+bArray[7]+bArray[8])==2){
			return bArray[6];
		}else if(Math.abs(bArray[0]+bArray[3]+bArray[6])==2){ //Checks columns
			return bArray[0];
		}else if(Math.abs(bArray[1]+bArray[4]+bArray[7])==2){
			return bArray[1];
		}else if(Math.abs(bArray[2]+bArray[5]+bArray[8])==2){
			return bArray[2];
		}else if(Math.abs(bArray[0]+bArray[4]+bArray[8])==2){ //Checks diagonals
			return bArray[0];
		}else if(Math.abs(bArray[2]+bArray[4]+bArray[6])==2){
			return bArray[2];
		}
	
		return 0;
		
	}
	
	
	//Returns a clone of the board if the player makes this move.
	public Board makeMove(int player, int position){
		Board newBoard = clone();
		newBoard.moves.remove(position);
		newBoard.bArray[position-1] = player;
		return newBoard;
	}
	
	public boolean isLegalMove(int index){
		return (moves.contains(index));
	}
	
	
	/* 
	 * Finds the position argument that maximizes the utility of minimax.
	 * A random choice is made if the utilities are the same.
	 */
	public int decision(boolean noprune){
		int best = -INFINITY;
		ArrayList<Integer> moveset = new ArrayList<Integer>();
		
		long startTime = System.currentTimeMillis(); //Beginner timer
		
		for(Integer move: moves){
			int score;
			
			if(noprune){
				score = minimax(makeMove(AI_ID, move), -AI_ID);
			}else{
				score = minimax(makeMove(AI_ID, move), -INFINITY, INFINITY, -AI_ID);
			}
			
			if(score > best){
				best = score;
				moveset = new ArrayList<Integer>();
				moveset.add(move);
			}else if(score == best){
				moveset.add(move);
			}
		}
		
		int choice = (int) (Math.random()*(moveset.size()));
		
		long endTime = System.currentTimeMillis(); //End timer
		timer = (int)(endTime - startTime);
		bestUtil = best;
		
		return(moveset.get(choice));
	}
	
	
	/*
	 * The pure minimax version without alpha-beta pruning.
	 * Implementation of the pseudo-code found on Wikipedia:
	 * https://en.wikipedia.org/wiki/Minimax
	 * 
	 * +1 utility for a win
	 * 0 utility for a tie
	 * -1 utility for a loss
	 */
	public int minimax(Board bn, int player){
		int code = bn.isTerminalState();
		nodeCount++; 
		
		if(code!=NOT_TERMINAL){
			leafCount++;
			return code;
			
		}else if(player==AI_ID){
			int best = -INFINITY;

			for(Integer move: bn.moves){
				best = Math.max(best, minimax(bn.makeMove(player, move), -player));
			}
			
			return best;
			
		}else{
			int worst = INFINITY;
		
			for(Integer move: bn.moves){
				worst = Math.min(worst, minimax(bn.makeMove(player, move), -player));
			}
			
			return worst;
		}
	}
	
	
	/*
	 * The pure minimax version with alpha-beta pruning.
	 * Implementation of the pseudo-code found on Wikipedia:
	 * https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
	 * 
	 * +1 utility for a win
	 * 0 utility for a tie
	 * -1 utility for a loss
	 */
	public int minimax(Board bn, int alpha, int beta, int player){
		int code = bn.isTerminalState();
		nodeCount++; 
		
		if(code!=NOT_TERMINAL){
			leafCount++;
			return code;
			
		}else if(player==AI_ID){
			int best = -INFINITY;

			for(Integer move: bn.moves){
				best = Math.max(best, minimax(bn.makeMove(player, move), alpha, beta, -player));
				alpha = Math.max(alpha, best);
				
				if(beta <= alpha){
					break;
				}
			}
			
			return best;
			
		}else{
			int worst = INFINITY;
		
			for(Integer move: bn.moves){
				worst = Math.min(worst, minimax(bn.makeMove(player, move), alpha, beta, -player));
				beta = Math.min(beta, worst);
				
				if(beta <= alpha){
					break;
				}
			}
			
			return worst;
		}
	}
	
	//Returns a deep copy of the board
	public Board clone(){
		Board newBoard = new Board();
		newBoard.moves = new HashSet<>();
		newBoard.moves.addAll(moves);
		newBoard.bArray = bArray.clone();
		return newBoard;
	}
}
