package com.szhao.jigsaw.adapters;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.puzzle.GlobalGameData;
import com.szhao.jigsaw.puzzle.PuzzlePiece;
import com.szhao.jigsaw.vh.PuzzlePieceViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 7/25/2017.
 */

public class PuzzlePieceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    ArrayList<PuzzlePiece> puzzlePieces;


    public  PuzzlePieceRecyclerViewAdapter(Context context, ArrayList<PuzzlePiece> puzzlePieces){
        mContext = context;
        this.puzzlePieces = puzzlePieces;
        Collections.shuffle(puzzlePieces);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.jigsaw_puzzle_pieces, parent, false);
        final PuzzlePieceViewHolder vh = new PuzzlePieceViewHolder(v);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData dragData = ClipData.newPlainText("","");
                removeItem(vh.getAdapterPosition());
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(dragData,dragShadowBuilder,v,0);
                return true;
            }
        });
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PuzzlePieceViewHolder vh = (PuzzlePieceViewHolder)holder;
        vh.setPuzzlePieceImage(mContext, puzzlePieces.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return puzzlePieces.size();
    }

    public void insertPuzzlePiece(int position){
        puzzlePieces.add(position, GlobalGameData.getInstance().getSelectedPuzzlePiece());
        puzzlePieces.get(position).setCurrentPos(PuzzlePiece.UNPLACED_PIECE);
        notifyItemInserted(position);
    }

    public void removeItem(int position){
        GlobalGameData.getInstance().setSelectedPuzzlePiece(puzzlePieces.get(position));
        puzzlePieces.remove(position);
        notifyItemRemoved(position);
    }

}
