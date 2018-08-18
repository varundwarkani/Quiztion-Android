package dwarsoft.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static dwarsoft.learning.QuizLoading.CATPREF;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardAdapterHolder> {

    private Context context;
    private ArrayList<LeaderboardModel> categories1;
    int i = 0;
    int j = 0;


    public LeaderboardAdapter(Context context, ArrayList<LeaderboardModel> categories1) {
        this.categories1 = categories1;
        this.context = context;

    }

    @Override
    public LeaderboardAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_leaderboard,parent,false);

        LeaderboardAdapterHolder categoryAdapterHolder = new LeaderboardAdapterHolder(v);

        return categoryAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LeaderboardAdapter.LeaderboardAdapterHolder holder, final int position) {
        final LeaderboardModel model = categories1.get(position);
        holder.tvLeaderboardName.setText(model.getName());
        holder.tvLeaderboardScore.setText(model.getScore());
    }

    @Override
    public int getItemCount() {
        return categories1.size();
    }

    class LeaderboardAdapterHolder extends RecyclerView.ViewHolder{

        private TextView tvLeaderboardScore,tvLeaderboardName;
        private View view;
        RelativeLayout rlleaderboard;

        public LeaderboardAdapterHolder(View itemView) {
            super(itemView);
            view = itemView;
            rlleaderboard = itemView.findViewById(R.id.rlleaderboard);
            tvLeaderboardScore = itemView.findViewById(R.id.tvLeaderboardScore);
            tvLeaderboardName = itemView.findViewById(R.id.tvLeaderboardName);
        }
    }
}