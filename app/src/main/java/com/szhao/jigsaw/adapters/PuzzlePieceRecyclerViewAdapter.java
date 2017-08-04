package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.puzzle.GlobalGameData;
import com.szhao.jigsaw.puzzle.PuzzlePiece;
import com.szhao.jigsaw.puzzle.PuzzlePieceDragShadowBuilder;
import com.szhao.jigsaw.vh.ImageViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 7/25/2017.
 */

public class PuzzlePieceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    ArrayList<PuzzlePiece> puzzlePieces;
    ArrayList<PuzzlePiece> sidePieces;
    ArrayList<PuzzlePiece> allPuzzlePieces;

    boolean isShowingSidePieces = false;

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
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    int position = vh.getAdapterPosition();
                    GlobalGameData.getInstance().setSelectedPuzzlePiece(puzzlePieces.get(position));
                    Bitmap puzzleImage = GlobalGameData.getInstance().getSelectedPuzzlePiece().getImage();
                    PuzzlePieceDragShadowBuilder dragShadowBuilder = new PuzzlePieceDragShadowBuilder(mContext, puzzleImage);
                    v.startDrag(null,dragShadowBuilder,null,0);
                    removeItem(position);
                }
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
        Log.d("b4 insert", " "+ puzzlePieces.size() + " " + allPuzzlePieces.size() + " " + sidePieces.size());

        if (isShowingSidePieces){
            if (insertedPiece.isSidePiece()) {
                puzzlePieces.add(position, insertedPiece);
                notifyItemInserted(position);
            }
            allPuzzlePieces.add(position, insertedPiece);
        } else {
            if (insertedPiece.isSidePiece()) {
                sidePieces.add(position, insertedPiece);
            }
            puzzlePieces.add(position, insertedPiece);
            notifyItemInserted(position);
        }

        Log.d("insert done", " "+ puzzlePieces.size() + " " + allPuzzlePieces.size() + " " + sidePieces.size());
    }

    public void removeItem(int position){
        PuzzlePiece removedPiece = puzzlePieces.get(position);
        puzzlePieces.remove(removedPiece);
        allPuzzlePieces.remove(removedPiece);
        if (removedPiece.isSidePiece())
            sidePieces.remove(removedPiece);
        notifyItemRemoved(position);
        Log.d("remove", " "+ puzzlePieces.size() + " " + allPuzzlePieces.size() + " " + sidePieces.size());

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

}
