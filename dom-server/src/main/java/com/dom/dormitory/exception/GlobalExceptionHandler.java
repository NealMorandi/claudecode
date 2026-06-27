package com.dom.dormitory.exception;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * グローバル例外ハンドラー
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 業務例外 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("業務例外: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /** 認証失敗 */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentials(BadCredentialsException e) {
        return Result.error(401, MessageConstant.LOGIN_FAILED);
    }

    /** アカウント無効 */
    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleDisabled(DisabledException e) {
        return Result.error(401, MessageConstant.ACCOUNT_DISABLED);
    }

    /** 権限なし */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.error(403, MessageConstant.FORBIDDEN);
    }

    /** ファイルサイズ超過 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return Result.error(MessageConstant.FILE_TOO_LARGE);
    }

    /** パラメータ不正 */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("パラメータエラー: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /** その他のシステム例外 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("システムエラー: ", e);
        return Result.error(MessageConstant.SERVER_ERROR);
    }
}
