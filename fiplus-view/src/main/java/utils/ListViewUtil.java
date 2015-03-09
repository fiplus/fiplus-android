package utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtil {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int temp;
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
            temp = listItem.getMeasuredHeight();
            if(temp > 200 && temp < 2000)
            {
                temp = 100;
            }
            else if(temp > 2000)
            {
                temp = 125;
            }

            totalHeight += temp;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}