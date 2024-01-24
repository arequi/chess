package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    Collection<ChessMove> pawnMovesCollection;
    Collection<ChessMove> bishopMovesCollection;
    Collection<ChessMove> queenMovesCollection;
    Collection<ChessMove> kingMovesCollection;
    Collection<ChessMove> rookMovesCollection;
    Collection<ChessMove> knightMovesCollection;
    ChessMove move;
    ChessPiece.PieceType promotionPiece;
    ChessPosition newPosition;
    ChessPosition currentPosition;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    // get moves for all promotion pieces, not just one
    public boolean needsPromotionPiece(ChessPosition position, ChessGame.TeamColor color) {
        // for white
        if (color == ChessGame.TeamColor.WHITE && position.getRow() == 7) {
            return true;
        }
        // for black
        else if (color == ChessGame.TeamColor.BLACK && position.getRow() == 2) {
            return true;
        }
        else {
            return false;
        }
    }

    public void addPromotionPieces(ChessPosition position, ChessGame.TeamColor color, ChessPosition direction) {
        promotionPiece = PieceType.KNIGHT;
        move = new ChessMove(position, direction, promotionPiece);
        pawnMovesCollection.add(move);
        promotionPiece = PieceType.BISHOP;
        move = new ChessMove(position, direction, promotionPiece);
        pawnMovesCollection.add(move);
        promotionPiece = PieceType.ROOK;
        move = new ChessMove(position, direction, promotionPiece);
        pawnMovesCollection.add(move);
        promotionPiece = PieceType.QUEEN;
        move = new ChessMove(position, direction, promotionPiece);
        pawnMovesCollection.add(move);
    }

    public boolean isPawnBlocked(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ChessPosition checkedPosition;
        // check if any-colored pieces are up ahead for white
        if (color == ChessGame.TeamColor.WHITE) {
            checkedPosition = new ChessPosition(position.getRow()+1, position.getColumn());
            if (board.getPiece(checkedPosition) == null) {
                return false;
            }
            else {
                return true;
            }
        }
        // check if any-colored pieces are down ahead for black
        else {
            checkedPosition = new ChessPosition(position.getRow()-1, position.getColumn());
            if (board.getPiece(checkedPosition) == null) {
                return false;
            }
            else {
                return true;
            }
        }
    }
    public void captureEnemyDiag(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ChessPosition leftUp = new ChessPosition(position.getRow()+1, position.getColumn()-1);
        ChessPosition rightUp = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
        ChessPosition leftDown = new ChessPosition(position.getRow()-1, position.getColumn()-1);
        ChessPosition rightDown = new ChessPosition(position.getRow()-1, position.getColumn()+1);
        if (color == ChessGame.TeamColor.WHITE) {
            // left diag position
            if (board.getPiece(leftUp) != null) {
                if (board.getPiece(leftUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    // check if pawn will land on last row
                    if (needsPromotionPiece(position, color)) {
                        addPromotionPieces(position, color, leftUp);
                    }
                    else {
                        move = new ChessMove(position, leftUp, null);
                        pawnMovesCollection.add(move);
                    }

                }
            }
                // right diag position
            if (board.getPiece(rightUp) != null) {
                if (board.getPiece(rightUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (needsPromotionPiece(position, color)) {
                        addPromotionPieces(position, color, rightUp);
                    }
                    else {
                        move = new ChessMove(position, rightUp, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
        }
        // for black pieces
        else {
            // left diag position
            if (board.getPiece(leftDown) != null) {
                if (board.getPiece(leftDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    //check if pawn will land on last row (needs promotionPiece)
                    if (needsPromotionPiece(position, color)) {
                        addPromotionPieces(position, color, leftDown);
                    }
                    else {
                        move = new ChessMove(position, leftDown, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
            // right diag position
            if (board.getPiece(rightDown) != null) {
                if (board.getPiece(rightDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (needsPromotionPiece(position, color)) {
                        addPromotionPieces(position, color, rightDown);
                    }
                    else {
                        move = new ChessMove(position, rightDown, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
        }
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        kingMovesCollection = new HashSet<>();
        return null;
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        queenMovesCollection = new HashSet<>();
        return null;
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        bishopMovesCollection = new HashSet<>();

        currentPosition = myPosition;
        // upLeft movement
        while (currentPosition.getRow() != 8 && currentPosition.getColumn() != 1) {
            ChessPosition upLeft = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()-1);
            if (board.getPiece(upLeft) == null) {
                move = new ChessMove(myPosition, upLeft, null);
                bishopMovesCollection.add(move);
                currentPosition = upLeft;
            }
            else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(upLeft).getTeamColor()) {
                break;
            }
            else {
                move = new ChessMove(myPosition, upLeft, null);
                bishopMovesCollection.add(move);
                break;
            }
        }
        // up right movement
        currentPosition = myPosition;
        while (currentPosition.getRow() != 8 && currentPosition.getColumn() != 8) {
            ChessPosition upRight = new ChessPosition(currentPosition.getRow()+1, currentPosition.getColumn()+1);
            if (board.getPiece(upRight) == null) {
                move = new ChessMove(myPosition, upRight, null);
                bishopMovesCollection.add(move);
                currentPosition = upRight;
            }
            else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(upRight).getTeamColor()) {
                break;
            }
            else {
                move = new ChessMove(myPosition, upRight, null);
                bishopMovesCollection.add(move);
                break;
            }
        }
        // down left movement
        currentPosition = myPosition;
        while (currentPosition.getRow() != 1 && currentPosition.getColumn() != 1) {
            ChessPosition downLeft = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()-1);
            if (board.getPiece(downLeft) == null) {
                move = new ChessMove(myPosition, downLeft, null);
                bishopMovesCollection.add(move);
                currentPosition = downLeft;
            }
            else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(downLeft).getTeamColor()) {
                break;
            }
            else {
                move = new ChessMove(myPosition, downLeft, null);
                bishopMovesCollection.add(move);
                break;
            }
        }
        // down right movement
        currentPosition = myPosition;
        while (currentPosition.getRow() != 1 && currentPosition.getColumn() != 1) {
            ChessPosition downRight = new ChessPosition(currentPosition.getRow()-1, currentPosition.getColumn()+1);
            if (board.getPiece(downRight) == null) {
                move = new ChessMove(myPosition, downRight, null);
                bishopMovesCollection.add(move);
                currentPosition = downRight;
            }
            else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(downRight).getTeamColor()) {
                break;
            }
            else {
                move = new ChessMove(myPosition, downRight, null);
                bishopMovesCollection.add(move);
                break;
            }
        }
        return bishopMovesCollection;
    }
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        knightMovesCollection = new HashSet<>();
        return null;
    }
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        rookMovesCollection = new HashSet<>();
        return null;
    }
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        pawnMovesCollection = new HashSet<>();

        // white pawn types
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            // white pawn moving forward on first move
            if (myPosition.getRow() == 2) {
                //check if blocked from current position
                if (!isPawnBlocked(board, myPosition, ChessGame.TeamColor.WHITE)) {
                    newPosition = new ChessPosition(3, myPosition.getColumn());
                    move = new ChessMove(myPosition, newPosition, null);
                    pawnMovesCollection.add(move);
                    //if not blocked from current position, check if blocked from next position
                    if (!isPawnBlocked(board, newPosition, ChessGame.TeamColor.WHITE)) {
                        newPosition = new ChessPosition(4, myPosition.getColumn());
                        move = new ChessMove(myPosition, newPosition, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
            // white pawn moving forward on second move or later
            else {
                if (!isPawnBlocked(board, myPosition, ChessGame.TeamColor.WHITE)) {
                    newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                    if (needsPromotionPiece(myPosition, ChessGame.TeamColor.WHITE)) {
                        addPromotionPieces(myPosition, ChessGame.TeamColor.WHITE, newPosition);
                    }
                    else {
                        move = new ChessMove(myPosition, newPosition, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
            // check if any white pieces are diagonal (vulnerable to take) (decrement row by 1 & increment AND decrement column by 1)
            captureEnemyDiag(board, myPosition, ChessGame.TeamColor.WHITE);
        }
        // black pawn types
        else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            // black pawn moving forward on first move
            if (myPosition.getRow() == 7) {
                if (!isPawnBlocked(board, myPosition, ChessGame.TeamColor.BLACK)) {
                    newPosition = new ChessPosition(6, myPosition.getColumn());
                    move = new ChessMove(myPosition, newPosition, null);
                    pawnMovesCollection.add(move);
                    if (!isPawnBlocked(board, newPosition, ChessGame.TeamColor.BLACK)) {
                        newPosition = new ChessPosition(5, myPosition.getColumn());
                        move = new ChessMove(myPosition, newPosition, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
            // black pawn moving forward on second move or later
            else {
                if (!isPawnBlocked(board, myPosition, ChessGame.TeamColor.BLACK)) {
                    newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                    if (needsPromotionPiece(myPosition, ChessGame.TeamColor.BLACK)) {
                        addPromotionPieces(myPosition, ChessGame.TeamColor.BLACK, newPosition);
                    }
                    else {
                        move = new ChessMove(myPosition, newPosition, null);
                        pawnMovesCollection.add(move);
                    }
                }
            }
            // check if any black pieces are diagonal (vulnerable to take) (increment row by 1 & increment OR decrement column by 1 (first check to stay in bounds of chess board ()
            captureEnemyDiag(board, myPosition, ChessGame.TeamColor.BLACK);
        }
        return pawnMovesCollection;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(board.getPiece(myPosition).getPieceType()) {
            case KING:
                // code block
                return kingMoves(board, myPosition);
            case QUEEN:
                // code block
                return queenMoves(board, myPosition);
            case BISHOP:
                // code block
                return bishopMoves(board, myPosition);
            case KNIGHT:
                // code block
                return knightMoves(board, myPosition);
            case ROOK:
                // code block
                return rookMoves(board, myPosition);
            case PAWN:
                // code block
                return pawnMoves(board, myPosition);
            case null:
                return null;
        }
    }
}
