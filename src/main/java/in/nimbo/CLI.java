package in.nimbo;

import in.nimbo.dao.ChannelDaoImpl;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDaoImpl;
import in.nimbo.entity.News;

import java.util.Scanner;

public class CLI {
    private static final String INVALID_INPUT = "Invalid Input";
    private App app = new App(new NewsDaoImpl(), new ChannelDaoImpl());
    private static final String help = "add <link> for add a channel" +
            "news <filter> to get news with given filter:" +
            "filter " +
            "news -channel tabnak.ir/rss -title باخت پرسپلیس";

    private String handle(String line) {
        if (line.equals("help")) {
            return help;
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
            FilterNews filter = Utility.parseNewsFilter(line.substring(type.length() + 1), app);
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
        CLI cli = new CLI();
        while (scanner.hasNextLine())
            System.out.println(cli.handle(scanner.nextLine()));
    }
}
