package com.basicspringboot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class AligoAuthNumber {
    private final String cell;
    private final String authNumber;
    private final LocalDateTime expiredAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // 같은 참조
        if (obj == null || getClass() != obj.getClass()) return false; // null 또는 다른 클래스

        AligoAuthNumber other = (AligoAuthNumber) obj;
        return cell != null && cell.equals(other.cell);
    }
}
