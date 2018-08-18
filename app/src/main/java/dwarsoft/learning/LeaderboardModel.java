package dwarsoft.learning;

public class LeaderboardModel {

    private String name,score;

    public LeaderboardModel(String name, String score){
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }
}
