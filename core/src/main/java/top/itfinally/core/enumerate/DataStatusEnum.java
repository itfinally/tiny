package top.itfinally.core.enumerate;

public enum DataStatusEnum {
    NORMAL( 1, "正常" ),
    DELETE( -1, "已逻辑删除" );

    private int status;
    private String name;

    DataStatusEnum( int status, String name ) {
        this.status = status;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public DataStatusEnum setStatus( int status ) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataStatusEnum setName( String name ) {
        this.name = name;
        return this;
    }

    public boolean expect( int... status ) {
        for ( int item : status ) {
            if ( item != this.status ) return false;
        }

        return true;
    }
}
