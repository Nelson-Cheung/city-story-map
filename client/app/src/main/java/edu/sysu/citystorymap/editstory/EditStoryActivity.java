package edu.sysu.citystorymap.editstory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.model.StoryModel;
import edu.sysu.citystorymap.model.UserModel;
import edu.sysu.citystorymap.myposition.MyPosition;
import edu.sysu.citystorymap.userstory.UserStory;

public class EditStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        getSupportActionBar().hide();
        StoryModel model = new StoryModel();
        int type = getIntent().getIntExtra("type", -1);

        if(type == UserStory.INSERT) {
            double latitude = getIntent().getDoubleExtra("latitude",0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);
            MyPosition position = new MyPosition(latitude, longitude);

            model.getStory().setAccount(UserModel.getInstance().getUser().getAccount());
            model.getStory().setPosition(position);
            model.getStory().setType(type);
        } else if (type == UserStory.UPDATE) {
            UserStory story = (UserStory)getIntent().getSerializableExtra("story");
            story.setType(type);
            model.setStory(story);
        } else {
            finish();
        }

        EditStoryFragment view = new EditStoryFragment();
        EditStoryPresenter presenter = new EditStoryPresenter(view, model);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.edit_story_container, view)
                .commit();
    }
}
