package model;

public class SuggestionListItem {

    private String mSuggestionId;
    private String mSuggestion;
    private int mVote;

    public SuggestionListItem(){}

    public SuggestionListItem(String suggestionId, String suggestion, int vote){
        this.mSuggestionId = suggestionId;
        this.mSuggestion = suggestion;
        this.mVote = vote;
    }

    public String getSuggestion() {
        return this.mSuggestion;
    }

    public String getSuggestionId() {
        return this.mSuggestionId;
    }

    public int getVote(){
        return this.mVote;
    }
}
