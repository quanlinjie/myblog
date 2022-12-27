package com.study.myblog.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class UtilPost {

    public static String getContentWithoutImg(String content) {
        Document doc = Jsoup.parse(content);

        Elements els = doc.select("img");
        for (Element el : els) {
            el.remove();
        }
        return doc.select("body").text();
    }

}
