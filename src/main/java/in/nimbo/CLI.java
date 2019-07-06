package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Scanner;

public class CLI {
    private static void addLink(String url) { // add or update
        DBOperations.RSSRead(url);
        System.out.println("done.");
    }

    private static void searchTitle(String title) { // print the result
        for (SyndEntry entry: DBOperations.searchTitle(title))
            System.out.println(entry.getDescription().getValue());
    }

    private static void search(String s) { // print the result
        for (SyndEntry entry: DBOperations.search(s))
            System.out.println(entry.getDescription().getValue());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("<1 link> for add a link to you links");
        System.out.println("<2 title> for search on titles in you extracted news");
        System.out.println("<3 s> for search in you extracted news");
        System.out.println("<4> for exit");
        int type = scanner.nextInt();
        String s = scanner.nextLine();
        while (true) {
            if (type == 1)
                addLink(s);
            else if (type == 2)
                searchTitle(s);
            else if (type == 3)
                search(s);
            else
                break;
        }
    }
}
