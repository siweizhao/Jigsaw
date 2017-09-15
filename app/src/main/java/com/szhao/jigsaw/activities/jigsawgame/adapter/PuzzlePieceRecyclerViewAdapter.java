package com.szhao.jigsaw.activities.jigsawgame.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.jigsawgame.jigsaw.PuzzlePiece;
import com.szhao.jigsaw.activities.jigsawgame.jigsaw.PuzzlePieceDragShadowBuilder;
import com.szhao.jigsaw.activities.jigsawgame.vh.ImageViewHolder;
import com.szhao.jigsaw.global.GlobalGameData;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 7/25/2017.
 */

public class PuzzlePieceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<PuzzlePiece> puzzlePieces;
    ArrayList<PuzzlePiece> sidePieces;
    ArrayList<PuzzlePiece> allPuzzlePieces;
    boolean isShowingSidePieces = false;
    private Context mContext;

    public  PuzzlePieceRecyclerViewAdapter(Context context, ArrayList<PuzzlePiece> puzzlePieces){
        mContext = context;
        this.puzzlePieces = puzzlePieces;
        Collections.shuffle(puzzlePieces);
        allPuzzlePieces = puzzlePieces;
        initSidePieces();
    }

    private void initSidePieces(){
        sidePieces = new ArrayList<>();
        for (PuzzlePiece p : puzzlePieces){
            if (p.isSidePiece())
                sidePieces.add(p);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cv_image_only, parent, false);
        final ImageViewHolder vh = new ImageViewHolder(mContext,v);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = vh.getAdapterPosition();
                GlobalGameData.getInstance().setSelectedPuzzlePiece(puzzlePieces.get(position));
                Bitmap puzzleImage = GlobalGameData.getInstance().getSelectedPuzzlePiece().getImage();
                PuzzlePieceDragShadowBuilder dragShadowBuilder = new PuzzlePieceDragShadowBuilder(mContext, puzzleImage);
                v.startDrag(null, dragShadowBuilder, null, 0);
                removeItem(position);
                return false;
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
        ImageViewHolder vh = (ImageViewHolder)holder;
        vh.setPuzzlePieceImage(puzzlePieces.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return puzzlePieces.size();
    }

    public void insertPuzzlePiece(int position){
        PuzzlePiece insertedPiece = GlobalGameData.getInstance().getSelectedPuzzlePiece();
        insertedPiece.setCurrentPos(PuzzlePiece.UNPLACED_PIECE);

        if (isShowingSidePieces){
            if (insertedPiece.isSidePiece()) {
                puzzlePieces.add(position, insertedPiece);
                notifyItemInserted(position);
            }
            allPuzzlePieces.add(position, insertedPiece);
        } else {
            if (insertedPiece.isSidePiece()) {
                if (position > sidePieces.size()) {
                    sidePieces.add(sidePieces.size() - 1, insertedPiece);
                } else {
                    sidePieces.add(position, insertedPiece);
                }
            }
            puzzlePieces.add(position, insertedPiece);
            notifyItemInserted(position);
        }
    }

    public void removeItem(int position){
        removePuzzlePiece(puzzlePieces.get(position));
        notifyItemRemoved(position);
    }

    private void removePuzzlePiece(PuzzlePiece p){
        puzzlePieces.remove(p);
        allPuzzlePieces.remove(p);
        if (p.isSidePiece())
            sidePieces.remove(p);
    }

    public void showSidePieces(){
        if (!isShowingSidePieces){
            puzzlePieces = sidePieces;
            notifyDataSetChanged();
        } else {
            puzzlePieces = allPuzzlePieces;
            notifyDataSetChanged();
        }
        isShowingSidePieces = !isShowingSidePieces;
    }

    public ArrayList<PuzzlePiece> getSavedPuzzlePieces(Point[] correctPos, Point[] currPos){
        ArrayList<PuzzlePiece> savedPieces = new ArrayList<>();
        for (int i = 0; i < correctPos.length; i++) {
            for (PuzzlePiece p : puzzlePieces) {
                if (p.getCorrectPos().equals(correctPos[i].x,correctPos[i].y)) {
                    p.setCurrentPos(currPos[i]);
                    savedPieces.add(p);
                }
            }
        }
        for (PuzzlePiece p : savedPieces){
            removePuzzlePiece(p);
        }
        notifyDataSetChanged();
        return savedPieces;
    }
}
