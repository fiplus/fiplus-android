package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Fiplus.R;
import com.Fiplus.ViewProfileActivity;

import java.util.ArrayList;

import adapters.EventListAdapter;
import adapters.FavouriteUsersListAdapter;
import model.FavouriteUsersListItem;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentPeople extends Fragment {

    public static final String TAG = FragmentEvents.class.getSimpleName();

    private ListView mPeopleList;
    private FavouriteUsersListAdapter mFavoriteUserListAdapter ;

    public FragmentPeople() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mPeopleList = (ListView) v.findViewById(R.id.eventsList);

        mPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO: Add item click listener for view profile list
                Intent intent = new Intent(getActivity().getBaseContext(), ViewProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
            }
        });

        setPeopleList();

        return v;
    }


    private void setPeopleList() {
        ArrayList<FavouriteUsersListItem> FavouriteUsersList = new ArrayList<FavouriteUsersListItem>();

        //FavouriteUsersList.add(new FavouriteUsersListItem("John Doe", R.drawable.fiplus, "1111"));
        //FavouriteUsersList.add(new FavouriteUsersListItem("Xerxes", R.drawable.fiplus, "2222"));

        mFavoriteUserListAdapter = new FavouriteUsersListAdapter(getActivity(), FavouriteUsersList);
        mPeopleList.setAdapter(mFavoriteUserListAdapter);

    }
}
