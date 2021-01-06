package edu.sysu.citystorymap.showstories;


import java.util.List;

import edu.sysu.citystorymap.model.StoryModel;
import edu.sysu.citystorymap.model.UserModel;
import edu.sysu.citystorymap.userstory.StoryComment;
import edu.sysu.citystorymap.userstory.UserStory;

public class ShowStoryPresenter implements ShowStoryContract.Presenter {
    private ShowStoryActivity mContext;
    private ShowStorySummaryFragment mSummaryView;
    private ShowCompleteStoryFragment mCompleteView;
    private StoryModel mStoryModel;
    private UserModel mUserModel;
    private int mContainer;

    public ShowStoryPresenter(ShowStoryActivity context, int container,
                              ShowStorySummaryFragment summaryView, ShowCompleteStoryFragment completeView,
                              StoryModel storyModel, UserModel userModel) {
        this.mContext = context;
        this.mSummaryView = summaryView;
        this.mCompleteView = completeView;
        this.mStoryModel = storyModel;
        this.mUserModel = userModel;
        this.mContainer = container;

        mSummaryView.setPresenter(this);
        mCompleteView.setPresenter(this);
    }

    @Override
    public void refreshStoryList() {
        mStoryModel.refreshStoryList();
        getStoryList();
    }

    @Override
    public void refreshStory() {
        mStoryModel.refreshStory();
        getStory();
        getStoryComment();
    }

    @Override
    public void getStoryList() {
        List<UserStory> list = mStoryModel.getStoryList();
        mSummaryView.showStoryList(list);
    }

    @Override
    public void addComment(String content) {
        StoryComment comment = new StoryComment();
        comment.setAccount(mUserModel.getUser().getAccount());
        comment.setContent(content);

        mStoryModel.addComment(comment);
        getStoryComment();
    }

    @Override
    public void getStoryAuthor() {

    }

    @Override
    public void getStoryPosition() {

    }

    @Override
    public void getStoryComment() {
        mCompleteView.showComment(mStoryModel.getStory().getComments());
    }

    @Override
    public void getStory() {
        mCompleteView.showStory(mStoryModel.getStory());
    }

    @Override
    public void giveThumbUp() {
        mStoryModel.giveThumbUp(mUserModel.getUser().getAccount());
    }

    @Override
    public void startShowWholeStory(int position) {
        List<UserStory> list = mStoryModel.getStoryList();
        mStoryModel.setStory(list.get(position));

        mContext.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(mContainer, mCompleteView)
                .commit();
    }

    @Override
    public void showWholeStoryReturn() {
        mContext.onBackPressed();
    }

    @Override
    public void showStorySummaryReturn() {
        mContext.finish();
    }

    @Override
    public void start() {

    }
}
