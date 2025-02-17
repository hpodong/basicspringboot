package com.basicspringboot.models.others;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@NoArgsConstructor
@BSTable(
        name = "app_version",
        primaryKey = "av_idx",
        createdAt = "av_crdt",
        updatedAt = "av_updt",
        deletedAt = "av_dldt",
        status = "av_status"
)
public class AppVersion extends _BSModel {

    @BSColumn(name = "av_idx")
    private Long idx;

    @BSValidation(label = "메인 버전", min = 1, max = 11)
    @BSColumn(name = "av_major")
    private Integer major;

    @BSValidation(label = "서브 버전", min = 1, max = 11)
    @BSColumn(name = "av_minor")
    private Integer minor;

    @BSValidation(label = "업데이트 버전", min = 1, max = 11)
    @BSColumn(name = "av_fetch")
    private Integer fetch;

    @BSValidation(label = "빌드 번호", min = 1, max = 11)
    @BSColumn(name = "av_build_number")
    private Integer build_number;

    @BSValidation(label = "상태", min = 1)
    @BSColumn(name = "av_status", reqName = "version_status")
    private String status;

    @BSValidation(label = "디바이스 종류", min = 1)
    @BSColumn(name = "av_os")
    private String os;

    @BSColumn(name = "av_desc")
    private String description;

    @BSColumn(name = "av_crdt")
    private Timestamp created_at;

    @BSColumn(name = "av_updt")
    private Timestamp updated_at;

    @BSColumn(name = "av_dldt")
    private Timestamp deleted_at;

    public AppVersion(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public AppVersion(HttpServletRequest request) {
        super(request);
    }

    public AppVersion(Integer offset, long count, ResultSet rs, int rowNum) {
        super(offset, count, rs, rowNum);
    }

    public boolean isHigh(AppVersion appVersion) {
        return build_number > appVersion.build_number;
    }

    public boolean isSame(AppVersion appVersion) {
        return build_number.equals(appVersion.build_number);
    }

    public String versionToString() {
        return major + "." + minor + "." + fetch;
    }

    public String osToString() {
        return switch (os) {
            case "A" -> "AOS";
            case "I" -> "IOS";
            default -> "";
        };
    }

    public String statusToString() {
        return switch (status) {
            case "pass" -> "업데이트 진행 안함";
            case "update" -> "강제 업데이트";
            case "push" -> "업데이트 알림만 확인";
            case "inspection" -> "현재 서버 점검중";
            default -> "";
        };
    }
}
