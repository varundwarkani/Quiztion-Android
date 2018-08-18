package dwarsoft.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static dwarsoft.learning.QuizLoading.CATPREF;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizAdapterHolder> {

    private Context context;
    private ArrayList<QuizModel> categories1;
    int i = 0;
    int j = 0;


    public QuizAdapter(Context context, ArrayList<QuizModel> categories1) {
        this.categories1 = categories1;
        this.context = context;

        SharedPreferences categoriesPref = context.getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = categoriesPref.edit();
        editor.putString("selectedquiz","0");
        editor.commit();
    }

    @Override
    public QuizAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_quizlist,parent,false);

        QuizAdapterHolder categoryAdapterHolder = new QuizAdapterHolder(v);

        return categoryAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final QuizAdapter.QuizAdapterHolder holder, final int position) {
        final QuizModel model = categories1.get(position);
        holder.tvQuizName.setText(model.getText());
        
        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.isSelected())
                {
                    SharedPreferences categoriesPref = context.getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = categoriesPref.edit();
                    editor.putString("selectedquiz",holder.tvQuizName.getText().toString());
                    editor.commit();
                    holder.view.setBackgroundColor(model.isSelected() ? Color.WHITE : Color.CYAN);
                    model.setSelected(false);
                    i = 0;
                }
                else
                {
                    if (i==1)
                    {
                        Toast.makeText(context, "Please un-select the already selected quiz.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        SharedPreferences categoriesPref = context.getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = categoriesPref.edit();
                        editor.putString("selectedquiz",holder.tvQuizName.getText().toString());
                        editor.commit();
                        holder.view.setBackgroundColor(model.isSelected() ? Color.WHITE : Color.CYAN);
                        model.setSelected(true);
                        i = 1;
                    }
                }

            }
        });
        
    }

    @Override
    public int getItemCount() {
        return categories1.size();
    }

    class QuizAdapterHolder extends RecyclerView.ViewHolder{

        private TextView tvQuizName;
        private View view;
        RelativeLayout category;

        public QuizAdapterHolder(View itemView) {
            super(itemView);
            view = itemView;
            category = itemView.findViewById(R.id.category);
            tvQuizName = itemView.findViewById(R.id.tvQuizName);
            category = itemView.findViewById(R.id.category);

        }
    }
}

