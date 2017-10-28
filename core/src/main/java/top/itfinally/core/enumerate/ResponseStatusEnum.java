package top.itfinally.core.enumerate;

import top.itfinally.core.vo.BaseResponseVoBean;

import javax.validation.constraints.NotNull;

public enum ResponseStatusEnum implements BaseResponseStatus {
    SUCCESS( 200, "请求成功" ),
    EMPTY_RESULT( 204, "请求成功, 但没有数据" ),

    BAD_REQUEST( 400, "用户请求错误或请求缺少参数" ),
    UNAUTHORIZED( 401, "未登录" ),
    TOO_MARY_REQUEST( 429, "当前 ip 或用户请求过多" ),

    // 430 在 aop 异常处理器拦截到 BaseBusinessException 及其子类时使用, 即用户非法操作时使用
    ILLEGAL_REQUEST( 430, "用户非法操作, 暂无更多细节" ),

    SERVER_ERROR( 500, "服务器异常" );

    private String message;
    private int statusCode;

    ResponseStatusEnum( int statusCode, String message ) {
        this.message = message;
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ResponseStatusEnum setMessage( String message ) {
        this.message = message;
        return this;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    public ResponseStatusEnum setStatusCode( int statusCode ) {
        this.statusCode = statusCode;
        return this;
    }

    public static boolean expect( @NotNull BaseResponseVoBean bean, @NotNull BaseResponseStatus... allStatus ) {
        int statusCode = bean.getStatusCode();
        for ( BaseResponseStatus code : allStatus ) {
            if ( code.getStatusCode() == statusCode ) {
                return true;
            }
        }

        return false;
    }
}
