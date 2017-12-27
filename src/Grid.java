import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Xiaoning Guo
 * CSC 242: Artificial Intelligence Project 1 Tic-Tac-Toe
 * 
 * This is a grid class that stores boards in a 3 x 3 grid for 9-Tic-Tac-Toe.
 */
public class Grid {
	Board[] gArray; //Stores boards in an array
	HashSet<Integer> gMoves; //Moves regarding position on the grid.
	int gamestate; //Records whether the game is won or drawn out
	int nodeCount, heurCount, leafCount, bestUtil, depth; //For printing summary after each move
	int timer; //How long it takes for the argmax minimax to find an optimal move
	
	public Grid(){
		gArray = new Board[9];
		gMoves = new HashSet<>();
		gamestate = Board.NOT_TERMINAL;
	}
	
	
	public void initialState(){
		for(int i=0; i<9; i++){
			gArray[i] = new Board();
			gArray[i].initialState();
			gMoves.add(i+1);
		}
		nodeCount = heurCount = leafCount = depth = 0;
	}

	//Returns a clone of the board if the player makes this move.
	public Grid makeMove(int player, int gpos, int bpos){
		Grid newGrid = clone();
		newGrid.gMoves = new HashSet<>();
		newGrid.gArray[gpos-1] = newGrid.gArray[gpos-1].makeMove(player, bpos);
		
		int result = newGrid.gArray[gpos-1].isTerminalState();
		if(Math.abs(result)==1){
			newGrid.gamestate = result;
		}
		
		/*
		 * If the last move points to a board that's filled up, any board is open now.
		 * Otherwise, the last move will be the only board that you can play in.
		 */
		if(newGrid.gArray[bpos-1].isTerminalState()==Board.TIE){
			for(int i=0; i<9;i++){
				if(gArray[i].isTerminalState()!=Board.TIE)
						newGrid.gMoves.add(i+1);
			}
		}else{
			newGrid.gMoves.add(bpos);
		}
		
		//If all boards are tied, then the whole game ends in a draw.
		if(newGrid.gMoves.isEmpty()){
			newGrid.gamestate = Board.TIE;
		}

		return newGrid;
	}
	
	
	public boolean isLegalMove(int gpos, int bpos){
		
		if (gMoves.contains(gpos) && gArray[gpos-1].isLegalMove(bpos)){
			return true;
		}
		return false;
	}
	
	/*
	 * Returns the position arguments that maximizes the heuristic values of the minimax function
	 * A random choice is made if the heuristic values are identical.
	 */
	public Integer[] decision(boolean noprune){
		int best = -Board.INFINITY;
		//2 dimensional array, 1 for grid position and the other for board position.
		ArrayList<Integer[]> moveset = new ArrayList<Integer[]>(); 

		long startTime = System.currentTimeMillis(); //Begin timer
		
		for(Integer gpos: gMoves){
			for(Integer bpos: gArray[gpos-1].moves){
				int score;
				
				if(noprune){
					score = minimax(makeMove(Board.AI_ID, gpos, bpos), 
							optimalDepth(), -Board.AI_ID);
				}else{
					score = minimax(makeMove(Board.AI_ID, gpos, bpos), 
							optimalDepth(), -Board.INFINITY, Board.INFINITY, -Board.AI_ID);
				}
				
				if(score > best){
					best = score;
					moveset = new ArrayList<Integer[]>();
					Integer[] temp = new Integer[2];
					temp[0] = gpos;	temp[1] = bpos;
					moveset.add(temp);
				}else if(score == best){
					Integer[] temp = new Integer[2];
					temp[0] = gpos; temp[1] = bpos;
					moveset.add(temp);
				}
			}
		}
		
		bestUtil = best;
		long endTime = System.currentTimeMillis(); //Begin timer
		timer = (int)(endTime - startTime);
		int choice = (int) (Math.random()*(moveset.size()));
		return moveset.get(choice);
	}
	
	
	
