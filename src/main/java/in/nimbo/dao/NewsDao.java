package in.nimbo.dao;

import in.nimbo.entity.News;

import java.sql.SQLException;

public interface NewsDao {

    News[] search(FilterNews filter);

    News getNews(String url);

    void update(News news);

    void add(News news);
}
