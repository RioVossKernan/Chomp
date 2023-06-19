# Chomp

Game Rules:
    Chomp is a ancient logic game. The board is a 10x10 grid of chips. The bottom left chip is deadly. Players take turns clicking on chips. When a chip is clicked, it disappears allong with all chips above and the right of it, leaving a rectangluar "Chomp" in the board. As the board slowly shrinks, a player is eventually forced to click the death chip and lose the game. 
    

Algorithm:
    A user can play against themselves, a friend, or my algorithm. Good luck playing against my computer. I formed my algorithm by slowly expanding its knowledge by starting with the simplest of boards and progressivly growing more complex. A move that leaves only the death chip is a winning move becuase it forces the opponent to lose. Any move that leaves the opponent in a position to make a winning move is therefore a losing move. 
    By starting with a 1-chip board and testing boarding 1 chip at a time, catagorizing them as winning or losing along the way, I am able to store every single board as either a winner or a loser (assuming optimal play). Once you give the algorithm a winning position it will be able to force you into a loss regardless of your future moves. 
    Sometimes the algorithm is forced to take a losing move, meaning that you should win assuming you play perfectly. In this situation the algorithm chooses the move that has the longest path of moves until death. This forces you to play perfectly for as long as possible, giving you as many chances to make a mistake as possible. One mistake you will be in a losing position where beating the algorithm is yet again impossible. 
    Recreating this historic game was fun, but the real pleasure was the problem-solving and outside-the-box thinking of forming my unbeatable algorithm. One of the best feelings in programming. Good Luck...
    
