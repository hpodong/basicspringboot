package com.basicspringboot.filters.models.logs;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "inflow_log",
        primaryKey = "ifl_idx",
        createdAt = "ifl_crdt"
)
public class InflowLog extends _BSModel {

    @BSColumn(name = "ifl_idx")
    private Long idx;

    @BSColumn(name = "ifl_session_id")
    private String session_id;

    @BSColumn(name = "ifl_os")
    private String os;

    @BSColumn(name = "ifl_keyword")
    private String keyword;

    @BSColumn(name = "ifl_ip")
    private String ip;

    @BSColumn(name = "ifl_path")
    private String path;

    @BSColumn(name = "ifl_crdt")
    private Timestamp created_at;

    public InflowLog(HttpServletRequest request) {
        final String userAgent = request.getHeader("User-Agent");
        final String referer = request.getHeader("Referer");
        final URI uri = URI.create(referer);
        final HttpSession session = request.getSession();

        this.keyword = this.getKeyword(uri);
        this.path = referer;
        this.session_id = session.getId();
        this.os = extractOsFromUserAgent(userAgent);
        this.ip = request.getRemoteAddr();
    }

    public InflowLog(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    private String extractOsFromUserAgent(String userAgent) {
        String os = "unknown";
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

    private String getKeyword(URI uri) {
        String parameter = null;

        final String query = uri.getQuery();
        final String host = uri.getHost();

        if(query != null) {
            final String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                final String key = keyValue[0];
                final String value = keyValue[1];
                if(host.equals("search.naver.com")) {
                    if(key.equals("query")) {
                        parameter = value;
                        break;
                    }
                } else {
                    if(key.equals("q")) {
                        parameter = value;
                        break;
                    }
                }
            }
            return parameter;
        } else {
            return null;
        }
    }
}
