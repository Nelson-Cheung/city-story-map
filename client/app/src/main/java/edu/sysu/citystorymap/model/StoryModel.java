package edu.sysu.citystorymap.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sysu.citystorymap.editor.common.EssFile;
import edu.sysu.citystorymap.myposition.MyPosition;
import edu.sysu.citystorymap.userstory.StoryComment;
import edu.sysu.citystorymap.userstory.UserStory;
import edu.sysu.citystorymap.utils.HttpUtils;
import edu.sysu.citystorymap.utils.StreamUtils;


public class StoryModel {
    public static int ACCOUNT = 0;
    public static int LOCATION = 1;
    public static int TAGS = 2;

    // 获取故事的方法
    private int method;
    private String account;
    private MyPosition location;
    private String tags;
    private UserStory story;
    private List<UserStory> storyList;

    private final String POST_FILE_PATH = "post_file";
    private final String UPDATE_PATH = "update_story";
    private final String GET_STORY_PATH = "get_story";
    private final String INCREASE_STORY_ATRRIBUTE_PATH = "increase_story_attribute";


    public StoryModel() {
        story = new UserStory();
        method = -1;
        location = new MyPosition();
        tags = "";
        storyList = new ArrayList<UserStory>();
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public void giveThumbUp(String account) {
        try {
            GiveThumbUpTask task = new GiveThumbUpTask();
            task.reader = account;
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addComment(StoryComment comment) {
        try {
            AddCommentTask task = new AddCommentTask();
            task.comment = comment;
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //故事转换
    private UserStory toUserStory(HashMap<String, Object> map) {
        UserStory story = new UserStory();

        String account = (String) map.get("account");
        String summary = (String) map.get("summary");
        String wholeStory = (String) map.get("whole_story");
        int thumbUps = (int) map.get("thumb_ups");
        String tags = (String) map.get("tags");
        List<StoryComment> comments = (List<StoryComment>) map.get("comments");
        int lastTime = (int) map.get("last_time");
        int newTime = (int) map.get("new_time");

        MyPosition position = new MyPosition();
        position.setLongitude(Double.parseDouble(map.get("longitude").toString()));
        position.setLatitude(Double.parseDouble(map.get("latitude").toString()));

        story.setAccount(account);
        story.setSummary(summary);
        story.setWholeStory(wholeStory);
        story.setThumbUps(thumbUps);
        //story.setLastEditTime(lastTime);
        // story.setNewEditTime(newTime);
        story.setTags(tags);
        story.setPosition(position);
        story.setComments(comments);

        return story;
    }

    //查找用户故事
    public List<UserStory> getStoryWithAccount(String account) {
        this.account = account;
        try {
            GetStoryWithAccountThread thread = new GetStoryWithAccountThread();
            thread.account = account;
            thread.start();
            thread.join();
            return thread.list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<UserStory>();
//        List<UserStory> list = new ArrayList<UserStory>();
//
//        UserStory story = new UserStory();
//        StoryComment comment1 = new StoryComment();
//        StoryComment comment2 = new StoryComment();
//        StoryComment comment3 = new StoryComment();
//
//        comment1.setAccount("324242");
//        comment1.setContent("comment 1 comment 1");
//        comment1.setTime("1590217474815");
//
//        comment2.setAccount("436526436");
//        comment2.setContent("comment 2 comment 2");
//        comment2.setTime("1590217475815");
//
//        comment3.setAccount("54453");
//        comment3.setContent("comment 3 comment 3");
//        comment3.setTime("1590217674815");
//
//        List<StoryComment> comments = new ArrayList<StoryComment>();
//        comments.add(comment1);
//        comments.add(comment2);
//        comments.add(comment3);
//
//        story.setAccount("34234234");
//        story.setName("sdfsdf");
//        story.setLastEditTime("1590217474815");
//        story.setSummary("test1");
//        story.setWholeStory("This test1 😀");
//        story.setTags("test1 test1 test1");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("65437");
//        story.setName("dfhgshherhg");
//        story.setLastEditTime("1590217474615");
//        story.setSummary("test2");
//        story.setWholeStory("go on test 🐂");
//        story.setTags("test2 test2");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("95345345");
//        story.setName("sdhearh");
//        story.setLastEditTime("1590217574815");
//        story.setSummary("test3");
//        story.setWholeStory("😄 last test exmaple");
//        story.setTags("final test");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        return list;
    }

    //查找在特定地点的故事
    public List<UserStory> getStoriesWithLocation(MyPosition position) {
        location = position;
        try {
            GetStoriesWithLocationThread thread = new GetStoriesWithLocationThread();
            thread.position = position;
            thread.start();
            thread.join();
            return thread.list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<UserStory>();
//        List<UserStory> list = new ArrayList<UserStory>();
//
//        UserStory story = new UserStory();
//        StoryComment comment1 = new StoryComment();
//        StoryComment comment2 = new StoryComment();
//        StoryComment comment3 = new StoryComment();
//
//        comment1.setAccount("324242");
//        comment1.setContent("comment 1 comment 1");
//        comment1.setTime("1590217474815");
//
//        comment2.setAccount("436526436");
//        comment2.setContent("comment 2 comment 2");
//        comment2.setTime("1590217475815");
//
//        comment3.setAccount("54453");
//        comment3.setContent("comment 3 comment 3");
//        comment3.setTime("1590217674815");
//
//        List<StoryComment> comments = new ArrayList<StoryComment>();
//        comments.add(comment1);
//        comments.add(comment2);
//        comments.add(comment3);
//
//        story.setAccount("34234234");
//        story.setName("sdfsdf");
//        story.setLastEditTime("1590217474815");
//        story.setSummary("test1");
//        story.setWholeStory("This test1 😀");
//        story.setTags("test1 test1 test1");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("65437");
//        story.setName("dfhgshherhg");
//        story.setLastEditTime("1590217474615");
//        story.setSummary("test2");
//        story.setWholeStory("go on test 🐂");
//        story.setTags("test2 test2");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("95345345");
//        story.setName("sdhearh");
//        story.setLastEditTime("1590217574815");
//        story.setSummary("test3");
//        story.setWholeStory("😄 last test exmaple");
//        story.setTags("final test");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        return list;
    }

    //查找特定标签的故事
    public List<UserStory> getStoryWithTags(String tags) {
        this.tags = tags;

        try {
            GetStoryWithTagsThread thread = new GetStoryWithTagsThread();
            thread.tags = tags;
            thread.start();
            thread.join();
            return thread.list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<UserStory>();
//        List<UserStory> list = new ArrayList<UserStory>();
//
//        UserStory story = new UserStory();
//        StoryComment comment1 = new StoryComment();
//        StoryComment comment2 = new StoryComment();
//        StoryComment comment3 = new StoryComment();
//
//        comment1.setAccount("324242");
//        comment1.setContent("comment 1 comment 1");
//        comment1.setTime("1590217474815");
//
//        comment2.setAccount("436526436");
//        comment2.setContent("comment 2 comment 2");
//        comment2.setTime("1590217475815");
//
//        comment3.setAccount("54453");
//        comment3.setContent("comment 3 comment 3");
//        comment3.setTime("1590217674815");
//
//        List<StoryComment> comments = new ArrayList<StoryComment>();
//        comments.add(comment1);
//        comments.add(comment2);
//        comments.add(comment3);
//
//        story.setAccount("34234234");
//        story.setName("sdfsdf");
//        story.setLastEditTime("1590217474815");
//        story.setSummary("test1");
//        story.setWholeStory("This test1 😀");
//        story.setTags("test1 test1 test1");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("65437");
//        story.setName("dfhgshherhg");
//        story.setLastEditTime("1590217474615");
//        story.setSummary("test2");
//        story.setWholeStory("go on test 🐂");
//        story.setTags("test2 test2");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        story = new UserStory();
//        story.setAccount("95345345");
//        story.setName("sdhearh");
//        story.setLastEditTime("1590217574815");
//        story.setSummary("test3");
//        story.setWholeStory("😄 last test exmaple");
//        story.setTags("final test");
//        story.setThumbUps(12300);
//        story.setComments(comments);
//
//        list.add(story);
//
//        return list;
    }

    public void upload(List<String> allSrcAndHref, String wholeStory, String summary, String tags) {
        try {
            UpdateTask task = new UpdateTask(allSrcAndHref, wholeStory, summary, tags);
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public UserStory getStory() {
        return story;
    }

    public void setStory(UserStory story) {
        this.story = story;
    }

    public List<UserStory> getStoryList() {
        return storyList;
    }

    public void setStoryList(List<UserStory> storyList) {
        this.storyList = storyList;
    }

    public void refreshStoryList() {
        if (method == ACCOUNT) {
            storyList = getStoryWithAccount(account);
        } else if (method == LOCATION) {
            storyList = getStoriesWithLocation(location);
        } else if (method == TAGS) {
            storyList = getStoryWithTags(tags);
        }
    }

    public void refreshStory() {
        try {
            RefreshStoryTask task = new RefreshStoryTask();
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<MyPosition> getStoryWithBound(MyPosition p1, MyPosition p2) {
        try {
            GetStoryWithBoundThread thread = new GetStoryWithBoundThread();
            thread.p1 = p1;
            thread.p2 = p2;
            thread.start();
            thread.join();
            return thread.list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<MyPosition>();
    }

    private interface UpdateResult {
        void onSuccess();

        void onError();
    }

    private class RefreshStoryTask extends Thread {
        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("action_flag", "story");
            param.put("id", story.getId());
            JSONObject ans = HttpUtils.post(GET_STORY_PATH, param);
            if (!ans.containsKey("story")) {
                return;
            }
            story.fromJSONObject(ans.getJSONObject("story"));
        }
    }

    private class GiveThumbUpTask extends Thread {
        private String reader;

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("attribute", "thumb_up");
            param.put("reader", reader);
            param.put("id", story.getId().toString());

            JSONObject ans = HttpUtils.post(INCREASE_STORY_ATRRIBUTE_PATH, param);
            if (!ans.containsKey("result")) {
                return;
            }

            int result = Integer.parseInt(ans.get("result").toString());
            if (result == 0) {
                story.setThumbUps(story.getThumbUps() + 1);
            }
        }
    }

    private class AddCommentTask extends Thread {
        private StoryComment comment;

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("attribute", "comment");
            param.put("comment", comment.toJSONObject());
            param.put("id", story.getId().toString());

            JSONObject ans = HttpUtils.post(INCREASE_STORY_ATRRIBUTE_PATH, param);
            if (!ans.containsKey("result")) {
                return;
            }

            int res = Integer.parseInt(ans.get("result").toString());

            if (res == 0) {
                return;
                //do something
            } else if (res == 2) {
                //do something
            } else if (res == 1) {
                //do something
            }
        }
    }

    private class GetStoryWithAccountThread extends Thread {
        private String account;
        private List<UserStory> list;

        @Override
        public void run() {
            list = new ArrayList<UserStory>();
            JSONObject param = new JSONObject();
            param.put("action_flag", "account");
            param.put("account", account);
            JSONObject ans = HttpUtils.post(GET_STORY_PATH, param);

            if (!ans.containsKey("stories")) return;

            JSONArray array = (JSONArray) ans.get("stories");
            for (int i = 0; i < array.size(); ++i) {
                UserStory story = new UserStory();
                story.fromJSONObject(array.getJSONObject(i));
                list.add(story);
            }
        }
    }

    private class GetStoriesWithLocationThread extends Thread {
        private MyPosition position;
        private List<UserStory> list;

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("action_flag", "location");
            param.put("location", position.toJSONObject());
            list = new ArrayList<UserStory>();
            JSONObject ans = HttpUtils.post(GET_STORY_PATH, param);

            if (!ans.containsKey("stories")) return;

            JSONArray array = (JSONArray) ans.get("stories");
            for (int i = 0; i < array.size(); ++i) {
                UserStory story = new UserStory();
                story.fromJSONObject(array.getJSONObject(i));
                list.add(story);
            }
        }
    }

    private class GetStoryWithTagsThread extends Thread {
        private String tags;
        private List<UserStory> list;

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("action_flag", "tags");
            param.put("tags", tags);
            list = new ArrayList<UserStory>();
            JSONObject ans = HttpUtils.post(GET_STORY_PATH, param);

            if (!ans.containsKey("stories")) return;

            JSONArray array = (JSONArray) ans.get("stories");
            for (int i = 0; i < array.size(); ++i) {
                UserStory story = new UserStory();
                story.fromJSONObject(array.getJSONObject(i));
                list.add(story);
            }
        }
    }

    private class UpdateTask extends Thread {
        private List<String> allSrc;
        private String src;
        private String wholeStory;
        private String summary;
        private String tags;

        UpdateTask(List<String> allSrcAndHref, String wholeStory, String summary, String tags) {
            this.allSrc = allSrcAndHref;
            this.wholeStory = wholeStory;
            this.summary = summary;
            this.tags = tags;
        }

        @Override
        public void run() {
            try {
                //故事中的资源上传到服务器转换url
                for (int index = 0; index < allSrc.size(); ++index) {
                    src = allSrc.get(index);
                    EssFile essFile = new EssFile(src);
                    File file = new File(essFile.getAbsolutePath());
                    InputStream in = new FileInputStream(file);

                    byte[] temp = StreamUtils.inputStreamToBytes(in);
                    //String转回byte[]可能有问题
                    String content = new String(temp, "ISO-8859-1");

                    JSONObject param = new JSONObject();
                    param.put("extension", essFile.getExtension(essFile.getAbsolutePath()));
                    param.put("file", content);

                    JSONObject ans = HttpUtils.post(POST_FILE_PATH, param);
                    //鲁棒性
                    if (ans.isEmpty()) return;

                    String address = (String) ans.get("address");
                    wholeStory = wholeStory.replaceAll(src, address);
                }

                story.setSummary(summary);
                story.setWholeStory(wholeStory);
                story.setTags(tags);

                JSONObject param = new JSONObject();
                param.put("story", story.toJSONObject());
                JSONObject ans = HttpUtils.post(UPDATE_PATH, param);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private class GetStoryWithBoundThread extends Thread {
        private MyPosition p1, p2;
        private List<MyPosition> list;

        public GetStoryWithBoundThread() {
            p1 = new MyPosition();
            p2 = new MyPosition();
            list = new ArrayList<MyPosition>();
        }

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("action_flag", "bound");
            param.put("p1", p1.toJSONObject());
            param.put("p2", p2.toJSONObject());

            JSONObject ans = HttpUtils.post(GET_STORY_PATH, param);

            if (!ans.containsKey("positions")) return;
            JSONArray array = (JSONArray) ans.get("positions");

            for (int i = 0; i < array.size(); ++i) {
                MyPosition position = new MyPosition();
                position.fromJSONObject(array.getJSONObject(i));
                list.add(position);
            }
        }
    }
}
