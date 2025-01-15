package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

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
        if (type == PieceType.PAWN) {
            /* adjust this to accommodate both teams (+1 and -1) */ ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(forwardOne) == null) {
                moves.add(new ChessMove(myPosition, forwardOne, null));
                if (forwardOne.getRow() == 0 || forwardOne.getRow() == 7) {
                    //promote to whatever
                }
            }
            if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 1) || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 6)){
                //forwardTwo
            }
        }
        return moves;
    }
}
