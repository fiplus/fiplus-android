package model;

import java.util.List;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersListItem {
    private String mFavouriteUser;
    private int mUserPic;
    private String mUserId;
    private List<String> mTaggedInterests;


    public FavouriteUsersListItem(){}

    public FavouriteUsersListItem(String mFavouriteUser, int mUserPic, String userId, List<String> taggedInterests){
            this.mFavouriteUser = mFavouriteUser;
            this.mUserPic = mUserPic;
            this.mUserId = userId;
            this.mTaggedInterests = taggedInterests;
    }

    public String getFavouriteUser() {
        return this.mFavouriteUser;
    }

    public String getUserId(){return this.mUserId;}

    public List<String> getTaggedInterests() {return this.mTaggedInterests;}

    public int getUserPic() {
        return this.mUserPic;
    }
}