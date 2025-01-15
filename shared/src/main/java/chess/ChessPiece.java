package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int dir = 1;
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            dir = -1;
        }
        if (type == PieceType.PAWN) {
            ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn());
            if (board.getPiece(forwardOne) == null) {
                addPawnMoves(moves, myPosition, forwardOne); //move forward one
            }
            if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)){
                ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + (dir * 2), myPosition.getColumn());
                moves.add(new ChessMove(myPosition, forwardTwo, null)); //move forward two if applicable
            }
            ChessPosition leftDiagonal = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() - dir);
            ChessPosition rightDiagonal = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() + dir);
            if (board.getPiece(leftDiagonal) != null) {
                addPawnMoves(moves, myPosition, leftDiagonal); //capture
            }
            if (board.getPiece(rightDiagonal) != null) {
                addPawnMoves(moves, myPosition, rightDiagonal); //capture
            }
        }
        else if (type == PieceType.ROOK) {
            for (int r = 1; r < 9; r++) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, myPosition.getColumn()), null));
            }
            for (int c = 1; c < 9; c++) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), c), null));
            }
        }
        else if (type == PieceType.KNIGHT) {
            for (int r = 1; r < 9; r++) {
                for (int c = 1; c < 9; c++) {
                    if ((Math.abs(myPosition.getRow() - r) == 1 && Math.abs(myPosition.getColumn() - c) == 2) || (Math.abs(myPosition.getRow() - r) == 2 && Math.abs(myPosition.getColumn() - c) == 1)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                    }
                }
            }
        }
        else if (type == PieceType.BISHOP) {
            for (int r = 1; r < 9; r++) {
                for (int c = 1; c < 9; c++) {
                    if (Math.abs(myPosition.getRow() - r) == Math.abs(myPosition.getColumn() - c)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                    }
                }
            }
        }
        return moves;
    }

    public void addPawnMoves(Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(myPosition, endPosition, null));
        if (endPosition.getRow() == 1 || endPosition.getRow() == 8) {
            moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
        }
    }
}
