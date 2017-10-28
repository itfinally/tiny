package top.itfinally.security.repository.po;

import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

public abstract class UserDetailsEntity<Entity extends UserDetailsEntity> extends BaseEntity<Entity> {

    public UserDetailsEntity() {
    }

    public UserDetailsEntity( String id ) {
        super( id );
    }

    public abstract String getAccount();

    public abstract String getPassword();

    public abstract String getNickname();

    public abstract String getAuthorityId();

    public abstract Entity setAuthorityId( String authorityId );

    public static class Default extends UserDetailsEntity<Default> {
        private String account;
        private String password;
        private String nickname;
        private String authorityId;

        public Default() {
        }

        public Default( String id ) {
            super( id );
        }

        @Override
        public String getAccount() {
            return account;
        }

        public Default setAccount( String account ) {
            this.account = account;
            return this;
        }

        @Override
        public String getPassword() {
            return password;
        }

        public Default setPassword( String password ) {
            this.password = password;
            return this;
        }

        @Override
        public String getNickname() {
            return nickname;
        }

        public Default setNickname( String nickname ) {
            this.nickname = nickname;
            return this;
        }

        @Override
        public String getAuthorityId() {
            return authorityId;
        }

        @Override
        public Default setAuthorityId( String authorityId ) {
            this.authorityId = authorityId;
            return this;
        }

        @Override
        public String toString() {
            return "AbstractForbiddenHandler{" +
                    "id='" + id + '\'' +
                    ", status=" + status +
                    ", createTime=" + createTime +
                    ", updateTime=" + updateTime +
                    ", deleteTime=" + deleteTime +
                    ", account='" + account + '\'' +
                    ", password='" + password + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", authorityId='" + authorityId + '\'' +
                    '}';
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            if ( !super.equals( o ) ) return false;
            Default aDefault = ( Default ) o;
            return Objects.equals( account, aDefault.account ) &&
                    Objects.equals( password, aDefault.password ) &&
                    Objects.equals( nickname, aDefault.nickname ) &&
                    Objects.equals( authorityId, aDefault.authorityId );
        }

        @Override
        public int hashCode() {
            return Objects.hash( super.hashCode(), account, password, nickname, authorityId );
        }
    }
}
