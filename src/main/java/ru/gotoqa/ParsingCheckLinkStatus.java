package ru.gotoqa;




import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParsingCheckLinkStatus {

    public static final Logger LOG = LogManager.getLogger(ParsingCheckLinkStatus.class);


    @Test
    public void getStatus() throws IOException {
        //URL url = new URL("https://www.alfastrah.ru/");
        URL url = new URL("https://www.alfastrah.ru/tel:+74957880999/");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int code = connection.getResponseCode();
        System.out.println(code);
    }

    @Test
    public void remove_element_from_array_to_arraylist_java8 () {

        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday"};

        List<String> daysOfWeekAsList = Lists.newArrayList(daysOfWeek);

        boolean removed = daysOfWeekAsList.removeIf(p -> p.equalsIgnoreCase("Monday"));

        Assertions.assertTrue(daysOfWeekAsList.size() == 6);
        System.out.println(daysOfWeekAsList);
    }



    @Test
    public void getLinksListfromUrls() throws IOException {
        File file = new File("D:\\JAVA\\Java_SRC\\CheckLinkStatus\\src\\main\\resources\\urllistalfa.txt");


        List<String> list = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(file)))) {

            //1. filter line 3
            //2. convert all content to upper case
            //3. convert it into a List
            list = stream
                    .filter(line -> !line.startsWith("line3"))
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

        }

        String url = null;
        for (String s : list){
            url = s;
        }

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .timeout(1000*5) //it's in milliseconds, so this means 5 seconds.+
                .ignoreHttpErrors(true)
                .get();
        //Document doc = Jsoup.parse(String.valueOf(new URL(url)));
        Elements inputElements = doc.select("a[href]");

        //Remove duplicates from a List using Java 8 Lambdas
        List<Element> collect = inputElements.stream().distinct().collect(Collectors.toList());

        for (Element elements : collect) {
            String href = elements.attr("href");
            System.out.println(href);
        }


    }




    @Test
    public void getLinkList() throws IOException {
        //https://try.jsoup.org/
        Document doc = Jsoup.parse(new File("D:\\JAVA\\Java_SRC\\CheckLinkStatus\\src\\main\\resources\\alfa.html"), "utf-8");
        Elements inputElements = doc.select("a[href]");

        //Remove duplicates from a List using Java 8 Lambdas
        List<Element> collect = inputElements.stream().distinct().collect(Collectors.toList());

        int x = 0;
        int y1 = 0;
        int y2 = 0;
        int z = 0;
        for (Element elements : collect){

            String href = elements.attr("href");

            if (!href.equals("#top") && !href.equals("#")
                    && !href.equals("/") && !href.equals("") && !href.equals("tel:+74957880999")
                    && !href.equals("javascript:void(0);") && !href.equals("tel:84957880999")){
                x++;

                if (!href.contains("http")) {

                    URL url = new URL("https://www.alfastrah.ru"+href);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int code = connection.getResponseCode();
                    //System.out.println(code);
                    LOG.info(code);

                    if (code == 200) {
                        y1++;
                    }else {
                        z++;
                        //System.out.println("Тут косячок: " +code);
                        LOG.info("Тут косячок: " +code);
                    }

/*                    System.out.println("https://www.alfastrah.ru"+href);
                    System.out.println("-----------------------------------");*/
                    LOG.info("https://www.alfastrah.ru"+href);
                    LOG.info("-----------------------------------");

                }else {

                    URL url = new URL(href);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int code = connection.getResponseCode();
                    //System.out.println(code);
                    LOG.info(code);

                    if (code == 200) {
                        y2++;
                    }

/*                    System.out.println(href);
                    System.out.println("-----------------------------------");*/
                    LOG.info(href);
                    LOG.info("-----------------------------------");

                }

            }

        }

/*        System.out.println("Кол-во линков: " +x);
        System.out.println("Кол-во 200 ответов: " +(y1+y2));
        System.out.println("Кол-во не 200 ответов: " +z);*/

        LOG.info("Кол-во линков: " +x);
        LOG.info("Кол-во 200 ответов: " +(y1+y2));
        LOG.info("Кол-во не 200 ответов: " +z);

    }
}