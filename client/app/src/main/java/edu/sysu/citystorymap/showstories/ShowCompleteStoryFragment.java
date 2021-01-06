package edu.sysu.citystorymap.showstories;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.editor.common.DownloadTask;
import edu.sysu.citystorymap.editor.view.RichEditorNew;
import edu.sysu.citystorymap.user.User;
import edu.sysu.citystorymap.userstory.StoryComment;
import edu.sysu.citystorymap.userstory.UserStory;
import edu.sysu.citystorymap.utils.TimeUtils;

public class ShowCompleteStoryFragment extends Fragment implements ShowStoryContract.ShowCompleteStoryView {
    private ShowStoryContract.Presenter mPresenter;
    private ImageView back;
    private ImageView refresh;
    private TextView name;
    private TextView time;
    private TextView tags;
    private RichEditorNew wholeStory;
    private TextView thumbUpNumber;
    private ImageView thumbUp;
    private ImageView comment;
    private ListView commentList;
    private RichEditorNew commentBox;

    private ScrollView scrollView;

    /**
     * scrollview嵌套listview显示不全解决
     *
     * @param
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight * 2
                + (listView.getDividerHeight() * listAdapter.getCount());
        listView.setLayoutParams(params);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_complete_story_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getStory();
        mPresenter.getStoryComment();
    }

    private void init(View view) {
        back = view.findViewById(R.id.back);
        refresh = view.findViewById(R.id.refresh);
        name = view.findViewById(R.id.name);
        time = view.findViewById(R.id.time);
        tags = view.findViewById(R.id.tags);
        wholeStory = view.findViewById(R.id.whole_story);
        thumbUp = view.findViewById(R.id.thumb_up);
        thumbUpNumber = view.findViewById(R.id.thumb_up_number);
        comment = view.findViewById(R.id.comment);
        commentList = view.findViewById(R.id.comment_list);

        wholeStory.setTextColor(R.color.colorBlack);
        wholeStory.setFontSize(3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showWholeStoryReturn();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.refreshStory();
            }
        });

        thumbUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.giveThumbUp();
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialog dialog = new CommentDialog(getActivity());
                dialog.show();
                dialog.setOnCommentDialogPublishListener(new CommentDialogPublishListener() {
                    @Override
                    public void onPublish(String content) {
                        if (!content.isEmpty()) {
                            mPresenter.addComment(content);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void showStory(UserStory story) {
        name.setText(story.getName());
        time.setText(TimeUtils.toTimeString(Long.parseLong(story.getLastEditTime())));
        tags.setText(story.getTags());

        wholeStory.loadRichEditorCode(story.getWholeStory());
        wholeStory.setOnClickImageTagListener(new RichEditorNew.OnClickImageTagListener() {
            @Override
            public void onClick(String url) {
                //Toast.makeText(getActivity(), "url:" + url, Toast.LENGTH_LONG).show();
            }
        });

        wholeStory.setDownloadListener(DownloadTask.getDefaultDownloadListener(getActivity()));

        if (story.getThumbUps() >= 1000) {
            int num = story.getThumbUps();
            num = num / 1000;
            thumbUpNumber.setText(num + "k");
        } else {
            thumbUpNumber.setText(story.getThumbUps() + "");
        }
    }

    @Override
    public void showUser(User user) {

    }

    @Override
    public void showPosition() {

    }

    @Override
    public void showComment(List<StoryComment> list) {
        StoryCommentAdapter adapter = new StoryCommentAdapter(list);
        commentList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(commentList);
    }

    @Override
    public void setPresenter(ShowStoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private interface CommentDialogPublishListener {
        void onPublish(String content);
    }

    private class StoryCommentAdapter extends BaseAdapter {
        private List<StoryComment> list;

        public StoryCommentAdapter(List<StoryComment> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public StoryComment getItem(int position) {
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
                view = View.inflate(getActivity(), R.layout.story_comment, null);
            } else {
                view = convertView;
            }

            TextView name = view.findViewById(R.id.name);
            TextView time = view.findViewById(R.id.time);
            RichEditorNew comment = view.findViewById(R.id.comment);

            StoryComment storyComment = list.get(position);

            name.setText(storyComment.getAccount());
            long timeValue = Long.parseLong(storyComment.getTime());
            time.setText(TimeUtils.toTimeString(timeValue));
            comment.loadRichEditorCode(storyComment.getContent());
            comment.setOnClickImageTagListener(new RichEditorNew.OnClickImageTagListener() {
                @Override
                public void onClick(String url) {
                    //Toast.makeText(getActivity(), "url:" + url, Toast.LENGTH_LONG).show();
                }
            });

            comment.setDownloadListener(DownloadTask.getDefaultDownloadListener(getActivity()));
            return view;
        }
    }

    private class CommentDialog extends Dialog {
        private Context mContext;
        private ImageView cancel;
        private ImageView publish;
        private RichEditorNew commentBox;
        private CommentDialogPublishListener publishListener;

        public CommentDialog(@NonNull Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.comment_input_box);
            init();
        }

        private void init() {
            cancel = findViewById(R.id.cancel);
            publish = findViewById(R.id.pulish);
            commentBox = findViewById(R.id.comment_box);

            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentDialog.this.dismiss();
                    if (publishListener != null) {
                        publishListener.onPublish(commentBox.getHtml());
                    }
                    commentBox.setHtml("");
                    hide();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
        }

        public void setOnCommentDialogPublishListener(CommentDialogPublishListener listener) {
            publishListener = listener;
        }
    }
}
