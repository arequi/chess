package chess;

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
    Collection<ChessMove> currentMoves;
    ChessGame.TeamColor teamColor;
    ChessBoard board;
    ChessBoard clonedBoard;
    ChessPosition kingPosition;

    public ChessGame() {

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
    public ChessPosition findKing(ChessBoard board) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j ++) {
                ChessPosition position = new ChessPosition(i,j);
                if (board.getPiece(position) != null) {
                    ChessPiece.PieceType type = board.getPiece(position).getPieceType();
                    if (type == ChessPiece.PieceType.KING && board.getPiece(position).getTeamColor() == getTeamTurn()) {
                        kingPosition = position;
                    }
                }
            }
        }
        return kingPosition;
    }


    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // TODO: replace this method with moves that don't leave
        //  your own king in check or put your king in check
        // TODO: possibly need to clone board
        movesCollection = new HashSet<>();
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        setTeamTurn(board.getPiece(startPosition).getTeamColor());
        ChessPiece.PieceType type = board.getPiece(startPosition).getPieceType();
        ChessPiece currentPiece = new ChessPiece(teamColor, type);
        movesCollection = currentPiece.pieceMoves(board, startPosition);
        if (movesCollection == null) {
            return null;
        }
        //check if pieceMoves put or leave king in check
        Iterator movesItr = movesCollection.iterator();
        while (movesItr.hasNext()) {
            ChessMove nextMove = (ChessMove) movesItr.next();
            try {
                // create cloned chess board
                clonedBoard = (ChessBoard) board.clone();
                // make move in cloned board
                clonedBoard.addPiece(nextMove.getEndPosition(),currentPiece);
                // check if any piece on the board can capture king of THIS color
                // add to collection of moves ONLY if MY king is not in check
                if (!isInCheck(getTeamTurn())) {
                    movesCollection.add(nextMove);
                }
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return movesCollection;
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
        if (isInCheck(getTeamTurn())) {
            throw new InvalidMoveException("Invalid move: You put your king in check!");
        }
        board.addPiece(move.getStartPosition(), null);
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
        //get color that is passed in
        setTeamTurn(teamColor);
        //find position of current king
        kingPosition = findKing(board);
        //loop through all positions on the board and
        // add all moves each piece can make to movesCollection set
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() != getTeamTurn()) {
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
        //TODO: 1. clone board, apply move, then call isinCheck method


            // find position of king whose turn it is currently
            kingPosition = findKing(board);
            // find all possible moves the king can make
            ChessPiece kingPiece = new ChessPiece(getTeamTurn(), ChessPiece.PieceType.KING);
            movesCollection = kingPiece.pieceMoves(board, kingPosition);
            // TODO: if no moves get you out of check, then you're in check mate
            Iterator<ChessMove> itr = movesCollection.iterator();
            ChessMove currentMove;
            while (itr.hasNext()) {
                currentMove = itr.next();
                try {
                clonedBoard = (ChessBoard) board.clone();
                clonedBoard.addPiece(currentMove.getEndPosition(), kingPiece);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                // return false if a single move can get the king out of check
                if (!isInCheck(getTeamTurn())) {
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
        movesCollection = new HashSet<>();
        // simplified version from real chess: if you don't have any valid moves
        throw new RuntimeException("Not implemented");
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
