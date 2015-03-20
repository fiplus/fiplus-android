package model;

public class SuggestionListItem {

    private String mSuggestionId;
    private String mSuggestion;
    private int mVoteCount;
    private boolean mYesVote;

    public SuggestionListItem(){}

    public SuggestionListItem(String suggestionId, String suggestion, int voteCount, boolean yesVote){
        this.mSuggestionId = suggestionId;
        this.mSuggestion = suggestion;
        this.mVoteCount = voteCount;
        this.mYesVote = yesVote;
    }

    public String getSuggestion() {
        return this.mSuggestion;
    }

    public String getSuggestionId() {
        return this.mSuggestionId;
    }

    public boolean getYesVote() { return this.mYesVote; }

    public int getVote(){
        return this.mVoteCount;
    }

    public void setSuggestion(String s) {
        mSuggestion = s;
    }

    public void setSuggestionId(String s) {
        mSuggestionId = s;
    }

    public void setYesVote(boolean b) { mYesVote = b; }

    public void setVote(int n){
        mVoteCount = n;
    }
}
