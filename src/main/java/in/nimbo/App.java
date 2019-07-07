package in.nimbo;

import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDao;
import in.nimbo.entity.News;

public class App {
    private NewsDao newsDao;
    private ChannelDao channelDao;

    public App(NewsDao newsDao, ChannelDao channelDao) {
        this.newsDao = newsDao;
        this.channelDao = channelDao;
    }

    public void addLink(String url){

    }

    public News[] getNews(FilterNews filter){
        return newsDao.search(filter);
    }
}
