package com.dom.dormitory.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * ページングレスポンスVO
 */
@Data
@NoArgsConstructor
public class PageVO<T> {

    /** データリスト */
    private List<T> list;

    /** 総件数 */
    private Long total;

    /** 追加サマリー（使用する画面のみセット） */
    private Long pendingCount;
    private Long doneCount;
    private Long confirmedCount;
    private java.math.BigDecimal totalAmount;

    public PageVO(List<T> list, Long total) {
        this.list  = list;
        this.total = total;
    }

    /** 空ページ生成 */
    public static <T> PageVO<T> empty() {
        return new PageVO<>(new ArrayList<>(), 0L);
    }
}
