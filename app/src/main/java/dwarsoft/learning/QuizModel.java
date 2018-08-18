package dwarsoft.learning;

public class QuizModel {

    private String text;
    private boolean isSelected = false;

    public QuizModel(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
