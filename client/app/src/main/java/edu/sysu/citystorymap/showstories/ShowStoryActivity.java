package edu.sysu.citystorymap.showstories;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.model.StoryModel;
import edu.sysu.citystorymap.model.UserModel;
import edu.sysu.citystorymap.myposition.MyPosition;


public class ShowStoryActivity extends AppCompatActivity {
    public static final int ACCOUNT = 0;
    public static final int LOCATION = 1;
    public static final int TAGS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_story);

        getSupportActionBar().hide();

        ShowStorySummaryFragment summaryView = new ShowStorySummaryFragment();
        ShowCompleteStoryFragment completeView = new ShowCompleteStoryFragment();
        StoryModel storyModel = new StoryModel();
        UserModel userModel = UserModel.getInstance();
        ShowStoryPresenter presenter = new ShowStoryPresenter(this, R.id.container, summaryView, completeView, storyModel, userModel);

        int type = getIntent().getIntExtra("type", -1);

        // ACCOUNT 显示特定用户的故事
        // LOCATION 显示某个地点的故事
        // TAGS 显示某个标签的故事
        if (type == ACCOUNT) {
            String account = getIntent().getStringExtra("account");
            storyModel.setMethod(StoryModel.ACCOUNT);
            storyModel.setStoryList(storyModel.getStoryWithAccount(account));
        } else if(type == LOCATION) {
            double longitude, latitude;
            latitude = getIntent().getDoubleExtra("latitude", 0);
            longitude = getIntent().getDoubleExtra("longitude", 0);
            MyPosition position = new MyPosition();
            position.setLongitude(longitude);
            position.setLatitude(latitude);
            storyModel.setMethod(StoryModel.LOCATION);
            storyModel.setStoryList(storyModel.getStoriesWithLocation(position));
        } else if(type == TAGS) {
            String tags = getIntent().getStringExtra("tags");
            storyModel.setMethod(StoryModel.TAGS);
            storyModel.setStoryList(storyModel.getStoryWithTags(tags));
        } else {
            finish();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, summaryView)
                .commit();
    }
}
