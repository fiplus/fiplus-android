package utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Nick on 1/1/2015.
 */
public class ListViewUtil {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        if (listAdapter.getCount() == 0)
        {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount() && i < 3; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);

            //BAD CODING STYLE!
            //However, for some reason, the height of the items in
            //the suggestion list is really really big. I'm
            //not sure how that's happening so I'm just
            //hardcoding this for now.
            totalHeight += 144; //listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}