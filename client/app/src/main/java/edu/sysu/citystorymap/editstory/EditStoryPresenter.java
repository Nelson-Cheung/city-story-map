package edu.sysu.citystorymap.editstory;

import java.util.List;

import edu.sysu.citystorymap.model.StoryModel;

public class EditStoryPresenter implements EditStoryContract.Presenter {
    private EditStoryFragment mEditStoryFragment;
    private StoryModel mStoryModel;

    public EditStoryPresenter(EditStoryFragment editStoryFragment, StoryModel storyModel) {
        mEditStoryFragment = editStoryFragment;
        mStoryModel = storyModel;
        mEditStoryFragment.setPresenter(this);
    }

    @Override
    public void upload(List<String> allSrcAndHref, String wholeStory, String summary, String tags, EditStoryContract.Result result) {
        mStoryModel.upload(allSrcAndHref, wholeStory, summary, tags);
    }

    @Override
    public void start() {
        mEditStoryFragment.setWholeStory(mStoryModel.getStory().getWholeStory());
        mEditStoryFragment.setSummary(mStoryModel.getStory().getSummary());
    }
}
