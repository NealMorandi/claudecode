package com.dom.dormitory.constant;

/**
 * メッセージ定数クラス
 */
public class MessageConstant {

    public static final String LOGIN_SUCCESS     = "ログインしました";
    public static final String LOGOUT_SUCCESS    = "ログアウトしました";
    public static final String GET_SUCCESS       = "取得成功";
    public static final String CREATE_SUCCESS    = "登録しました";
    public static final String UPDATE_SUCCESS    = "更新しました";
    public static final String DELETE_SUCCESS    = "削除しました";
    public static final String CALCULATE_SUCCESS = "計算完了";
    public static final String CONFIRM_SUCCESS   = "確定しました";
    public static final String CHECKOUT_SUCCESS        = "退寮処理が完了しました";
    public static final String CHECKOUT_DATE_INVALID   = "退寮日は入居日以降の日付を指定してください";
    public static final String UPLOAD_SUCCESS    = "アップロードしました";
    public static final String VALIDATE_SUCCESS  = "検証完了";
    public static final String IMPORT_SUCCESS    = "インポートが完了しました";
    public static final String PASSWORD_CHANGED  = "パスワードを変更しました";
    public static final String LOGIN_FAILED          = "ユーザー名またはパスワードが正しくありません";
    public static final String ACCOUNT_DISABLED      = "アカウントが無効です";
    public static final String TOKEN_INVALID         = "認証トークンが無効です";
    public static final String TOKEN_EXPIRED         = "セッションが切れました";
    public static final String NOT_FOUND             = "データが見つかりません";
    public static final String DUPLICATE_ENTRY       = "データが重複しています";
    public static final String ROOM_OCCUPIED         = "指定した部屋はすでに入居中です";
    public static final String RESIDENCE_NOT_ACTIVE  = "有効な入居記録が見つかりません";
    public static final String RENT_ALREADY_CONFIRMED = "すでに確定済みの寮費があります";
    public static final String EMPLOYEE_HAS_RESIDENCE = "この社員は現在入居中のため削除できません";
    public static final String DORMITORY_HAS_RESIDENTS = "この寮には現在入居者がいるため削除できません";
    public static final String ROOM_HAS_RESIDENTS          = "この部屋には現在入居者がいるため削除できません";
    public static final String ROOM_CAPACITY_BELOW_RESIDENTS = "定員を現在の入居者数より少なく設定することはできません";
    public static final String PARAM_INVALID         = "パラメータが不正です";
    public static final String SERVER_ERROR          = "サーバーエラーが発生しました";
    public static final String FORBIDDEN             = "権限がありません";
    public static final String FILE_TOO_LARGE        = "ファイルサイズが制限を超えています（最大20MB）";

    private MessageConstant() {}
}
