package edu.sysu.citystorymap.showstories;


import java.util.List;

import edu.sysu.citystorymap.base.BasePresenter;
import edu.sysu.citystorymap.base.BaseView;
import edu.sysu.citystorymap.user.User;
import edu.sysu.citystorymap.userstory.StoryComment;
import edu.sysu.citystorymap.userstory.UserStory;

public interface ShowStoryContract {
    interface ShowStorySummaryView extends BaseView<Presenter> {
        void showStoryList(List<UserStory> stories);
    }


    interface ShowCompleteStoryView extends BaseView<Presenter> {
        void showStory(UserStory story);
        void showUser(User user);
        void showPosition();
        void showComment(List<StoryComment> list);
    }

    interface Presenter extends BasePresenter {
        void refreshStoryList();
        void refreshStory();
        void getStoryList();
        void addComment(String comment);
        void getStoryAuthor();
        void getStoryPosition();
        void getStoryComment();
        void getStory();
        void giveThumbUp();
        void startShowWholeStory(int position);
        void showWholeStoryReturn();
        void showStorySummaryReturn();
    }
}
