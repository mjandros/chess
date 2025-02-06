package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor turn;
    ChessBoard board;
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        setBoard(new ChessBoard());
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        List<ChessMove> possibleMoves = (List<ChessMove>) piece.pieceMoves(board, startPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor enemyTeam = TeamColor.WHITE;
        if (piece.getTeamColor() == TeamColor.WHITE) {
            enemyTeam = TeamColor.BLACK;
        }
        for (ChessMove cm : possibleMoves) {
            ChessBoard saveBoard = new ChessBoard(board);
            ChessBoard tempBoard = new ChessBoard(saveBoard);
            setBoard(tempBoard);
            validMoves.add(cm);
            //System.out.printf("start: {%d, %d}; end: {%d, %d}\n", cm.getStartPosition().getRow(), cm.getStartPosition().getColumn(), cm.getEndPosition().getRow(), cm.getEndPosition().getColumn());
            board.addPiece(startPosition, null);
            board.addPiece(cm.getEndPosition(), piece);
            int kingPosR = 1;
            int kingPosC = 1;
            for (int r = 1; r < 9; r++) {
                for (int c = 1; c < 9; c++) {
                    ChessPosition pos = new ChessPosition(r, c);
                    if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != enemyTeam && board.getPiece(pos).getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosR = r;
                        kingPosC = c;
                    }
                }
            }
            ChessPosition kingPos = new ChessPosition(kingPosR, kingPosC);
            //System.out.printf("kingPos: {%d, %d}\n", kingPosR, kingPosC);
            for (int r = 1; r < 9; r++) {
                for (int c = 1; c < 9; c++) {
                    ChessPosition pos = new ChessPosition(r, c);
                    if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == enemyTeam) {
                        ChessPiece p = board.getPiece(pos);
                        //System.out.printf("Checking %s %s at {%d, %d}\n", p.getTeamColor(), p.getPieceType(), r, c);
                        List<ChessMove> moves = (List<ChessMove>) p.pieceMoves(tempBoard, pos);
                        for (ChessMove move : moves) {
                            //System.out.printf("Checking {%d, %d}\n", move.getEndPosition().getRow(), move.getEndPosition().getColumn());
                            if (move.getEndPosition().equals(kingPos)) {
                                //System.out.println("Move is invalid. Removing.");
                                validMoves.remove(cm);
                                break;
                            }
                        }
                    }
                }
            }
            setBoard(saveBoard);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        List<ChessMove> validMoves = (List<ChessMove>) validMoves(move.getStartPosition());
        if (validMoves == null) {
            throw new InvalidMoveException();
        }
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (getTeamTurn() != piece.getTeamColor()) {
            throw new InvalidMoveException();
        }
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), piece);
        }
        else {
            board.addPiece(move.getEndPosition(), new ChessPiece(turn, move.getPromotionPiece()));
        }
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
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
