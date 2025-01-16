package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Objects;

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
            if (myPosition.getRow() != 8 && myPosition.getRow() != 1) {
                ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn());
                if (board.getPiece(forwardOne) == null) {
                    addPawnMoves(board, moves, myPosition, forwardOne); //move forward one
                }
                if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)){
                    ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + (dir * 2), myPosition.getColumn());
                    moves.add(new ChessMove(myPosition, forwardTwo, null)); //move forward two if applicable
                }
                if (!((myPosition.getColumn() == 1 && pieceColor == ChessGame.TeamColor.WHITE) || (myPosition.getColumn() == 8 && pieceColor == ChessGame.TeamColor.BLACK))) {
                    ChessPosition leftDiagonal = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() - dir);
                    if (board.getPiece(leftDiagonal) != null) {
                        addPawnMoves(board, moves, myPosition, leftDiagonal); //capture
                    }
                }
                if (!((myPosition.getColumn() == 8 && pieceColor == ChessGame.TeamColor.WHITE) || (myPosition.getColumn() == 1 && pieceColor == ChessGame.TeamColor.BLACK))) {
                    ChessPosition rightDiagonal = new ChessPosition(myPosition.getRow() + dir, myPosition.getColumn() + dir);
                    if (board.getPiece(rightDiagonal) != null) {
                        addPawnMoves(board, moves, myPosition, rightDiagonal); //capture
                    }
                }
            }
        }
        else if (type == PieceType.ROOK) {
            addRookMoves(board, moves, myPosition);
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
            addBishopMoves(board, moves, myPosition);
        }
        else if (type == PieceType.QUEEN) {
            addRookMoves(board, moves, myPosition);
            addBishopMoves(board, moves, myPosition);
        }
        else if (type == PieceType.KING) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null));
        }
        return moves;
    }

    public void addPawnMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition endPosition) {
        moves.add(new ChessMove(myPosition, endPosition, null));
        if (endPosition.getRow() == 1 || endPosition.getRow() == 8) {
            moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
        }
    }

    public void addRookMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition) {
        for (int r = myPosition.getRow() + 1; r < 9; r++) {
            ChessPosition pos = new ChessPosition(r, myPosition.getColumn());
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int r = myPosition.getRow() - 1; r > 0; r--) {
            ChessPosition pos = new ChessPosition(r, myPosition.getColumn());
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int c = myPosition.getColumn() + 1; c < 9; c++) {
            ChessPosition pos = new ChessPosition(myPosition.getRow(), c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int c = myPosition.getColumn() - 1; c > 0; c--) {
            ChessPosition pos = new ChessPosition(myPosition.getRow(), c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
    }
    public boolean addMoveInLine(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition pos) {
        if (board.getPiece(pos) != null) {
            if (board.getPiece(pos).getTeamColor() != this.getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            return true;
        }
        else {
            moves.add(new ChessMove(myPosition, pos, null));
        }
        return false;
    }

    public void addBishopMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition) {
        for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() + 1; r < 9 && c < 9; c++, r++) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r < 9 && c > 0; c--, r++) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r > 0 && c < 9; c++, r--) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
        for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r > 0 && c > 0; c--, r--) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                break;
            }
        }
//        for (int r = myPosition.getRow() - 1; r > 0; r--) {
//            if (addMoveInDiagonal(board, moves, myPosition, r)) {
//                break;
//            }
//        }
        for (ChessMove c : moves) {
            System.out.printf("{%d, %d}\n", c.endPosition.getRow(), c.endPosition.getColumn());
        }
    }

    public boolean addMoveInDiagonal(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, int r) {
        for (int c = myPosition.getColumn() + 1; c < 9; c++) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                return true;
            }
        }
        for (int c = myPosition.getColumn() - 1; c > 0; c--) {
            ChessPosition pos = new ChessPosition(r, c);
            if (addMoveInLine(board, moves, myPosition, pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
