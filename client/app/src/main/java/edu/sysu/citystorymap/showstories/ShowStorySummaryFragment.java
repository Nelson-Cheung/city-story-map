package edu.sysu.citystorymap.showstories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.userstory.UserStory;
import edu.sysu.citystorymap.utils.TimeUtils;


public class ShowStorySummaryFragment extends Fragment implements ShowStoryContract.ShowStorySummaryView {
    private ShowStoryContract.Presenter mPresenter;
    private ListView listView;
    private ImageView back;
    private ImageView refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_story_summary_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getStoryList();
    }

    private void init(View view) {
        listView = view.findViewById(R.id.story_list);
        back = view.findViewById(R.id.back);
        refresh = view.findViewById(R.id.refresh);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showStorySummaryReturn();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.refreshStoryList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.startShowWholeStory(position);
            }
        });
    }

    @Override
    public void showStoryList(List<UserStory> stories) {
        StorySummaryAdapter adapter = new StorySummaryAdapter(stories);
        listView.setAdapter(adapter);
    }


    @Override
    public void setPresenter(ShowStoryContract.Presenter presenter) {
        mPresenter = presenter;
    }


    private class StorySummaryAdapter extends BaseAdapter {
        List<UserStory> list;

        public StorySummaryAdapter(List<UserStory> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public UserStory getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.story_summary_card, null);
            } else {
                view = convertView;
            }

            TextView name = view.findViewById(R.id.name);
            TextView address = view.findViewById(R.id.address);
            TextView time = view.findViewById(R.id.time);
            TextView tags = view.findViewById(R.id.tags);
            TextView summary = view.findViewById(R.id.summary);

            UserStory story = list.get(position);

            name.setText(story.getName());
            tags.setText(story.getTags());
            summary.setText(story.getSummary());
            long timeValue = Long.parseLong(story.getLastEditTime());
            time.setText(TimeUtils.toTimeString(timeValue));

            return view;
        }
    }
}
