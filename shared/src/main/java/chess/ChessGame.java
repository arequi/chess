package chess;

import javax.imageio.stream.ImageOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    Collection<ChessMove> movesCollection;
    Collection<ChessMove> cloneMovesCollection;
    Collection<ChessMove> movesInCheckCollection;
    Collection<ChessMove> currentMoves;
    ChessGame.TeamColor teamColor;
    ChessBoard board;
    ChessBoard clonedBoard;
    ChessPosition kingPosition;
    private boolean isGameOverResign;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        this.setTeamTurn(TeamColor.WHITE);
        this.setGameOverResign(false);
    }

    public boolean getGameOverResign() {
        return this.isGameOverResign;
    }
    public void setGameOverResign(boolean gameOver) {
        this.isGameOverResign = gameOver;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    // find current king position
    public ChessPosition findKing(ChessBoard myBoard, TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j ++) {
                ChessPosition position = new ChessPosition(i,j);
                if (myBoard.getPiece(position) != null) {
                    ChessPiece.PieceType type = myBoard.getPiece(position).getPieceType();
                    if (type == ChessPiece.PieceType.KING && myBoard.getPiece(position).getTeamColor() == teamColor) {
                        kingPosition = position;
                    }
                }
            }
        }
        return kingPosition;
    }

    public Boolean isKingInDanger(ChessBoard myBoard, TeamColor teamColor) {
        movesInCheckCollection = new HashSet<>();
        ChessPosition myKing = findKing(myBoard, teamColor);
        // iterates through every piece on board
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                // only iterates through pieces of opposite color
                if (myBoard.getPiece(position) != null && myBoard.getPiece(position).getTeamColor() != teamColor) {
                    ChessPiece.PieceType type = myBoard.getPiece(position).getPieceType();
                    TeamColor currentColor = myBoard.getPiece(position).getTeamColor();
                    ChessPiece piece = new ChessPiece(currentColor, type);
                    cloneMovesCollection = new HashSet<>();
                    // gets all possible moves for this piece and stores them in cloneMovesCollection
                    cloneMovesCollection.addAll(piece.pieceMoves(myBoard,position));
                    Iterator<ChessMove> itr = cloneMovesCollection.iterator();
                    // iterates through all moves for this piece
                    while (itr.hasNext()) {
                        ChessMove nextMove = itr.next();
                        // adds current move to collection if it lands on the king
                        if (nextMove.getEndPosition().equals(myKing)) {
                            movesInCheckCollection.add(nextMove);
                        }
                    }
                }
            }
        }
        // if there is a way out of check from any move, it's not in danger, so return false
        if (movesInCheckCollection.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        movesCollection = new HashSet<>();
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        ChessPiece.PieceType type = board.getPiece(startPosition).getPieceType();
        ChessPiece currentPiece = new ChessPiece(teamColor, type);
        movesCollection = currentPiece.pieceMoves(board, startPosition);
        if (movesCollection == null) {
            return null;
        }
        Collection<ChessMove> validMovesCollection = new HashSet<>();
        //check if pieceMoves put or leave king in check
        Iterator movesItr = movesCollection.iterator();
        while (movesItr.hasNext()) {
            ChessMove nextMove = (ChessMove) movesItr.next();
                // create cloned chess board
                clonedBoard = (ChessBoard) board.chessBoardCopy();
                // make move in cloned board
                clonedBoard.addPiece(nextMove.getEndPosition(),currentPiece);
                clonedBoard.addPiece(nextMove.getStartPosition(), null);
                // check if any piece on the board can capture king of THIS color
                // add to collection of moves ONLY if MY king is not in check
                if (!isKingInDanger(clonedBoard, teamColor)) {
                    validMovesCollection.add(nextMove);
                }
        }
        return validMovesCollection;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessGame.TeamColor currentColor = board.getPiece(move.getStartPosition()).getTeamColor();
        if (currentColor != getTeamTurn()) {
            throw new InvalidMoveException("Invalid move: It's not your turn!");
        }
        movesCollection = validMoves(move.getStartPosition());
        Collection<ChessMove> validMovesCollection = new HashSet<>();
        Iterator itr = movesCollection.iterator();
        while (itr.hasNext()) {
            ChessMove currentMove = (ChessMove) itr.next();
            if (currentMove.equals(move)) {
                validMovesCollection.add(currentMove);
            }
        }
        if (validMovesCollection.isEmpty()) {
            throw new InvalidMoveException("Invalid move!");
        }
        if (move.getPromotionPiece() != null) {
            // turn piece into promotion piece
            ChessPiece promotionPiece = new ChessPiece(currentColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        }
        else {
            ChessPiece.PieceType type = board.getPiece(move.getStartPosition()).getPieceType();
            ChessPiece currentPiece = new ChessPiece(currentColor, type);
            board.addPiece(move.getEndPosition(), currentPiece);
        }
        board.addPiece(move.getStartPosition(), null);
        if (isInCheck(getTeamTurn())) {
            throw new InvalidMoveException("Invalid move: You put your king in check!");
        }
        if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // check if king of THIS color can be captured by any piece on the board
        movesCollection = new HashSet<>();
        currentMoves = new HashSet<>();
        //find position of current king
        kingPosition = findKing(board, teamColor);
        //loop through all positions on the board and
        // add all moves each piece can make to movesCollection set
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() != teamColor) {
                    ChessPiece currentPiece = new ChessPiece(board.getPiece(position).getTeamColor(), board.getPiece(position).getPieceType());
                    currentMoves = currentPiece.pieceMoves(board, position);
                    movesCollection.addAll(currentMoves);
                }
            }
        }
        Iterator<ChessMove> itr = movesCollection.iterator();
        while(itr.hasNext()) {
            if (itr.next().getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        movesCollection = new HashSet<>();
            // find position of king whose turn it is currently
            kingPosition = findKing(board, teamColor);
            // find all possible moves the king can make
            ChessPiece kingPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
            movesCollection = kingPiece.pieceMoves(board, kingPosition);
            ChessBoard clonedBoardReset = board.chessBoardCopy();
            clonedBoardReset.resetBoard();
            if (board.equals(clonedBoardReset)) {
                return false;
            }
            Iterator<ChessMove> itr = movesCollection.iterator();
            ChessMove currentMove;
            while (itr.hasNext()) {
                currentMove = itr.next();
                kingPosition = currentMove.getEndPosition();
                clonedBoard = board.chessBoardCopy();
                clonedBoard.addPiece(currentMove.getEndPosition(), kingPiece);
                clonedBoard.addPiece(currentMove.getStartPosition(), null);
                // simulate this move by getting all valid moves from each piece.
                // If any piece's moves land on the king, it's still in checkMate
                if (!isKingInDanger(clonedBoard, teamColor)) {
                    return false;
                }
            }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> currentMovesCollection = new HashSet<>();
        // simplified version from real chess: if you don't have any valid moves
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (board.getPiece(currentPosition) != null) {
                    ChessGame.TeamColor currentColor = board.getPiece(currentPosition).getTeamColor();
                    if (currentColor == teamColor) {
                        Collection <ChessMove> moves = validMoves(currentPosition);
                        if (!moves.isEmpty()) {
                            currentMovesCollection.addAll(moves);
                        }
                    }
                }

            }
        }
        if (currentMovesCollection.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
