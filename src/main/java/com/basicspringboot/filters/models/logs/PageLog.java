package com.basicspringboot.filters.models.logs;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(
        name = "inflow_page_log",
        primaryKey = "ifpl_idx",
        createdAt = "ifpl_crdt"
)
@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
public class PageLog extends _BSModel {

    @BSColumn(name = "ifpl_idx")
    private Long idx;

    @BSColumn(name = "ifpl_url")
    private String url;

    @BSColumn(name = "ifpl_ip")
    private String ip;

    @BSColumn(name = "ifpl_os")
    private String os;

    @BSColumn(name = "ifpl_browser")
    private String browser;

    @BSColumn(name = "ifpl_useragent")
    private String useragent;

    @BSColumn(name = "ifpl_crdt")
    private Timestamp created_at;

    public PageLog(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public PageLog(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public PageLog(HttpServletRequest request) {
        this(request, null);
    }

    public PageLog(HttpServletRequest request, String url) {
        final String userAgent = request.getHeader("User-Agent");

        if(url != null) this.url = url;
        else this.url = request.getRequestURL().toString();

        this.ip = request.getRemoteAddr();
        this.useragent = userAgentFromUserAgent(userAgent);
        this.os = extractOsFromUserAgent(userAgent);
        this.browser = extractBrowserFromUserAgent(userAgent);
    }

    private String extractOsFromUserAgent(String userAgent) {
        String os = "Unknown";
        if (userAgent.toLowerCase().contains("windows")) {
            os = "window";
        } else if (userAgent.toLowerCase().contains("mac")) {
            os = "mac";
        } else if (userAgent.toLowerCase().contains("linux")) {
            os = "linux";
        } else if (userAgent.toLowerCase().contains("ubuntu")) {
            os = "ubuntu";
        }
        return os;
    }

    private String extractBrowserFromUserAgent(String userAgent) {
        String browser = "Unknown";
        if (userAgent.toLowerCase().contains("safari")) {
            browser = "Apple Safari";
        } else if (userAgent.toLowerCase().contains("chrome")) {
            browser = "Google Chrome";
        } else if (userAgent.toLowerCase().contains("edge")) {
            browser = "Microsoft Edge";
        } else if (userAgent.toLowerCase().contains("Trident") || userAgent.toLowerCase().contains("MSIE")) {
            browser = "Internet Explorer";
        } else if (userAgent.toLowerCase().contains("opera")) {
            browser = "Opera";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            browser = "Brave";
        } else if (userAgent.toLowerCase().contains("vivaldi")) {
            browser = "Vivaldi";
        } else if (userAgent.toLowerCase().contains("ucbrowser")) {
            browser = "UC Browser";
        } else if (userAgent.toLowerCase().contains("samsungbrowser")) {
            browser = "Samsung Internet";
        } else if (userAgent.toLowerCase().contains("maxthon")) {
            browser = "Maxthon";
        } else if (userAgent.toLowerCase().contains("yabrowser")) {
            browser = "Yandex Browser";
        } else if (userAgent.toLowerCase().contains("sleipnir")) {
            browser = "Sleipnir";
        } else if (userAgent.toLowerCase().contains("tor")) {
            browser = "Tor Browser";
        }
        return browser;
    }

    private String userAgentFromUserAgent(String userAgent) {
        userAgent = userAgent.toLowerCase();
        return userAgent.contains("mobile") || userAgent.contains("android") ||
                userAgent.contains("iphone") || userAgent.contains("ipad") ||
                userAgent.contains("windows phone") ? "M" : "PC";
    }
}
