package model;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersListItem {
    private String mFavouriteUser;
    private int mUserPic;


    public FavouriteUsersListItem(){}

    public FavouriteUsersListItem(String mFavouriteUser, int mUserPic){
            this.mFavouriteUser = mFavouriteUser;
            this.mUserPic = mUserPic;
            }

    public String getFavouriteUser() {
        return this.mFavouriteUser;
    }

    public int getUserPic() {
        return this.mUserPic;
    }
}