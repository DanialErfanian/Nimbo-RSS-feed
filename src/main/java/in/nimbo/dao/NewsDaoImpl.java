package in.nimbo.dao;

import in.nimbo.entity.News;

public class NewsDaoImpl implements NewsDao {

    @Override
    public News[] search(FilterNews filter) {
        return new News[0];
    }

    @Override
    public News getNews(String url) {
        return null;
    }

    @Override
    public void update(News news) {

    }

    @Override
    public void add(News news) {

    }
}
