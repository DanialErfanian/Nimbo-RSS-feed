package in.nimbo;

import in.nimbo.dao.ChannelDaoImpl;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDaoImpl;
import in.nimbo.entity.News;

import java.util.Scanner;

public class CLI {
    private static final String INVALID_INPUT = "Invalid Input";
    private App app = new App(new NewsDaoImpl(), new ChannelDaoImpl());
    private static final String help = "add <link> for add a channel\n" +
            "news <filter> to get news with given filter:\n" +
            "news -channel tabnak.ir/rss -title باخت پرسپلیس";

    private String getStatus() {
        return String.format("We have %d RSSChannels. and %d news.",
                app.getAllChannels().size(),
                app.getNews(new FilterNews()).length);
    }

    private String handle(String line) {
        line = line.trim();
        if (line.length() == 0)
            return "";
        if (line.equals("help")) {
            return help;
        }
        if (line.equals("status")) {
            return getStatus();
        }
        String[] split = line.split("\\s+");
        String type = split[0];
        if (!type.matches("add|news")) {
            return INVALID_INPUT;
        }
        if (type.equals("add")) {
            app.addLink(line.substring(type.length() + 1));
            return "Link added successfully";
        } else if (type.equals("news")) {
            FilterNews filter;
            if (line.length() == type.length()) {
                filter = new FilterNews();
            } else {
                filter = Utility.parseNewsFilter(line.substring(type.length() + 1), app);
            }
            if (filter == null)
                return INVALID_INPUT;
            else {
                News[] filteredNews = app.getNews(filter);
                StringBuilder s = new StringBuilder("News found.");
                for (News news : filteredNews)
                    s.append(news);
                return s.toString();
            }
        } else
            return INVALID_INPUT;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(help);
        CLI cli = new CLI();
        while (scanner.hasNextLine())
            System.out.println(cli.handle(scanner.nextLine()));
    }
}
