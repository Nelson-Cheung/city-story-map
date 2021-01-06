package edu.sysu.citystorymap.editstory;

import java.util.List;

import edu.sysu.citystorymap.base.BasePresenter;
import edu.sysu.citystorymap.base.BaseView;

public interface EditStoryContract {
     interface View extends BaseView<Presenter> {
         void setWholeStory(String text);
         void setSummary(String text);
         void setTag();
     }

     interface Presenter extends BasePresenter {
         void upload(List<String> allSrcAndHref, String wholeStory, String summary, String tags, Result result);
     }

     interface Result {
         void onSuccess();
         void onError();
     }
}
