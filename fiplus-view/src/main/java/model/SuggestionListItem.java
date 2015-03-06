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
}
