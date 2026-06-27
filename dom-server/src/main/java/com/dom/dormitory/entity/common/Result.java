package com.dom.dormitory.entity.common;

import lombok.Data;

/**
 * 統一レスポンス結果クラス
 */
@Data
public class Result<T> {

    /** ステータスコード: 200=成功 */
    private Integer code;

    /** レスポンスメッセージ */
    private String message;

    /** レスポンスデータ */
    private T data;

    /** 成功レスポンス（データなし） */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** 成功レスポンス（データあり） */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /** 成功レスポンス（データ＋メッセージ） */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /** エラーレスポンス */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /** エラーレスポンス（コード指定） */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