	/*
	 * The depth-limited minimax version without alpha-beta pruning.
	 * Implementation of the pseudo-code found on Wikipedia:
	 * https://en.wikipedia.org/wiki/Minimax
	 * 
	 * +10 utility for a win
	 * 0 utility for a tie
	 * -10 utility for a loss
	 * +-N up to a maximum of 9, +1 for each 2 in a row on a board 
	 */
	public int minimax(Grid g, int depth, int player){
		nodeCount++;
		
		if(g.gamestate != Board.NOT_TERMINAL){
			leafCount++;
			return g.gamestate*10; //This needs to be 10 because the maximum value of heuristic is 9
		}else if(depth==0){
			heurCount++;
			return g.heuristic();
		}
		
		else if(player==Board.AI_ID){
			int best = -Board.INFINITY;

			for(Integer gpos: g.gMoves){
				for(Integer bpos: g.gArray[gpos-1].moves){
					best = Math.max(best, minimax(g.makeMove(player, gpos, bpos), depth-1, -player));
				}
			}
			
			return best;
			
		}else{
			int worst = Board.INFINITY;
		
			for(Integer gpos: g.gMoves){
				for(Integer bpos: g.gArray[gpos-1].moves){
					worst = Math.min(worst, minimax(g.makeMove(player, gpos, bpos), depth-1, -player));
				}
			}
			
			return worst;
		}
	}
	
	
	/*
	 * The depth-limited minimax version with alpha-beta pruning.
	 * Implementation of the pseudo-code found on Wikipedia:
	 * https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
	 * 
	 * +10 utility for a win
	 * 0 utility for a tie
	 * -10 utility for a loss
	 * +-N up to a maximum of 9, +1 for each 2 in a row on a board 
	 */
	public int minimax(Grid g, int depth, int alpha, int beta, int player){
		nodeCount++;
		
		if(g.gamestate != Board.NOT_TERMINAL){
			leafCount++;
			return g.gamestate*10; //This needs to be 10 because the maximum value of heuristic is 9
		}else if(depth==0){
			heurCount++;
			return g.heuristic();
		}
		
		else if(player==Board.AI_ID){
			int best = -Board.INFINITY;

			for(Integer gpos: g.gMoves){
				for(Integer bpos: g.gArray[gpos-1].moves){
					best = Math.max(best, minimax(g.makeMove(player, gpos, bpos), depth-1, alpha, beta, -player));
					alpha = Math.max(alpha, best);
					
					if(beta <= alpha){
						break;
					}
				}
			}
			
			return best;
			
		}else{
			int worst = Board.INFINITY;
		
			for(Integer gpos: g.gMoves){
				for(Integer bpos: g.gArray[gpos-1].moves){
			
					worst = Math.min(worst, minimax(g.makeMove(player, gpos, bpos), depth-1, alpha, beta, -player));
					beta = Math.min(beta, worst);
				
					if(beta <= alpha){
						break;
					}
				}
			}
			
			return worst;
		}
	}
	
	/*
	 * Returns the optimal depth for which minimax cuts off. 
	 * It works by having a set number of operations and taking the log
	 * with the base numbers of branches. 9^6 operations starts taking longer
	 * than instant time, so we can change the depth to match that number of operations.
	 */
	public int optimalDepth(){
		int sum = 0;
		for(Board b: gArray){
			sum+=b.moves.size();
		}
		
		if(sum < 9) sum = 9;
		depth = (int)(Math.log((int) Math.pow(9, 7))/Math.log(sum/9));
		return depth;
	}
	
	//Return the total number of 2 in a row pieces on the board.
	public int heuristic(){
		int sum = 0;
		for(Board b: gArray){
			sum += b.isGoodState();
		}
		return sum;
	}
	
	// Returns a deep copy of the grid.
	public Grid clone(){
		Grid newGrid = new Grid();
		newGrid.gMoves = new HashSet<>();
		newGrid.gMoves.addAll(gMoves);
		newGrid.gArray = gArray.clone();
		return newGrid;
	}
	
}
